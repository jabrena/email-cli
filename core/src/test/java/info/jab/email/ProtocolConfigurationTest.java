package info.jab.email;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for ProtocolConfiguration.
 */
class ProtocolConfigurationTest {

    @Test
    void shouldCreateImapConfigurationForPort143() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(143);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.IMAP);
        assertThat(config.isUseSSL()).isFalse();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldCreateImapConfigurationForPort3143() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(3143);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.IMAP);
        assertThat(config.isUseSSL()).isFalse();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldCreateImapSslConfigurationForPort993() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(993);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.IMAP);
        assertThat(config.isUseSSL()).isTrue();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldCreateImapSslConfigurationForPort3993() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(3993);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.IMAP);
        assertThat(config.isUseSSL()).isTrue();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldCreatePop3ConfigurationForPort110() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(110);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.POP3);
        assertThat(config.isUseSSL()).isFalse();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldCreatePop3SslConfigurationForPort995() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(995);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.POP3);
        assertThat(config.isUseSSL()).isTrue();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldThrowExceptionForUnsupportedPort() {
        // When/Then
        assertThatThrownBy(() -> ProtocolConfiguration.fromPort(9999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported IMAP/POP3 port");
    }

    @Test
    void shouldCreateSmtpConfigurationForPort25() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(25);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.SMTP);
        assertThat(config.isUseSSL()).isFalse();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldCreateSmtpStartTlsConfigurationForPort587() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(587);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.SMTP);
        assertThat(config.isUseSSL()).isFalse();
        assertThat(config.isUseSTARTTLS()).isTrue();
    }

    @Test
    void shouldCreateSmtpSslConfigurationForPort465() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(465);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.SMTP);
        assertThat(config.isUseSSL()).isTrue();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldCreateSmtpConfigurationForNonStandardPort() {
        // When
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(1025);

        // Then
        assertThat(config.getProtocol()).isEqualTo(Protocol.SMTP);
        assertThat(config.isUseSSL()).isFalse();
        assertThat(config.isUseSTARTTLS()).isFalse();
    }

    @Test
    void shouldGenerateJavaMailPropertiesForImap() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(143);

        // When
        Properties properties = config.toJavaMailProperties("mail.example.com", 143);

        // Then
        assertThat(properties.getProperty("mail.store.protocol")).isEqualTo("imap");
        assertThat(properties.getProperty("mail.imap.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.imap.port")).isEqualTo("143");
        assertThat(properties.getProperty("mail.imap.auth")).isEqualTo("true");
        assertThat(properties.getProperty("mail.imap.ssl.enable")).isEqualTo("false");
    }

    @Test
    void shouldGenerateJavaMailPropertiesForImapSsl() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(993);

        // When
        Properties properties = config.toJavaMailProperties("mail.example.com", 993);

        // Then
        assertThat(properties.getProperty("mail.store.protocol")).isEqualTo("imap");
        assertThat(properties.getProperty("mail.imap.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.imap.port")).isEqualTo("993");
        assertThat(properties.getProperty("mail.imap.auth")).isEqualTo("true");
        assertThat(properties.getProperty("mail.imap.ssl.enable")).isEqualTo("true");
        assertThat(properties.getProperty("mail.imap.ssl.trust")).isEqualTo("*");
        assertThat(properties.getProperty("mail.imap.ssl.checkserveridentity")).isEqualTo("false");
    }

    @Test
    void shouldGenerateJavaMailPropertiesForPop3() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(110);

        // When
        Properties properties = config.toJavaMailProperties("mail.example.com", 110);

        // Then
        assertThat(properties.getProperty("mail.store.protocol")).isEqualTo("pop3");
        assertThat(properties.getProperty("mail.pop3.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.pop3.port")).isEqualTo("110");
        assertThat(properties.getProperty("mail.pop3.auth")).isEqualTo("true");
        assertThat(properties.getProperty("mail.pop3.ssl.enable")).isEqualTo("false");
    }

    @Test
    void shouldGenerateJavaMailPropertiesForPop3Ssl() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromPort(995);

        // When
        Properties properties = config.toJavaMailProperties("mail.example.com", 995);

        // Then
        assertThat(properties.getProperty("mail.store.protocol")).isEqualTo("pop3");
        assertThat(properties.getProperty("mail.pop3.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.pop3.port")).isEqualTo("995");
        assertThat(properties.getProperty("mail.pop3.auth")).isEqualTo("true");
        assertThat(properties.getProperty("mail.pop3.ssl.enable")).isEqualTo("true");
        assertThat(properties.getProperty("mail.pop3.ssl.trust")).isEqualTo("*");
        assertThat(properties.getProperty("mail.pop3.ssl.checkserveridentity")).isEqualTo("false");
    }

    @Test
    void shouldGenerateSmtpJavaMailPropertiesForPort25() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(25);

        // When
        Properties properties = config.toSmtpJavaMailProperties("mail.example.com", 25);

        // Then
        assertThat(properties.getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.smtp.port")).isEqualTo("25");
        assertThat(properties.getProperty("mail.smtp.auth")).isEqualTo("true");
        assertThat(properties.getProperty("mail.smtp.ssl.enable")).isEqualTo("false");
        assertThat(properties.getProperty("mail.smtp.starttls.enable")).isNull();
    }

    @Test
    void shouldGenerateSmtpJavaMailPropertiesForPort587() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(587);

        // When
        Properties properties = config.toSmtpJavaMailProperties("mail.example.com", 587);

        // Then
        assertThat(properties.getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.smtp.port")).isEqualTo("587");
        assertThat(properties.getProperty("mail.smtp.auth")).isEqualTo("true");
        assertThat(properties.getProperty("mail.smtp.ssl.enable")).isEqualTo("false");
        assertThat(properties.getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
    }

    @Test
    void shouldGenerateSmtpJavaMailPropertiesForPort465() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(465);

        // When
        Properties properties = config.toSmtpJavaMailProperties("mail.example.com", 465);

        // Then
        assertThat(properties.getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.smtp.port")).isEqualTo("465");
        assertThat(properties.getProperty("mail.smtp.auth")).isEqualTo("true");
        assertThat(properties.getProperty("mail.smtp.ssl.enable")).isEqualTo("true");
        assertThat(properties.getProperty("mail.smtp.starttls.enable")).isNull();
    }

    @Test
    void shouldUseSmtpPropertiesWhenProtocolIsSmtp() {
        // Given
        ProtocolConfiguration config = ProtocolConfiguration.fromSmtpPort(587);

        // When
        Properties properties = config.toJavaMailProperties("mail.example.com", 587);

        // Then
        // Should delegate to toSmtpJavaMailProperties
        assertThat(properties.getProperty("mail.smtp.host")).isEqualTo("mail.example.com");
        assertThat(properties.getProperty("mail.smtp.port")).isEqualTo("587");
    }
}

