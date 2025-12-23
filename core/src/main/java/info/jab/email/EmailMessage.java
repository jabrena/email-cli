package info.jab.email;

/**
 * Represents an email message with recipient, subject, and body.
 *
 * @param to the recipient email address
 * @param subject the email subject
 * @param body the email body content
 */
public record EmailMessage(String to, String subject, String body) { }
