package com.midou.tutorial.student;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private  final JavaMailSender mailSender;

    public void emailSenderOtp(String email,String otp,String fullName) {
        String subject = "Email Verification";
        String body = "Hello " + fullName+"This is your Email Verification Code:" + otp;

        sendMail(email, subject, body);
    }



    public void emailSenderForgetPassword(String email,String fullName,String resetUrl) {
        String subject = "Reset Password";
        String body = "Hello " + fullName+"This is your Reset Password link:" + resetUrl;

        sendMail(email, subject, body);
    }


    private void sendMail(String email, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("medbenmaaouia1@gmail.com"); // Set sender email
            helper.setTo(email); // Set recipient
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
