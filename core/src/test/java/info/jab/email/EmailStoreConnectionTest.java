package info.jab.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EmailStoreConnection.
 * Note: These are basic structural tests. For full integration testing,
 * see EmailClientIT which tests EmailStoreConnection through EmailClient.
 */
class EmailStoreConnectionTest {

    @Test
    void shouldCreateProtocolConfiguration() {
        // When
        ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromPort(143);

        // Then
        assertThat(protocolConfig).isNotNull();
        assertThat(protocolConfig.getProtocol()).isEqualTo(Protocol.IMAP);
    }

    @Test
    void shouldSupportDifferentProtocols() {
        // When
        ProtocolConfiguration imapConfig = ProtocolConfiguration.fromPort(143);
        ProtocolConfiguration imapSslConfig = ProtocolConfiguration.fromPort(993);
        ProtocolConfiguration pop3Config = ProtocolConfiguration.fromPort(110);
        ProtocolConfiguration pop3SslConfig = ProtocolConfiguration.fromPort(995);

        // Then
        assertThat(imapConfig.getProtocol()).isEqualTo(Protocol.IMAP);
        assertThat(imapSslConfig.getProtocol()).isEqualTo(Protocol.IMAP);
        assertThat(imapSslConfig.isUseSSL()).isTrue();
        assertThat(pop3Config.getProtocol()).isEqualTo(Protocol.POP3);
        assertThat(pop3SslConfig.getProtocol()).isEqualTo(Protocol.POP3);
        assertThat(pop3SslConfig.isUseSSL()).isTrue();
    }
}

