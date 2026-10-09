package com.Vhytor.GoRent.exceptions;

public class PhotoUploadException extends GoRentException {
    public PhotoUploadException(String message) {
        super(message);
    }

    public PhotoUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
