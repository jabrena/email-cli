package info.jab.email;

import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import jakarta.mail.Folder;
import jakarta.mail.Session;
import jakarta.mail.Message;
import java.io.Closeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailStoreConnection implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(EmailStoreConnection.class);

    private final Store store;
    private final Folder inbox;

    public EmailStoreConnection(String hostname, int imapPort, String user, String password, ProtocolConfiguration protocolConfig) throws MessagingException {
        Session session = SessionFactory.createStoreSession(hostname, imapPort, user, password, protocolConfig);
        this.store = session.getStore(protocolConfig.getProtocol().getValue());

        logger.debug("Connecting with explicit configuration:");
        logger.debug("  Protocol: {}", protocolConfig.getProtocol().name());
        logger.debug("  Port: {} (configured, no conversion)", imapPort);
        logger.debug("  SSL: {} (required for port {})", protocolConfig.isUseSSL(), imapPort);

        logger.info("Attempting connection to {} server: {}:{}", protocolConfig.getProtocol().name(), hostname, imapPort);
        logger.debug("Using configured port {} - connection will fail if port is incorrect", imapPort);

        try {
            store.connect(hostname, imapPort, user, password);
            logger.info("Connected successfully to port {}!", imapPort);
        } catch (MessagingException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("ESMTP") || errorMsg.contains("220"))) {
                logger.error("The server responded with an SMTP greeting, but we're trying to use {}.", protocolConfig.getProtocol().name());
                logger.error("Configured EMAIL_IMAP_PORT={} - connection failed as expected.", imapPort);
                logger.error("Please check your EMAIL_IMAP_PORT setting in the .env file.");
                logger.error("Supported IMAP/POP3 ports: 143 (IMAP), 993 (IMAP SSL), 110 (POP3), 995 (POP3 SSL)");
            } else {
                logger.error("Connection failed to port {} as configured.", imapPort);
                logger.error("No automatic port conversion - using exact port from EMAIL_IMAP_PORT={}", imapPort);
            }
            throw e;
        }

        this.inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
    }

    public Message[] getMessages() throws MessagingException {
        return inbox.getMessages();
    }

    public int getMessageCount() throws MessagingException {
        return inbox.getMessageCount();
    }

    @Override
    public void close() {
        try {
            if (inbox != null && inbox.isOpen()) {
                inbox.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
            logger.debug("Connection closed.");
        } catch (MessagingException e) {
            logger.error("Error closing connections: {}", e.getMessage(), e);
        }
    }
}
