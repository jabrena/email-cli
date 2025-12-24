package info.jab.cli.command;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmailInfo.
 */
class EmailInfoTest {

    @Test
    void shouldCreateEmailInfoWithNullSentDate() {
        // When
        EmailInfo emailInfo = EmailInfo.fromMessage(1, "sender@example.com", "Subject", null);

        // Then
        assertThat(emailInfo.index()).isEqualTo(1);
        assertThat(emailInfo.from()).isEqualTo("sender@example.com");
        assertThat(emailInfo.subject()).isEqualTo("Subject");
        assertThat(emailInfo.sentDate()).isNull();
    }

    @Test
    void shouldCreateEmailInfoWithNonNullSentDate() {
        // Given
        Date sentDate = new Date();

        // When
        EmailInfo emailInfo = EmailInfo.fromMessage(2, "sender@example.com", "Test Subject", sentDate);

        // Then
        assertThat(emailInfo.index()).isEqualTo(2);
        assertThat(emailInfo.from()).isEqualTo("sender@example.com");
        assertThat(emailInfo.subject()).isEqualTo("Test Subject");
        assertThat(emailInfo.sentDate()).isNotNull();
        assertThat(emailInfo.sentDate()).isNotEmpty();
    }

    @Test
    void shouldFormatSentDateCorrectly() {
        // Given
        Date sentDate = new Date(1640995200000L); // 2022-01-01 00:00:00 UTC

        // When
        EmailInfo emailInfo = EmailInfo.fromMessage(1, "sender@example.com", "Subject", sentDate);

        // Then
        assertThat(emailInfo.sentDate()).isNotNull();
        assertThat(emailInfo.sentDate()).contains("2022");
    }

    @Test
    void shouldCreateEmailInfoWithAllFields() {
        // Given
        Date sentDate = new Date();

        // When
        EmailInfo emailInfo = EmailInfo.fromMessage(5, "test@example.com", "Important Email", sentDate);

        // Then
        assertThat(emailInfo.index()).isEqualTo(5);
        assertThat(emailInfo.from()).isEqualTo("test@example.com");
        assertThat(emailInfo.subject()).isEqualTo("Important Email");
        assertThat(emailInfo.sentDate()).isNotNull();
    }
}

