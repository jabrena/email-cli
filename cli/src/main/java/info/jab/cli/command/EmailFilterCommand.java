package info.jab.cli.command;

import info.jab.email.EmailSearch;
import picocli.CommandLine.Option;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Base class for email commands that support filtering.
 * Provides common filter options and search term building logic.
 */
public abstract class EmailFilterCommand {

    @Option(
            names = {"--unread"},
            description = "Filter unread emails"
    )
    protected boolean unread;

    @Option(
            names = {"--read"},
            description = "Filter read emails"
    )
    protected boolean read;

    @Option(
            names = {"--from"},
            description = "Filter emails from sender (email address or name)"
    )
    protected String from;

    @Option(
            names = {"--subject"},
            description = "Filter emails with subject containing text"
    )
    protected String subject;

    @Option(
            names = {"--body"},
            description = "Filter emails with body containing text"
    )
    protected String body;

    @Option(
            names = {"--to"},
            description = "Filter emails sent to recipient"
    )
    protected String to;

    @Option(
            names = {"--cc"},
            description = "Filter emails CC'd to recipient"
    )
    protected String cc;

    @Option(
            names = {"--received-after"},
            description = "Filter emails received after date (format: yyyy-MM-dd)"
    )
    protected String receivedAfter;

    @Option(
            names = {"--received-before"},
            description = "Filter emails received before date (format: yyyy-MM-dd)"
    )
    protected String receivedBefore;

    @Option(
            names = {"--sent-after"},
            description = "Filter emails sent after date (format: yyyy-MM-dd)"
    )
    protected String sentAfter;

    @Option(
            names = {"--sent-before"},
            description = "Filter emails sent before date (format: yyyy-MM-dd)"
    )
    protected String sentBefore;

    /**
     * Builds a search term from the filter options.
     *
     * @return the EmailSearch instance, or null if no filters are specified
     */
    protected EmailSearch buildSearchTerm() {
        EmailSearch search = null;

        if (unread) {
            search = combineSearch(search, EmailSearch.unread());
        }
        if (read) {
            search = combineSearch(search, EmailSearch.read());
        }
        if (from != null && !from.isBlank()) {
            search = combineSearch(search, EmailSearch.from(from));
        }
        if (subject != null && !subject.isBlank()) {
            search = combineSearch(search, EmailSearch.subjectContains(subject));
        }
        if (body != null && !body.isBlank()) {
            search = combineSearch(search, EmailSearch.bodyContains(body));
        }
        if (to != null && !to.isBlank()) {
            search = combineSearch(search, EmailSearch.to(to));
        }
        if (cc != null && !cc.isBlank()) {
            search = combineSearch(search, EmailSearch.cc(cc));
        }
        if (receivedAfter != null && !receivedAfter.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(receivedAfter, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                search = combineSearch(search, EmailSearch.receivedAfter(date));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format for --received-after. Use yyyy-MM-dd format.");
            }
        }
        if (receivedBefore != null && !receivedBefore.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(receivedBefore, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                search = combineSearch(search, EmailSearch.receivedBefore(date));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format for --received-before. Use yyyy-MM-dd format.");
            }
        }
        if (sentAfter != null && !sentAfter.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(sentAfter, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                search = combineSearch(search, EmailSearch.sentAfter(date));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format for --sent-after. Use yyyy-MM-dd format.");
            }
        }
        if (sentBefore != null && !sentBefore.isBlank()) {
            try {
                LocalDate date = LocalDate.parse(sentBefore, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                search = combineSearch(search, EmailSearch.sentBefore(date));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format for --sent-before. Use yyyy-MM-dd format.");
            }
        }

        return search;
    }

    /**
     * Combines two EmailSearch instances using AND logic.
     *
     * @param existing the existing EmailSearch (can be null)
     * @param newSearch the new EmailSearch to combine
     * @return the combined EmailSearch
     */
    protected EmailSearch combineSearch(EmailSearch existing, EmailSearch newSearch) {
        if (existing == null) {
            return newSearch;
        }
        return existing.and(newSearch);
    }
}

