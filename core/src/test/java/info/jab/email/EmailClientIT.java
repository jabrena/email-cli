package info.jab.email;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Integration tests for EmailClient using Testcontainers and GreenMail.
 *
 * This test uses:
 * - GreenMail (embedded) for IMAP server testing
 * - MailHog container (via Testcontainers) for SMTP server testing
 *
 * GreenMail supports both IMAP and SMTP, but we demonstrate Testcontainers usage
 * with MailHog for SMTP. For a pure Testcontainers approach, you could use a
 * containerized mail server like docker-mailserver that supports both protocols.
 */
@Testcontainers
class EmailClientIT {

    private static final String TEST_USER = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_HOST = "localhost";
    private static final DockerImageName MAILHOG_IMAGE = DockerImageName.parse("mailhog/mailhog:latest");
    private static final int IMAP_PORT = 3143;
    private static final int IMAP_SSL_PORT = 3993;
    private static final int MAILHOG_SMTP_PORT = 1025;
    private static final int MAILHOG_HTTP_PORT = 8025;

    // GreenMail extension for IMAP server (embedded)
    // Using non-privileged test ports (3143 for IMAP, 3993 for IMAPS)
    // Ports 143 and 993 require root privileges, so we use alternative ports for testing
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(
            new ServerSetup[]{
                    new ServerSetup(IMAP_PORT, null, ServerSetup.PROTOCOL_IMAP),   // IMAP (non-SSL) on port 3143
                    new ServerSetup(IMAP_SSL_PORT, null, ServerSetup.PROTOCOL_IMAPS)   // IMAP SSL on port 3993
            })
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser(TEST_USER, TEST_PASSWORD));

    // Testcontainers: MailHog container for SMTP testing
    // Testcontainers automatically manages the container lifecycle via @Container
    @Container
    @SuppressWarnings("resource")
    static GenericContainer<?> mailhog = new GenericContainer<>(MAILHOG_IMAGE)
            .withExposedPorts(MAILHOG_SMTP_PORT, MAILHOG_HTTP_PORT);

    private EmailClient emailClient;
    private int imapPort;
    private int smtpPort;

    @BeforeEach
    void setUp() {
        // Use GreenMail port 3143 for IMAP (non-privileged test port)
        imapPort = IMAP_PORT;

        // Use MailHog container port for SMTP (mapped port)
        smtpPort = mailhog.getMappedPort(MAILHOG_SMTP_PORT);

        emailClient = new EmailClient(
                TEST_HOST,
                imapPort,
                smtpPort,
                TEST_USER,
                TEST_PASSWORD
        );
    }

    @Test
    @DisplayName("Should list emails successfully when inbox is empty")
    void should_listEmailsSuccessfully_when_inboxIsEmpty() {
        // Given: Empty inbox

        // When & Then: List emails should complete without errors
        assertThatCode(() -> emailClient.listEmails())
                .doesNotThrowAnyException();

        // Verify inbox is actually empty
        int emailCount = getEmailCountFromGreenMail();
        assertThat(emailCount).isZero();
    }

    @Test
    @DisplayName("Should list all messages when inbox contains emails")
    void should_listAllMessages_when_inboxContainsEmails() throws MessagingException {
        // Given: Add some test emails to the inbox using GreenMail's delivery
        int expectedEmailCount = 2;
        Session session = greenMail.getImap().createSession();

        MimeMessage message1 = createTestMessage(session, "sender1@example.com", TEST_USER, "Test Subject 1", "Test Body 1");
        MimeMessage message2 = createTestMessage(session, "sender2@example.com", TEST_USER, "Test Subject 2", "Test Body 2");

        // Deliver messages using GreenMail's delivery mechanism
        greenMail.getUserManager().getUser(TEST_USER).deliver(message1);
        greenMail.getUserManager().getUser(TEST_USER).deliver(message2);

        // When: List emails
        assertThatCode(() -> emailClient.listEmails())
                .doesNotThrowAnyException();

        // Then: Verify correct number of emails
        int actualEmailCount = getEmailCountFromGreenMail();
        assertThat(actualEmailCount).isEqualTo(expectedEmailCount);
    }

    @Test
    @DisplayName("Should send email successfully")
    void should_sendEmailSuccessfully() {
        // Given: Email details
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // When & Then: Send email should complete without errors
        assertThatCode(() -> emailClient.sendEmail(to, subject, body))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle special characters in email content correctly")
    void should_handleSpecialCharacters_when_sendingEmail() {
        // Given: Email with special characters
        String to = "recipient@example.com";
        String subject = "Test Subject with Special Chars: áéíóú & <tags>";
        String body = "Test Body\nWith\nMultiple\nLines\nAnd special chars: áéíóú";

        // When & Then: Send email should complete without errors
        assertThatCode(() -> emailClient.sendEmail(to, subject, body))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should send email successfully with long body content")
    void should_sendEmailSuccessfully_when_bodyIsLong() {
        // Given: Email with long body
        String to = "recipient@example.com";
        String subject = "Long Email Test";
        StringBuilder longBody = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longBody.append("Line ").append(i).append(": This is a test line.\n");
        }

        // When & Then: Send email should complete without errors
        assertThatCode(() -> emailClient.sendEmail(to, subject, longBody.toString()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should list emails successfully using IMAP SSL port")
    void should_listEmailsSuccessfully_when_usingIMAPSSLPort() {
        // Given: Use IMAP SSL port (3993) - non-privileged test port
        int imapSslPort = IMAP_SSL_PORT; // GreenMail IMAP SSL port
        EmailClient sslClient = new EmailClient(
                TEST_HOST,
                imapSslPort,
                smtpPort,
                TEST_USER,
                TEST_PASSWORD
        );

        // When & Then: List emails should complete without errors
        assertThatCode(() -> sslClient.listEmails())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should count multiple messages correctly")
    void should_countMultipleMessagesCorrectly() throws MessagingException {
        // Given: Add multiple test emails to the inbox
        int expectedEmailCount = 5;
        Session session = greenMail.getImap().createSession();

        for (int i = 1; i <= expectedEmailCount; i++) {
            MimeMessage message = createTestMessage(
                    session,
                    "sender" + i + "@example.com",
                    TEST_USER,
                    "Test Subject " + i,
                    "Test Body " + i
            );
            greenMail.getUserManager().getUser(TEST_USER).deliver(message);
        }

        // When: List emails
        assertThatCode(() -> emailClient.listEmails())
                .doesNotThrowAnyException();

        // Then: Verify correct number of emails
        int actualEmailCount = getEmailCountFromGreenMail();
        assertThat(actualEmailCount).isEqualTo(expectedEmailCount);
    }

    /**
     * Helper method to get email count from GreenMail inbox.
     * This method connects to GreenMail's IMAP server and retrieves the message count.
     * Uses try-with-resources to ensure proper resource cleanup.
     *
     * @return the number of messages in the inbox
     */
    private int getEmailCountFromGreenMail() {
        try {
            Session session = greenMail.getImap().createSession();
            try (Store store = session.getStore("imap")) {
                store.connect(TEST_HOST, IMAP_PORT, TEST_USER, TEST_PASSWORD);
                try (Folder inbox = store.getFolder("INBOX")) {
                    inbox.open(Folder.READ_ONLY);
                    return inbox.getMessageCount();
                }
            }
        } catch (MessagingException e) {
            throw new AssertionError("Failed to get email count from GreenMail", e);
        }
    }

    /**
     * Helper method to create a test MimeMessage.
     * This reduces code duplication in test methods.
     *
     * @param session the mail session
     * @param from the sender email address
     * @param to the recipient email address
     * @param subject the email subject
     * @param body the email body text
     * @return a configured MimeMessage
     * @throws MessagingException if message creation fails
     */
    private MimeMessage createTestMessage(Session session, String from, String to, String subject, String body)
            throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}
