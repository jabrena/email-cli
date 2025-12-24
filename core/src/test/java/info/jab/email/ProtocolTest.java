package info.jab.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Protocol enum.
 */
class ProtocolTest {

    @Test
    void shouldHaveCorrectValues() {
        // Then
        assertThat(Protocol.IMAP.getValue()).isEqualTo("imap");
        assertThat(Protocol.POP3.getValue()).isEqualTo("pop3");
        assertThat(Protocol.SMTP.getValue()).isEqualTo("smtp");
    }

    @Test
    void shouldReturnValueInToString() {
        // Then
        assertThat(Protocol.IMAP.toString()).isEqualTo("imap");
        assertThat(Protocol.POP3.toString()).isEqualTo("pop3");
        assertThat(Protocol.SMTP.toString()).isEqualTo("smtp");
    }

    @Test
    void shouldHaveAllEnumValues() {
        // Then
        Protocol[] values = Protocol.values();
        assertThat(values).hasSize(3);
        assertThat(values).containsExactly(Protocol.IMAP, Protocol.POP3, Protocol.SMTP);
    }

    @Test
    void shouldValueOf() {
        // Then
        assertThat(Protocol.valueOf("IMAP")).isEqualTo(Protocol.IMAP);
        assertThat(Protocol.valueOf("POP3")).isEqualTo(Protocol.POP3);
        assertThat(Protocol.valueOf("SMTP")).isEqualTo(Protocol.SMTP);
    }
}

