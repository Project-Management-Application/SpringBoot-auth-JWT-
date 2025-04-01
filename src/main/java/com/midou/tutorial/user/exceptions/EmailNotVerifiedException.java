package com.midou.tutorial.user.exceptions;

public class EmailNotVerifiedException extends Throwable {
    public EmailNotVerifiedException(String emailNotVerified) {
        super(emailNotVerified);
    }
}
