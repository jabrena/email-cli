package info.jab.cli.command;

import info.jab.email.EmailClient;
import info.jab.email.EmailClientBuilder;
import info.jab.email.EmailSearch;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

/**
 * Command to delete emails in a folder with optional filtering.
 */
@Command(
        name = "delete-emails",
        description = "Delete emails in a folder matching the specified criteria"
)
public class DeleteEmailsCommand extends EmailFilterCommand implements Callable<Integer> {

    private final EmailClient emailClient;

    @Parameters(
            index = "0",
            description = "Folder name to delete emails from (e.g., INBOX)",
            defaultValue = "INBOX"
    )
    private String folder = "INBOX";

    /**
     * Constructor for dependency injection.
     *
     * @param emailClient the EmailClient to use (if null, will load from config)
     */
    public DeleteEmailsCommand(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    @Override
    public Integer call() {
        try {
            EmailClient client = getEmailClient();

            EmailSearch search = buildSearchTerm();

            if (search == null) {
                System.err.println("Error: At least one filter option must be specified to prevent accidental deletion of all emails.");
                System.err.println("Use --help to see available filter options.");
                return 1;
            }

            boolean deleted = client.deleteEmails(folder, search.build());

            if (deleted) {
                System.out.println("Emails deleted successfully from folder: " + folder);
                return 0;
            } else {
                System.out.println("No emails found matching the criteria in folder: " + folder);
                return 0;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Error deleting emails: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }


    private EmailClient getEmailClient() {
        if (emailClient != null) {
            return emailClient;
        }
        EmailConfig config = EmailConfig.load();
        return EmailClientBuilder.builder()
                .hostname(config.getHostname())
                .imapPort(config.getImapPort())
                .smtpPort(config.getSmtpPort())
                .user(config.getUser())
                .password(config.getPassword())
                .build();
    }
}

