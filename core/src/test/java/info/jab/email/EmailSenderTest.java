package info.jab.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmailSender.
 */
class EmailSenderTest {

    @Test
    void shouldCreateEmailSender() {
        // When
        EmailSender sender = new EmailSender("mail.example.com", 587, "user@example.com", "password");

        // Then
        assertThat(sender).isNotNull();
    }

    @Test
    void shouldCreateEmailSenderWithDifferentPorts() {
        // When
        EmailSender sender25 = new EmailSender("mail.example.com", 25, "user@example.com", "password");
        EmailSender sender465 = new EmailSender("mail.example.com", 465, "user@example.com", "password");
        EmailSender sender1025 = new EmailSender("mail.example.com", 1025, "user@example.com", "password");

        // Then
        assertThat(sender25).isNotNull();
        assertThat(sender465).isNotNull();
        assertThat(sender1025).isNotNull();
    }

    @Test
    void shouldCreateEmailSenderWithDifferentConfigurations() {
        // When
        EmailSender senderWithStartTls = new EmailSender("mail.example.com", 587, "user@example.com", "password");
        EmailSender senderWithSsl = new EmailSender("mail.example.com", 465, "user@example.com", "password");
        EmailSender senderPlain = new EmailSender("mail.example.com", 25, "user@example.com", "password");

        // Then
        assertThat(senderWithStartTls).isNotNull();
        assertThat(senderWithSsl).isNotNull();
        assertThat(senderPlain).isNotNull();
    }
}

