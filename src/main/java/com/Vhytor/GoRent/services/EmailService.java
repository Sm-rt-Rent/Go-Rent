package com.Vhytor.GoRent.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Handles all outbound emails for SmartRent via Brevo SMTP.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a verification email containing a 6-digit code
     * to the user's email address immediately after registration.
     *
     * @param toEmail   The user's email address
     * @param fullName  The user's full name for personalisation
     * @param code      The 6-digit verification code
     */
    public void sendVerificationEmail(String toEmail, String fullName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verify your SmartRent account");
            helper.setText(buildEmailBody(fullName, code), true); // true = HTML

            mailSender.send(message);
            System.out.println("Verification email sent to: " + toEmail);

        } catch (MessagingException e) {
            System.err.println("Messaging error sending to " + toEmail + ": " + e.getMessage());
        } catch (Exception e) {
            // Catches MailSendException, MailAuthenticationException, connection timeouts etc.
            // We log but never crash registration — user can request resend
            System.err.println("Failed to send verification email to " + toEmail + ": " + e.getMessage());
        }
    }

    /**
     * Builds the HTML email body.
     */
    private String buildEmailBody(String fullName, String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8"/>
              <style>
                body { font-family: 'Segoe UI', Arial, sans-serif; background: #f4f4f7; margin: 0; padding: 0; }
                .wrapper { max-width: 520px; margin: 40px auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 24px rgba(0,0,0,0.08); }
                .header { background: #0c0c0f; padding: 32px; text-align: center; }
                .brand { font-size: 28px; font-weight: 700; color: #f5a623; letter-spacing: 1px; }
                .tagline { font-size: 13px; color: #9898aa; margin-top: 4px; }
                .body { padding: 36px 32px; }
                h1 { font-size: 22px; color: #1a1a2e; margin: 0 0 12px; }
                p { font-size: 15px; color: #555; line-height: 1.6; margin: 0 0 20px; }
                .code-box { background: #f9f6f0; border: 2px dashed #f5a623; border-radius: 10px; padding: 24px; text-align: center; margin: 24px 0; }
                .code { font-size: 40px; font-weight: 700; color: #f5a623; letter-spacing: 12px; font-family: 'Courier New', monospace; }
                .code-note { font-size: 12px; color: #999; margin-top: 8px; }
                .footer { background: #f4f4f7; padding: 20px 32px; text-align: center; font-size: 12px; color: #aaa; }
                .warning { font-size: 13px; color: #e05c5c; background: #fff5f5; border-radius: 6px; padding: 10px 14px; }
              </style>
            </head>
            <body>
              <div class="wrapper">
                <div class="header">
                  <div class="brand">SmartRent</div>
                  <div class="tagline">Smart Renting. No Middlemen.</div>
                </div>
                <div class="body">
                  <h1>Welcome, %s! 👋</h1>
                  <p>Thank you for signing up. To activate your account please enter the verification code below:</p>
                  <div class="code-box">
                    <div class="code">%s</div>
                    <div class="code-note">This code expires in <strong>10 minutes</strong></div>
                  </div>
                  <p>If you did not create a SmartRent account, you can safely ignore this email.</p>
                  <div class="warning">⚠ Never share this code with anyone. SmartRent will never ask for it.</div>
                </div>
                <div class="footer">
                  © SmartRent · Smart Home Rentals · This is an automated message, please do not reply.
                </div>
              </div>
            </body>
            </html>
            """.formatted(fullName, code);
    }
}
