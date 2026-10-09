package com.Vhytor.GoRent.exceptions;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Standard error response body returned by the GlobalExceptionHandler.
 * Every error from the API will have this consistent shape.
 *
 * Example response:
 * {
 *   "timestamp": "2025-06-01T14:32:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Property with ID 5 was not found.",
 *   "path": "/api/homes/5"
 * }
 */
public class ApiErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String message;
    private String path;

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
}