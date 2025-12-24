package info.jab.email;

import jakarta.mail.Session;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SessionFactory.
 */
class SessionFactoryTest {

    @Test
    void shouldCreateStoreSessionForImap() {
        // When
        ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(143);
        Session session = SessionFactory.createStoreSession("mail.example.com", 143, "user@example.com", "password", protocolConfig);

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getProperties().getProperty("mail.store.protocol")).isEqualTo("imap");
    }

    @Test
    void shouldCreateStoreSessionForImapSsl() {
        // When
        ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(993);
        Session session = SessionFactory.createStoreSession("mail.example.com", 993, "user@example.com", "password", protocolConfig);

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getProperties().getProperty("mail.store.protocol")).isEqualTo("imap");
        assertThat(session.getProperties().getProperty("mail.imap.ssl.enable")).isEqualTo("true");
    }

    @Test
    void shouldCreateStoreSessionForPop3() {
        // When
        ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(110);
        Session session = SessionFactory.createStoreSession("mail.example.com", 110, "user@example.com", "password", protocolConfig);

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getProperties().getProperty("mail.store.protocol")).isEqualTo("pop3");
    }

    @Test
    void shouldCreateSmtpSessionForPort25() {
        // When
        Session session = SessionFactory.createSmtpSession("mail.example.com", 25, "user@example.com", "password");

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getProperties().getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(session.getProperties().getProperty("mail.smtp.port")).isEqualTo("25");
    }

    @Test
    void shouldCreateSmtpSessionForPort587() {
        // When
        Session session = SessionFactory.createSmtpSession("mail.example.com", 587, "user@example.com", "password");

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getProperties().getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(session.getProperties().getProperty("mail.smtp.port")).isEqualTo("587");
        assertThat(session.getProperties().getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
    }

    @Test
    void shouldCreateSmtpSessionForPort465() {
        // When
        Session session = SessionFactory.createSmtpSession("mail.example.com", 465, "user@example.com", "password");

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getProperties().getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(session.getProperties().getProperty("mail.smtp.port")).isEqualTo("465");
        assertThat(session.getProperties().getProperty("mail.smtp.ssl.enable")).isEqualTo("true");
    }

    @Test
    void shouldCreateSmtpSessionForNonStandardPort() {
        // When
        Session session = SessionFactory.createSmtpSession("mail.example.com", 1025, "user@example.com", "password");

        // Then
        assertThat(session).isNotNull();
        assertThat(session.getProperties().getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(session.getProperties().getProperty("mail.smtp.port")).isEqualTo("1025");
    }
}

