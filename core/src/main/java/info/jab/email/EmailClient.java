package info.jab.email;

import jakarta.mail.Message;
import jakarta.mail.search.SearchTerm;
import java.util.List;

/**
 * Interface for email client operations.
 * Provides methods for listing and sending emails.
 */
public interface EmailClient {

    /**
     * Lists all folders in the email store.
     *
     * @return a list of folder names, or an empty list if there is an error accessing the email store
     */
    List<String> listFolders();

    /**
     * Lists emails in the specified folder matching the given search term.
     * Uses server-side filtering for efficient querying.
     * Pass null as searchTerm to list all emails in the folder.
     *
     * @param folder the folder name to list emails from
     * @param searchTerm the search term for filtering, or null to list all emails
     * @return a list of filtered messages, or an empty list if there is an error
     */
    List<Message> listEmails(String folder, SearchTerm searchTerm);

    /**
     * Sends an email.
     *
     * @param email the email to send
     * @return true if the email was sent successfully, false otherwise
     */
    boolean send(EmailMessage email);

    /**
     * Deletes emails from the specified folder matching the given search term.
     * Uses server-side filtering for efficient querying and bulk deletion.
     *
     * @param folder the folder name containing the emails
     * @param searchTerm the search term for filtering emails to delete
     * @return true if emails were deleted successfully, false otherwise
     */
    boolean deleteEmails(String folder, SearchTerm searchTerm);
}
