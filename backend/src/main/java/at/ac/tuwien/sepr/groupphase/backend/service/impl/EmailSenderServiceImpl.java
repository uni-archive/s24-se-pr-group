package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.dto.MailBody;
import at.ac.tuwien.sepr.groupphase.backend.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender javaMailSender;

    public EmailSenderServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMail(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("***REMOVED***@gmail.com");
        message.setTo(mailBody.toEmail());
        message.setSubject(mailBody.subject());
        message.setText(mailBody.body());
        javaMailSender.send(message);
    }

    @Override
    public void sendHtmlMail(MailBody mailBody) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("***REMOVED***@gmail.com");
        helper.setTo(mailBody.toEmail());
        helper.setSubject(mailBody.subject());
        helper.setText(mailBody.body(), true);

        javaMailSender.send(message);
    }

}

