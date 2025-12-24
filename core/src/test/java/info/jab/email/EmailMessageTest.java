package info.jab.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmailMessage record.
 */
class EmailMessageTest {

    @Test
    void shouldCreateEmailMessage() {
        // When
        EmailMessage message = new EmailMessage("recipient@example.com", "Subject", "Body");

        // Then
        assertThat(message.to()).isEqualTo("recipient@example.com");
        assertThat(message.subject()).isEqualTo("Subject");
        assertThat(message.body()).isEqualTo("Body");
    }

    @Test
    void shouldSupportNullValues() {
        // When
        EmailMessage message = new EmailMessage(null, null, null);

        // Then
        assertThat(message.to()).isNull();
        assertThat(message.subject()).isNull();
        assertThat(message.body()).isNull();
    }

    @Test
    void shouldSupportEmptyStrings() {
        // When
        EmailMessage message = new EmailMessage("", "", "");

        // Then
        assertThat(message.to()).isEmpty();
        assertThat(message.subject()).isEmpty();
        assertThat(message.body()).isEmpty();
    }
}
