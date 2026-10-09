package com.Vhytor.GoRent.exceptions;

public class EmailNotVerifiedException extends GoRentException{
    private final String email;

    public EmailNotVerifiedException(String email) {
        super("EMAIL_NOT_VERIFIED:" + email);
        this.email = email;
    }

    public String getEmail() { return email; }
}
