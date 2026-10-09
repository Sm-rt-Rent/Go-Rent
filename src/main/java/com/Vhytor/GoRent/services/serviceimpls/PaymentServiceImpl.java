package com.Vhytor.GoRent.services.serviceimpls;

import com.Vhytor.GoRent.exceptions.PaymentInitializationException;
import com.Vhytor.GoRent.exceptions.PaymentVerificationException;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.services.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${paystack.secret.key}")
    private String payStackSecretKeys;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private final String PAYSTACK_INIT_URL = "https://api.paystack.co/transaction/initialize";
    private final String PAYSTACK_VERIFY_URL = "https://api.paystack.co/transaction/verify/";

    private final RestTemplate restTemplate;

    public PaymentServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, String> initializeTransaction(User user, BigDecimal amount, Long homeId) {
        RestTemplate restTemplate = new RestTemplate();
try {
    // ── DEBUG: Print key status (never print the full key in production) ──
    System.out.println("=== PAYSTACK DEBUG ===");
    System.out.println("Secret key loaded: " + (payStackSecretKeys != null && !payStackSecretKeys.isEmpty() ? "YES (starts with: " + payStackSecretKeys.substring(0, Math.min(7, payStackSecretKeys.length())) + "...)" : "NO - KEY IS EMPTY"));
    System.out.println("User email: " + user.getUserEmail());
    System.out.println("Amount (Naira): " + amount);
    System.out.println("Home ID: " + homeId);

    // Paystack expects amount in Kobo (Naira * 100)
    BigDecimal amountInKobo = amount.multiply(new BigDecimal(100));
    System.out.println("Amount (Kobo): " + amountInKobo.toPlainString());

    Map<String, Object> body = new HashMap<>();
    body.put("email", user.getUserEmail());
    body.put("amount", amountInKobo.toString());
//    body.put("callback_url", "http://localhost:8080/api/payments/verify");
    body.put("callback_url",frontendUrl + "/verify");

    // Metadata helps us link the payment back to the specific house
    Map<String, String> metadata = new HashMap<>();
    metadata.put("home_id", homeId.toString());
    body.put("metadata", metadata);

    System.out.println("Request body: " + body);
    System.out.println("Sending request to Paystack...");


    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, buildHeaders());

    // Send request to Paystack
    ResponseEntity<Map> response = restTemplate.postForEntity(PAYSTACK_INIT_URL, entity, Map.class);

    System.out.println("Paystack response status: " + response.getStatusCode());
    System.out.println("Paystack response body: " + response.getBody());
    System.out.println("=== END DEBUG ===");

    if (response.getBody() == null) {
        throw new PaymentInitializationException("Empty response from Paystack");
    }

    // Extract the authorization_url to send to the user
    Map data = (Map) response.getBody().get("data");

    // Read both values directly from Paystack's response — never derive from URL
    String authorizationUrl = data.get("authorization_url").toString();
    String reference = data.get("reference").toString();

    Map<String, String> result = new HashMap<>();
    result.put("authorizationUrl", authorizationUrl);
    result.put("reference", reference);
    return result;


     }catch (HttpClientErrorException ex) {
    // 4xx from Paystack (e.g. 401 Unauthorized = bad API key)
    System.out.println("=== PAYSTACK CLIENT ERROR ===");
    System.out.println("Status: " + ex.getStatusCode());
    System.out.println("Response body: " + ex.getResponseBodyAsString());
    throw new PaymentInitializationException("Paystack rejected the request: " + ex.getResponseBodyAsString(), ex);

    } catch (HttpServerErrorException ex) {
    // 5xx from Paystack
    System.out.println("=== PAYSTACK SERVER ERROR ===");
    System.out.println("Status: " + ex.getStatusCode());
    throw new PaymentInitializationException("Paystack server error", ex);

    } catch (ResourceAccessException ex) {
    // Network/connection failure — cannot reach Paystack at all
    System.out.println("=== PAYSTACK CONNECTION ERROR ===");
    System.out.println("Cannot reach Paystack. Cause: " + ex.getMessage());
    System.out.println("This usually means: no internet, firewall blocking, or SSL issue");
    throw new PaymentInitializationException("Could not reach Paystack", ex);
      }

    }

    @Override
    public boolean verifyTransaction(String reference) {
        // Implementation for Paystack verification endpoint
        // https://api.paystack.co/transaction/verify/:reference
        try {
            String url = PAYSTACK_VERIFY_URL + reference; //+ "?reference="
            System.out.println("=== PAYSTACK VERIFY DEBUG ===");
            System.out.println("Verifying reference: " + reference);
            System.out.println("Calling URL: " + url);

            HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET,entity, Map.class);

            System.out.println("Paystack verify status code: " + response.getStatusCode());
            System.out.println("Paystack verify response body: " + response.getBody());

            if (response.getBody() == null) {
                System.out.println("ERROR: Response body is null");
                throw new PaymentInitializationException("Empty response from Paystack");
            }

            //Paystack returns :{"data": {"status": "success"}}
            Map data = (Map) response.getBody().get("data");
            System.out.println("Data block: " + data);


            String status = data.get("status").toString();
            System.out.println("Transaction status from Paystack: " + status);
            System.out.println("=== END VERIFY DEBUG ===");

            if (!"success".equalsIgnoreCase(status)) {
                System.out.println("Payment not successful. Status was: " + status);
                throw new PaymentVerificationException(reference);
            }

            return true;

        }catch (HttpClientErrorException ex) {
            System.out.println("=== VERIFY CLIENT ERROR ===");
            System.out.println("Status: " + ex.getStatusCode());
            System.out.println("Body: " + ex.getResponseBodyAsString());
            throw new PaymentVerificationException(reference);
        } catch (ResourceAccessException ex) {
            System.out.println("=== VERIFY CONNECTION ERROR ===");
            System.out.println("Cause: " + ex.getMessage());
            throw new PaymentVerificationException(reference);
        }
    }

    /**
     * Builds the common authorization headers required by all Paystack API calls.
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(payStackSecretKeys);
        return headers;
    }
}
