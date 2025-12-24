package info.jab.email;

/**
 * Builder for creating EmailClient instances.
 * Provides a fluent API for configuring and constructing EmailClientImpl objects.
 */
public final class EmailClientBuilder {
    private String hostname;
    private Integer imapPort;
    private Integer smtpPort;
    private String user;
    private String password;

    private EmailClientBuilder() {
        // Private constructor to enforce use of builder pattern
    }

    /**
     * Creates a new EmailClientBuilder instance.
     *
     * @return a new EmailClientBuilder
     */
    public static EmailClientBuilder builder() {
        return new EmailClientBuilder();
    }

    /**
     * Sets the email server hostname.
     *
     * @param hostname the hostname of the email server
     * @return this builder instance for method chaining
     */
    public EmailClientBuilder hostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    /**
     * Sets the IMAP port for receiving emails.
     *
     * @param imapPort the IMAP port number
     * @return this builder instance for method chaining
     */
    public EmailClientBuilder imapPort(int imapPort) {
        this.imapPort = imapPort;
        return this;
    }

    /**
     * Sets the SMTP port for sending emails.
     *
     * @param smtpPort the SMTP port number
     * @return this builder instance for method chaining
     */
    public EmailClientBuilder smtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
        return this;
    }

    /**
     * Sets the username for email authentication.
     *
     * @param user the username/email address
     * @return this builder instance for method chaining
     */
    public EmailClientBuilder user(String user) {
        this.user = user;
        return this;
    }

    /**
     * Sets the password for email authentication.
     *
     * @param password the password
     * @return this builder instance for method chaining
     */
    public EmailClientBuilder password(String password) {
        this.password = password;
        return this;
    }

    /**
     * Builds and returns an EmailClient instance.
     * Validates that all required fields are set before creating the instance.
     *
     * @return a new EmailClientImpl instance
     * @throws IllegalStateException if any required field is missing
     */
    public EmailClient build() {
        validateRequiredFields();
        return new EmailClientImpl(hostname, imapPort, smtpPort, user, password);
    }

    private void validateRequiredFields() {
        if (hostname == null || hostname.isBlank()) {
            throw new IllegalStateException("Hostname is required");
        }
        if (imapPort == null) {
            throw new IllegalStateException("IMAP port is required");
        }
        if (smtpPort == null) {
            throw new IllegalStateException("SMTP port is required");
        }
        if (user == null || user.isBlank()) {
            throw new IllegalStateException("User is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Password is required");
        }
    }
}

