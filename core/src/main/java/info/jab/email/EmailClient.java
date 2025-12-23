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
     * Lists all emails in the specified folder.
     *
     * @param folder the folder name to list emails from
     * @return a list of messages, or an empty list if there is an error accessing the email store
     */
    List<Message> listEmails(String folder);

    /**
     * Lists emails in the specified folder matching the given search term.
     * Uses server-side filtering for efficient querying.
     *
     * @param folder the folder name to list emails from
     * @param searchTerm the search term for filtering
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
}
