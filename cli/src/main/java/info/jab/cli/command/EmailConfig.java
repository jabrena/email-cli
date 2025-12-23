package info.jab.cli.command;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

/**
 * Configuration class for reading email settings from .env files.
 * Supports reading from .env file in the current directory or user's home directory.
 */
public class EmailConfig {
    private static final String ENV_HOSTNAME = "EMAIL_HOSTNAME";
    private static final String ENV_IMAP_PORT = "EMAIL_IMAP_PORT";
    private static final String ENV_SMTP_PORT = "EMAIL_SMTP_PORT";
    private static final String ENV_USER = "EMAIL_USER";
    private static final String ENV_PASSWORD = "EMAIL_PASSWORD";

    private final String hostname;
    private final int imapPort;
    private final int smtpPort;
    private final String user;
    private final String password;

    private EmailConfig(String hostname, int imapPort, int smtpPort, String user, String password) {
        this.hostname = hostname;
        this.imapPort = imapPort;
        this.smtpPort = smtpPort;
        this.user = user;
        this.password = password;
    }

    /**
     * Package-private constructor for testing purposes.
     */
    EmailConfig(String hostname, int imapPort, int smtpPort, String user, String password, boolean test) {
        this(hostname, imapPort, smtpPort, user, password);
    }

    /**
     * Loads configuration from .env file.
     * First tries to load from current directory, then from user's home directory.
     *
     * @return EmailConfig instance with loaded values
     * @throws IllegalStateException if required environment variables are missing
     */
    public static EmailConfig load() {
        Dotenv dotenv = null;
        try {
            // Try to load from current directory first
            dotenv = Dotenv.load();
        } catch (DotenvException e) {
            // If not found, try user's home directory
            try {
                String homeDir = System.getProperty("user.home");
                dotenv = Dotenv.configure()
                        .directory(homeDir)
                        .load();
            } catch (DotenvException ex) {
                // If still not found, try system environment variables
                dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .load();
            }
        }

        String hostname = getRequiredEnv(dotenv, ENV_HOSTNAME);
        int imapPort = getRequiredIntEnv(dotenv, ENV_IMAP_PORT);
        int smtpPort = getRequiredIntEnv(dotenv, ENV_SMTP_PORT);
        String user = getRequiredEnv(dotenv, ENV_USER);
        String password = getRequiredEnv(dotenv, ENV_PASSWORD);

        return new EmailConfig(hostname, imapPort, smtpPort, user, password);
    }

    private static String getRequiredEnv(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable " + key + " is not set. " +
                    "Please create a .env file or set it as an environment variable.");
        }
        return value;
    }

    private static int getRequiredIntEnv(Dotenv dotenv, String key) {
        String value = getRequiredEnv(dotenv, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Environment variable " + key + " must be a valid integer, but got: " + value);
        }
    }

    public String getHostname() {
        return hostname;
    }

    public int getImapPort() {
        return imapPort;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}

