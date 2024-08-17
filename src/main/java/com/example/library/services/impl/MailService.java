package com.example.library.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.*;
import javax.mail.internet.*;

public class MailService {

    private final String username = "thinhtran3803@gmail.com";
    private final String password = "qlfd qlhm phmw jjbj";
    private final Properties prop;
    private final ExecutorService executor;

    public MailService(String host) {
        prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", 587);
        prop.put("mail.smtp.ssl.trust", host);

        this.executor = Executors.newSingleThreadExecutor();
    }

    public void sendMail(List<String> to, String subject, String msg) {
        String from = username;
        executor.submit(() -> {
            try {
                Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));

                List<InternetAddress> addresses = new ArrayList<>();
                for (String recipient : to) {
                    try {
                        addresses.add(new InternetAddress(recipient));
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }

                InternetAddress[] addressArray = addresses.toArray(new InternetAddress[0]);
                message.setRecipients(Message.RecipientType.TO, addressArray);

                message.setSubject(subject);

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);

                message.setContent(multipart);

                Transport.send(message);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        MailService mailService = new MailService("smtp.gmail.com");
        mailService.sendMail(List.of("thinhtran383.au@gmail.com"), "Test", "<h1>Test<h1>");
        mailService.shutdown();
    }


}