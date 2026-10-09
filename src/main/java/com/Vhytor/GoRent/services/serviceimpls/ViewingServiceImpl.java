package com.Vhytor.GoRent.services.serviceimpls;

import com.Vhytor.GoRent.exceptions.InvalidAccessCodeException;
import com.Vhytor.GoRent.exceptions.PaymentVerificationException;
import com.Vhytor.GoRent.exceptions.PropertyNotFoundException;
import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.model.ViewingRecord;
import com.Vhytor.GoRent.repositories.HomeRepository;
import com.Vhytor.GoRent.repositories.ViewingRecordRepository;
import com.Vhytor.GoRent.services.PaymentService;
import com.Vhytor.GoRent.services.ViewingService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ViewingServiceImpl implements ViewingService {

    private final HomeRepository homeRepository;
    private final ViewingRecordRepository viewingRecordRepository;
    private final PaymentService paymentService;

    // SecureRandom is thread-safe and cryptographically strong
    private static final SecureRandom secureRandom = new SecureRandom();


    public ViewingServiceImpl(HomeRepository homeRepository, ViewingRecordRepository viewingRecordRepository, PaymentService paymentService) {
        this.homeRepository = homeRepository;
        this.viewingRecordRepository = viewingRecordRepository;
        this.paymentService = paymentService;
    }

    @Override
    public Map<String, Object> processViewingRequest(Long homeId, User user) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new PropertyNotFoundException(homeId));

        // Returns Map with "authorizationUrl" and "reference"
        Map<String, String> paystackData = paymentService.initializeTransaction(
                user, home.getViewingFee(), homeId);

        String authorizationUrl = paystackData.get("authorizationUrl");
        String reference = paystackData.get("reference"); // ← Real reference from Paystack

        savePendingViewingRecord(user, home, reference);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Redirect the user to the authorization URL to complete payment");
        response.put("authorizationUrl", authorizationUrl);
        response.put("reference", reference);

        return response;

//        String accessCode = generateSecureCode();
//
//        // Save the record in the database
//        saveViewingRecord(user, home, accessCode);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Payment Successful");
//        response.put("accessCode", accessCode);
//        response.put("address", home.getAddress());
//
//        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> completeViewingAfterPayment(String reference) {
        //verify with paystack - throws PaymentVerificationException if failed
        paymentService.verifyTransaction(reference);

        //Find the pending record saved in Step 1
        ViewingRecord viewingRecord = viewingRecordRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new PaymentVerificationException(reference));

        //Generate the secure access code now that payment is confirmed
        String accessCode = generateSecureCode();

        // Update the record from PENDING to SUCCESSFUL
        viewingRecord.setAccessCode(accessCode);
        viewingRecord.setStatus("SUCCESSFUL");
        viewingRecord.setPaid(true);
        viewingRecord.setExpiryTime(LocalDateTime.now().plusHours(2));
        viewingRecordRepository.save(viewingRecord);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Payment verified. Here is your viewing access code.");
        response.put("accessCode", accessCode);
        response.put("address", viewingRecord.getHome().getAddress());
        response.put("expiresAt", viewingRecord.getExpiryTime().toString());

        return response;
    }

    @Override
    @Transactional //Ensures the update is saved to MYSQL correctly
    public boolean validateAccessCode(Long homeId, String code) {
        // 1. Find the latest record for this home and code
        // We'll need a custom method in ViewingRepository for this
        Optional<ViewingRecord> recordOpt = viewingRecordRepository.findByHomeHomeIdAndAccessCode(homeId, code)
                .stream()
                .filter(r -> !r.isUsed() && r.getExpiryTime().isAfter(LocalDateTime.now()))
                .findFirst();

        if (recordOpt.isEmpty()) {
            throw new InvalidAccessCodeException();

        }
        // 2. Mark as used so it can't be used again
            ViewingRecord record = recordOpt.get();
            record.setUsed(true);
            viewingRecordRepository.save(record);

            return true;
        }

//        return false;


    private void savePendingViewingRecord(User user, Home home, String reference) {
        ViewingRecord vr = new ViewingRecord();
        vr.setUser(user);
        vr.setHome(home);
        vr.setTransactionReference(reference);
        vr.setStatus("PENDING");
        vr.setPaid(false);// always false casue not confirmed yet
        viewingRecordRepository.save(vr);
    }
    /**
     * Generates a cryptographically secure 6-digit access code.
     */
    private String generateSecureCode() {
        int code = 100000 + ViewingServiceImpl.secureRandom.nextInt(900000); // 6-digit: 100000–999999
        return String.valueOf(code);
    }


}



