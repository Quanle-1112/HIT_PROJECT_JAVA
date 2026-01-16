package org.example.services;
public interface IEmailService {
    boolean sendEmail(String toEmail, String subject, String body);
}