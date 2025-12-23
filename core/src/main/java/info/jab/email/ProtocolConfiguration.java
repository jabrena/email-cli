package info.jab.email;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolConfiguration.class);

    private final Protocol protocol;
    private final boolean useSSL;
    private final boolean useSTARTTLS;

    private ProtocolConfiguration(Protocol protocol, boolean useSSL, boolean useSTARTTLS) {
        this.protocol = protocol;
        this.useSSL = useSSL;
        this.useSTARTTLS = useSTARTTLS;
    }

    public static ProtocolConfiguration fromPort(int port) {
        Protocol protocol;
        boolean useSSL;

        if (port == 143 || port == 3143) {
            // Standard IMAP port (143) or test port (3143)
            protocol = Protocol.IMAP;
            useSSL = false;
        } else if (port == 993 || port == 3993) {
            // Standard IMAP SSL port (993) or test port (3993)
            protocol = Protocol.IMAP;
            useSSL = true;
        } else if (port == 110) {
            protocol = Protocol.POP3;
            useSSL = false;
        } else if (port == 995) {
            protocol = Protocol.POP3;
            useSSL = true;
        } else {
            throw new IllegalArgumentException(
                "Unsupported IMAP/POP3 port: " + port +
                ". Supported ports: 143 (IMAP), 993 (IMAP SSL), 110 (POP3), 995 (POP3 SSL), " +
                "3143 (IMAP test), 3993 (IMAP SSL test)"
            );
        }

        return new ProtocolConfiguration(protocol, useSSL, false);
    }

    public static ProtocolConfiguration fromSmtpPort(int port) {
        Protocol protocol = Protocol.SMTP;
        boolean useSSL;
        boolean useSTARTTLS;

        if (port == 25) {
            // Standard SMTP port (plain, no encryption)
            useSSL = false;
            useSTARTTLS = false;
        } else if (port == 587) {
            // SMTP with STARTTLS
            useSSL = false;
            useSTARTTLS = true;
        } else if (port == 465) {
            // SMTP with SSL/TLS
            useSSL = true;
            useSTARTTLS = false;
        } else {
            // Allow other ports (e.g., test ports like 1025 for MailHog) but warn
            logger.warn("Unusual SMTP port: {}", port);
            logger.warn("Standard SMTP ports: 25 (SMTP), 587 (SMTP STARTTLS), 465 (SMTP SSL)");
            // Default to plain SMTP for non-standard ports
            useSSL = false;
            useSTARTTLS = false;
        }

        return new ProtocolConfiguration(protocol, useSSL, useSTARTTLS);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public boolean isUseSTARTTLS() {
        return useSTARTTLS;
    }

    public Properties toJavaMailProperties(String host, int port) {
        if (protocol == Protocol.SMTP) {
            return toSmtpJavaMailProperties(host, port);
        }

        // For store protocols (IMAP/POP3)
        String protocolValue = protocol.getValue();
        Properties properties = new Properties();
        properties.put("mail.store.protocol", protocolValue);
        properties.put("mail." + protocolValue + ".host", host);
        properties.put("mail." + protocolValue + ".port", String.valueOf(port));
        properties.put("mail." + protocolValue + ".auth", "true");
        properties.put("mail." + protocolValue + ".ssl.enable", String.valueOf(useSSL));

        if (protocol == Protocol.IMAP) {
            properties.put("mail.imap.starttls.enable", "false");
            if (useSSL) {
                properties.put("mail.imap.ssl.trust", "*");
                properties.put("mail.imap.ssl.checkserveridentity", "false");
            }
        } else if (protocol == Protocol.POP3) {
            properties.put("mail.pop3.starttls.enable", "false");
            if (useSSL) {
                properties.put("mail.pop3.ssl.trust", "*");
                properties.put("mail.pop3.ssl.checkserveridentity", "false");
            }
        }

        return properties;
    }

    public Properties toSmtpJavaMailProperties(String host, int port) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", String.valueOf(port));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", String.valueOf(useSSL));
        if (useSTARTTLS) {
            properties.put("mail.smtp.starttls.enable", "true");
        }

        return properties;
    }
}
