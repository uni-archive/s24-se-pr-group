package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.dto.MailBody;
import jakarta.mail.MessagingException;

public interface EmailSenderService {

    void sendMail(MailBody mailBody);

    void sendHtmlMail(MailBody mailBody) throws MessagingException;
}
