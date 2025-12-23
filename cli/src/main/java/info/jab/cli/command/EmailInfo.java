package info.jab.cli.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Email information model for JSON output.
 */
public record EmailInfo(
    @JsonProperty("index") int index,
    @JsonProperty("from") String from,
    @JsonProperty("subject") String subject,
    @JsonProperty("sentDate") String sentDate
) {
    /**
     * Factory method to create EmailInfo from a Message with date conversion.
     */
    public static EmailInfo fromMessage(int index, String from, String subject, java.util.Date sentDate) {
        String formattedDate = null;
        if (sentDate != null) {
            Instant instant = sentDate.toInstant();
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            formattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return new EmailInfo(index, from, subject, formattedDate);
    }
}

