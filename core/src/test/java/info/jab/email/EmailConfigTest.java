package info.jab.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmailConfig.
 */
class EmailConfigTest {

    @Test
    void shouldCreateEmailConfigForTesting() {
        // When
        EmailConfig config = EmailConfig.forTesting("mail.example.com", 143, 587, "user@example.com", "password123");

        // Then
        assertThat(config).isNotNull();
        assertThat(config.getHostname()).isEqualTo("mail.example.com");
        assertThat(config.getImapPort()).isEqualTo(143);
        assertThat(config.getSmtpPort()).isEqualTo(587);
        assertThat(config.getUser()).isEqualTo("user@example.com");
        assertThat(config.getPassword()).isEqualTo("password123");
    }

    @Test
    void shouldGetAllProperties() {
        // Given
        EmailConfig config = EmailConfig.forTesting("test.example.com", 993, 465, "test@example.com", "secret");

        // Then
        assertThat(config.getHostname()).isEqualTo("test.example.com");
        assertThat(config.getImapPort()).isEqualTo(993);
        assertThat(config.getSmtpPort()).isEqualTo(465);
        assertThat(config.getUser()).isEqualTo("test@example.com");
        assertThat(config.getPassword()).isEqualTo("secret");
    }

    @Test
    void shouldSupportDifferentPorts() {
        // When
        EmailConfig config1 = EmailConfig.forTesting("mail.example.com", 143, 25, "user@example.com", "password");
        EmailConfig config2 = EmailConfig.forTesting("mail.example.com", 993, 587, "user@example.com", "password");
        EmailConfig config3 = EmailConfig.forTesting("mail.example.com", 110, 465, "user@example.com", "password");

        // Then
        assertThat(config1.getImapPort()).isEqualTo(143);
        assertThat(config1.getSmtpPort()).isEqualTo(25);
        assertThat(config2.getImapPort()).isEqualTo(993);
        assertThat(config2.getSmtpPort()).isEqualTo(587);
        assertThat(config3.getImapPort()).isEqualTo(110);
        assertThat(config3.getSmtpPort()).isEqualTo(465);
    }
}

