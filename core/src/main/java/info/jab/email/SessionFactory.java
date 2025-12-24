package info.jab.email;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import java.util.Properties;

public class SessionFactory {

    public static Session createStoreSession(String hostname, int imapPort, String user, String password, ProtocolConfiguration protocolConfig) {
        Properties properties = protocolConfig.toJavaMailProperties(hostname, imapPort);

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
    }

    public static Session createSmtpSession(String hostname, int smtpPort, String user, String password) {
        ProtocolConfiguration protocolConfig = ProtocolConfiguration.fromSmtpPort(smtpPort);
        Properties smtpProperties = protocolConfig.toSmtpJavaMailProperties(hostname, smtpPort);

        return Session.getInstance(smtpProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
    }
}
