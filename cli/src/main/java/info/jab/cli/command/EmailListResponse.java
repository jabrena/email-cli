package info.jab.cli.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response model for email list output.
 */
public record EmailListResponse(
    @JsonProperty("folder") String folder,
    @JsonProperty("count") int count,
    @JsonProperty("emails") List<EmailInfo> emails
) {
}

