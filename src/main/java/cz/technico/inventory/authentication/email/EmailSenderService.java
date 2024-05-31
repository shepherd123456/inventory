package cz.technico.inventory.authentication.email;

public interface EmailSenderService {
    void sendEmail(String toEmail, String subject, String body);
}
