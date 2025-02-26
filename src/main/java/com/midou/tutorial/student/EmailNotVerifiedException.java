package com.midou.tutorial.student;

public class EmailNotVerifiedException extends Throwable {
    public EmailNotVerifiedException(String emailNotVerified) {
        super(emailNotVerified);
    }
}
