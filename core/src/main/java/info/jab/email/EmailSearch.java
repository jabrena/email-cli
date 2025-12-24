package info.jab.email;

import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.search.SearchTerm;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.FromStringTerm;
import jakarta.mail.search.SubjectTerm;
import jakarta.mail.search.BodyTerm;
import jakarta.mail.search.ReceivedDateTerm;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.SentDateTerm;
import jakarta.mail.search.RecipientStringTerm;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.OrTerm;
import jakarta.mail.search.NotTerm;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Functional builder for composing email search terms.
 * Enables functional composition of search criteria for server-side email filtering.
 *
 * <p>This interface provides a functional approach to building SearchTerm queries
 * that are executed on the mail server, reducing network transfer and improving
 * performance for large email folders.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Simple unread emails
 * EmailSearch.unread()
 *
 * // Composition - unread AND from sender
 * EmailSearch.unread()
 *     .and(EmailSearch.from("boss@example.com"))
 *
 * // Complex composition
 * EmailSearch.unread()
 *     .or(EmailSearch.from("boss@example.com")
 *         .and(EmailSearch.subjectContains("urgent")))
 * }</pre>
 */
@FunctionalInterface
public interface EmailSearch {

    /**
     * Builds a SearchTerm from this search builder.
     *
     * @return a SearchTerm that can be used for server-side email filtering
     */
    SearchTerm build();

    // Factory methods for common searches

    /**
     * Creates a search for unread emails (not flagged as SEEN).
     *
     * @return an EmailSearch for unread emails
     */
    static EmailSearch unread() {
        return () -> new FlagTerm(new Flags(Flags.Flag.SEEN), false);
    }

    /**
     * Creates a search for read emails (flagged as SEEN).
     *
     * @return an EmailSearch for read emails
     */
    static EmailSearch read() {
        return () -> new FlagTerm(new Flags(Flags.Flag.SEEN), true);
    }

    /**
     * Creates a search for emails from a specific sender.
     *
     * @param sender the sender email address or name to search for
     * @return an EmailSearch for emails from the specified sender
     */
    static EmailSearch from(String sender) {
        return () -> new FromStringTerm(sender);
    }

    /**
     * Creates a search for emails with a subject containing the specified text.
     *
     * @param text the text to search for in the subject
     * @return an EmailSearch for emails with matching subject
     */
    static EmailSearch subjectContains(String text) {
        return () -> new SubjectTerm(text);
    }

    /**
     * Creates a search for emails with a body containing the specified text.
     *
     * @param text the text to search for in the body
     * @return an EmailSearch for emails with matching body content
     */
    static EmailSearch bodyContains(String text) {
        return () -> new BodyTerm(text);
    }

    /**
     * Creates a search for emails received after the specified date.
     *
     * @param date the date threshold (start of day)
     * @return an EmailSearch for emails received after the date
     */
    static EmailSearch receivedAfter(LocalDate date) {
        Date dateValue = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return () -> new ReceivedDateTerm(ComparisonTerm.GE, dateValue);
    }

    /**
     * Creates a search for emails received before the specified date.
     *
     * @param date the date threshold (end of day)
     * @return an EmailSearch for emails received before the date
     */
    static EmailSearch receivedBefore(LocalDate date) {
        Date dateValue = Date.from(date.atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusSeconds(1).toInstant());
        return () -> new ReceivedDateTerm(ComparisonTerm.LE, dateValue);
    }

    /**
     * Creates a search for emails sent after the specified date.
     *
     * @param date the date threshold (start of day)
     * @return an EmailSearch for emails sent after the date
     */
    static EmailSearch sentAfter(LocalDate date) {
        Date dateValue = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return () -> new SentDateTerm(ComparisonTerm.GE, dateValue);
    }

    /**
     * Creates a search for emails sent before the specified date.
     *
     * @param date the date threshold (end of day)
     * @return an EmailSearch for emails sent before the date
     */
    static EmailSearch sentBefore(LocalDate date) {
        Date dateValue = Date.from(date.atStartOfDay(ZoneId.systemDefault()).plusDays(1).minusSeconds(1).toInstant());
        return () -> new SentDateTerm(ComparisonTerm.LE, dateValue);
    }

    /**
     * Creates a search for emails with a specific recipient.
     *
     * @param recipient the recipient email address to search for
     * @return an EmailSearch for emails sent to the specified recipient
     */
    static EmailSearch to(String recipient) {
        return () -> new RecipientStringTerm(Message.RecipientType.TO, recipient);
    }

    /**
     * Creates a search for emails with a specific CC recipient.
     *
     * @param recipient the CC recipient email address to search for
     * @return an EmailSearch for emails CC'd to the specified recipient
     */
    static EmailSearch cc(String recipient) {
        return () -> new RecipientStringTerm(Message.RecipientType.CC, recipient);
    }

    /**
     * Creates a search for emails with a specific BCC recipient.
     *
     * @param recipient the BCC recipient email address to search for
     * @return an EmailSearch for emails BCC'd to the specified recipient
     */
    static EmailSearch bcc(String recipient) {
        return () -> new RecipientStringTerm(Message.RecipientType.BCC, recipient);
    }

    // Composition methods

    /**
     * Combines this search with another using AND logic.
     * Both conditions must be satisfied.
     *
     * @param other the other EmailSearch to combine with
     * @return a new EmailSearch that combines both conditions with AND
     */
    default EmailSearch and(EmailSearch other) {
        return () -> new AndTerm(this.build(), other.build());
    }

    /**
     * Combines this search with another using OR logic.
     * At least one condition must be satisfied.
     *
     * @param other the other EmailSearch to combine with
     * @return a new EmailSearch that combines both conditions with OR
     */
    default EmailSearch or(EmailSearch other) {
        return () -> new OrTerm(this.build(), other.build());
    }

    /**
     * Negates this search using NOT logic.
     *
     * @return a new EmailSearch that negates this condition
     */
    default EmailSearch not() {
        return () -> new NotTerm(this.build());
    }

    /**
     * Convenience method to convert this EmailSearch to a SearchTerm.
     *
     * @return the SearchTerm representation of this search
     */
    default SearchTerm toSearchTerm() {
        return build();
    }
}

