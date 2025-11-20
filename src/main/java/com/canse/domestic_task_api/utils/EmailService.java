package com.canse.domestic_task_api.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String email) {

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirmer votre inscription");
            helper.setFrom("collectverythings@gmail.com");

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {

            throw new IllegalStateException("Failed to send email !");

        }
    }
}
