package com.example.projectgreenie.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a 6-digit OTP for password reset
     */
    public void sendOtpEmail(String to, String otp) {
        String subject = "Greenie OTP for Password Reset";
        String content = "<p>Hello,</p>"
                + "<p>Your OTP for password reset is:</p>"
                + "<h2 style='color:green;'>" + otp + "</h2>"
                + "<p>It will expire shortly. Please do not share this with anyone.</p>"
                + "<br><p>If you did not request this, just ignore this email.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
