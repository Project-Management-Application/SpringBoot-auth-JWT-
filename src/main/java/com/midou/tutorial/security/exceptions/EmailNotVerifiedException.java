package com.midou.tutorial.security.exceptions;

public class EmailNotVerifiedException extends Throwable {
    public EmailNotVerifiedException(String emailNotVerified) {
        super(emailNotVerified);
    }
}
