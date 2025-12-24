package info.jab.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for EmailClientBuilder.
 */
class EmailClientBuilderTest {

    @Test
    void shouldCreateBuilder() {
        // When
        EmailClientBuilder builder = EmailClientBuilder.builder();

        // Then
        assertThat(builder).isNotNull();
    }

    @Test
    void shouldBuildEmailClientWithAllFields() {
        // When
        EmailClient client = EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .imapPort(143)
                .smtpPort(587)
                .user("test@example.com")
                .password("password123")
                .build();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenHostnameIsNull() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .imapPort(143)
                .smtpPort(587)
                .user("test@example.com")
                .password("password123")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Hostname is required");
    }

    @Test
    void shouldThrowExceptionWhenHostnameIsBlank() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .hostname("   ")
                .imapPort(143)
                .smtpPort(587)
                .user("test@example.com")
                .password("password123")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Hostname is required");
    }

    @Test
    void shouldThrowExceptionWhenImapPortIsNull() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .smtpPort(587)
                .user("test@example.com")
                .password("password123")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("IMAP port is required");
    }

    @Test
    void shouldThrowExceptionWhenSmtpPortIsNull() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .imapPort(143)
                .user("test@example.com")
                .password("password123")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SMTP port is required");
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .imapPort(143)
                .smtpPort(587)
                .password("password123")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User is required");
    }

    @Test
    void shouldThrowExceptionWhenUserIsBlank() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .imapPort(143)
                .smtpPort(587)
                .user("   ")
                .password("password123")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User is required");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .imapPort(143)
                .smtpPort(587)
                .user("test@example.com")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // When/Then
        assertThatThrownBy(() -> EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .imapPort(143)
                .smtpPort(587)
                .user("test@example.com")
                .password("")
                .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Password is required");
    }

    @Test
    void shouldSupportMethodChaining() {
        // When
        EmailClientBuilder builder = EmailClientBuilder.builder()
                .hostname("mail.example.com")
                .imapPort(143)
                .smtpPort(587)
                .user("test@example.com")
                .password("password123");

        // Then
        assertThat(builder).isNotNull();
        EmailClient client = builder.build();
        assertThat(client).isNotNull();
    }
}
