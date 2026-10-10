package com.Vhytor.GoRent.exceptions;

//import jakarta.el.PropertyNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralized exception handler for the entire SmartRent API.
 *
 * Every exception thrown in any service or controller is caught here
 * and converted into a consistent ApiErrorResponse JSON body with
 * the correct HTTP status code.
 */



@RestControllerAdvice
public class GlobalExceptionHandler {
        // ─── 404 Not Found ────────────────────────────────────────────────────────
        @ExceptionHandler(com.Vhytor.GoRent.exceptions.PropertyNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handlePropertyNotFound(
                com.Vhytor.GoRent.exceptions.PropertyNotFoundException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        }
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleUserNotFound(
                UserNotFoundException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
        }

        // ─── 409 Conflict ─────────────────────────────────────────────────────────
        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(
                UserAlreadyExistsException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
        }

        @ExceptionHandler(PropertyHasViewingException.class)
        public ResponseEntity<ApiErrorResponse> handlePropertyHasViewings(
                PropertyHasViewingException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
        }

        // ─── 401 Unauthorized ─────────────────────────────────────────────────────
        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
                InvalidCredentialsException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
        }

        // ─── 403 Forbidden ────────────────────────────────────────────────────────
        @ExceptionHandler(UnauthorizedAccessException.class)
        public ResponseEntity<ApiErrorResponse> handleUnauthorizedAccess(
                UnauthorizedAccessException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
        }

        @ExceptionHandler(EmailNotVerifiedException.class)
        public ResponseEntity<ApiErrorResponse> handleEmailNotVerified(
                EmailNotVerifiedException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
        }

        // ─── 400 Bad Request ──────────────────────────────────────────────────────
        @ExceptionHandler(InvalidAccessCodeException.class)
        public ResponseEntity<ApiErrorResponse> handleInvalidAccessCode(
                InvalidAccessCodeException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        }

        @ExceptionHandler(PhotoUploadException.class)
        public ResponseEntity<ApiErrorResponse> handlePhotoUpload(
                PhotoUploadException ex, HttpServletRequest request) {
            return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
        }

        // ─── 402 Payment Required ─────────────────────────────────────────────────
        @ExceptionHandler(PaymentVerificationException.class)
        public ResponseEntity<ApiErrorResponse> handlePaymentVerification(
                PaymentVerificationException ex, HttpServletRequest request) {

            return buildResponse(HttpStatus.PAYMENT_REQUIRED, ex.getMessage(), request.getRequestURI());
        }

        // ─── 502 Bad Gateway ──────────────────────────────────────────────────────
        @ExceptionHandler(PaymentInitializationException.class)
        public ResponseEntity<ApiErrorResponse> handlePaymentInitialization(
                PaymentInitializationException ex, HttpServletRequest request) {

            return buildResponse(HttpStatus.BAD_GATEWAY, ex.getMessage(), request.getRequestURI());
        }

        // ─── 500 Internal Server Error (safety net) ───────────────────────────────

        /**
         * Catch-all for any unexpected exception that wasn't handled above.
         * Prevents raw stack traces from leaking to the client.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleGenericException(
                Exception ex, HttpServletRequest request) {

            return buildResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred. Please try again later.",
                    request.getRequestURI()
            );
        }

        // ─── Helper ───────────────────────────────────────────────────────────────

        private ResponseEntity<ApiErrorResponse> buildResponse(
                HttpStatus status, String message, String path) {

            ApiErrorResponse body = new ApiErrorResponse(
                    status.value(),
                    status.getReasonPhrase(),
                    message,
                    path
            );
            return new ResponseEntity<>(body, status);
        }
    }

