package com.midou.tutorial.student.exceptions;

public class EmailNotVerifiedException extends Throwable {
    public EmailNotVerifiedException(String emailNotVerified) {
        super(emailNotVerified);
    }
}
