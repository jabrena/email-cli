package info.jab.email;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.search.SearchTerm;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailClientImpl implements EmailClient {

    private static final Logger logger = LoggerFactory.getLogger(EmailClientImpl.class);

    private final String hostname;
    private final int imapPort;
    private final int smtpPort;
    private final String user;
    private final String password;

    /**
     * Package-private constructor. Use EmailClientBuilder to create instances.
     *
     * @param hostname the hostname of the email server
     * @param imapPort the IMAP port for receiving emails
     * @param smtpPort the SMTP port for sending emails
     * @param user the username for authentication
     * @param password the password for authentication
     */
    EmailClientImpl(String hostname, int imapPort, int smtpPort, String user, String password) {
        this.hostname = hostname;
        this.imapPort = imapPort;
        this.smtpPort = smtpPort;
        this.user = user;
        this.password = password;
    }

    @Override
    public List<String> listFolders() {
        List<String> folderNames = new ArrayList<>();

        try {
            ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(imapPort);
            try (EmailStoreConnection connection = new EmailStoreConnection(hostname, imapPort, user, password, protocolConfig)) {
                Folder[] folders = connection.getFolders();
                logger.info("Total folders found: {}", folders.length);
                for (Folder folder : folders) {
                    String folderName = folder.getFullName();
                    folderNames.add(folderName);
                    logger.info("  - {} ({})", folderName, folder.getType() == Folder.HOLDS_MESSAGES ? "messages" : "container");
                }
            }
        } catch (IllegalArgumentException | MessagingException e) {
            logger.error("Error listing folders: {}", e.getMessage(), e);
            return folderNames;
        }

        return folderNames;
    }

    @Override
    public List<Message> listEmails(String folder, SearchTerm searchTerm) {
        List<Message> messageList = new ArrayList<>();

        try {
            ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(imapPort);
            try (EmailStoreConnection connection = new EmailStoreConnection(hostname, imapPort, user, password, protocolConfig)) {
                Message[] messages = connection.searchMessages(folder, searchTerm);
                if (searchTerm == null) {
                    logger.info("Total emails in {}: {}", folder, messages.length);
                } else {
                    logger.info("Total emails matching search criteria in {}: {}", folder, messages.length);
                }
                for (Message message : messages) {
                    messageList.add(message);
                }
            }
        } catch (IllegalArgumentException | MessagingException e) {
            logger.error("Error listing emails from folder {}: {}", folder, e.getMessage(), e);
            return messageList;
        }

        return messageList;
    }

    @Override
    public boolean send(EmailMessage email) {
        logger.info("Sending email to: {}, Subject: {}", email.to(), email.subject());
        try {
            EmailSender sender = new EmailSender(hostname, smtpPort, user, password);
            sender.send(email);
            logger.info("Email sent successfully to: {}", email.to());
            return true;
        } catch (MessagingException e) {
            logger.error("Error sending email to {}: {}", email.to(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteEmails(String folder, SearchTerm searchTerm) {
        logger.info("Deleting emails from folder: {} matching search criteria", folder);
        try {
            ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(imapPort);
            try (EmailStoreConnection connection = new EmailStoreConnection(hostname, imapPort, user, password, protocolConfig)) {
                Folder emailFolder = connection.getStore().getFolder(folder);
                emailFolder.open(Folder.READ_WRITE);
                try {
                    // Search for messages matching the search term
                    Message[] messagesToDelete = emailFolder.search(searchTerm);
                    logger.info("Found {} emails matching search criteria in folder {}", messagesToDelete.length, folder);

                    if (messagesToDelete.length == 0) {
                        logger.info("No emails found matching search criteria");
                        return false;
                    }

                    // Mark all matching messages as deleted
                    for (Message message : messagesToDelete) {
                        message.setFlag(Flags.Flag.DELETED, true);
                    }

                    // Expunge to permanently remove all deleted messages
                    emailFolder.expunge();
                    logger.info("Successfully deleted {} emails from folder {}", messagesToDelete.length, folder);
                    return true;
                } finally {
                    emailFolder.close(false);
                }
            }
        } catch (IllegalArgumentException | MessagingException e) {
            logger.error("Error deleting emails from folder {}: {}", folder, e.getMessage(), e);
            return false;
        }
    }
}

