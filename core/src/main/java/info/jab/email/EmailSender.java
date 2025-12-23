package info.jab.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private final String hostname;
    private final int smtpPort;
    private final String user;
    private final Session session;

    public EmailSender(String hostname, int smtpPort, String user, String password) {
        this.hostname = hostname;
        this.smtpPort = smtpPort;
        this.user = user;
        this.session = SessionFactory.createSmtpSession(hostname, smtpPort, user, password);
    }

    public void send(EmailMessage email) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.to()));
        message.setSubject(email.subject());
        message.setText(email.body());

        logger.debug("Sending email to: {}, Subject: {}, SMTP server: {}:{}", email.to(), email.subject(), hostname, smtpPort);
        Transport.send(message);
        logger.info("Email sent successfully to: {}", email.to());
    }
}
