package info.jab.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailClient {

    private static final Logger logger = LoggerFactory.getLogger(EmailClient.class);

    private final String hostname;
    private final int imapPort;
    private final int smtpPort;
    private final String user;
    private final String password;

    public EmailClient(String hostname, int imapPort, int smtpPort, String user, String password) {
        this.hostname = hostname;
        this.imapPort = imapPort;
        this.smtpPort = smtpPort;
        this.user = user;
        this.password = password;
    }

    public void listEmails() throws MessagingException {
        ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(imapPort);

        try (EmailStoreConnection connection = new EmailStoreConnection(hostname, imapPort, user, password, protocolConfig)) {
            Message[] messages = connection.getMessages();
            logger.info("Total emails in INBOX: {}", messages.length);
        }
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        logger.info("Sending email");
        EmailSender sender = new EmailSender(hostname, smtpPort, user, password);
        sender.send(to, subject, body);
    }
}
