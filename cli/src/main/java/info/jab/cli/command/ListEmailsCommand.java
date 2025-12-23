package info.jab.cli.command;

import info.jab.email.EmailClient;
import info.jab.email.EmailClientBuilder;
import info.jab.email.EmailSearch;
import jakarta.mail.Message;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Command to list emails in a folder with optional filtering.
 */
@Command(
        name = "list-emails",
        description = "List emails in a folder with optional filtering"
)
public class ListEmailsCommand extends EmailFilterCommand implements Callable<Integer> {

    private final EmailClient emailClient;

    @Parameters(
            index = "0",
            description = "Folder name to list emails from (e.g., INBOX)",
            defaultValue = "INBOX"
    )
    private String folder = "INBOX";

    /**
     * Constructor for dependency injection.
     *
     * @param emailClient the EmailClient to use (if null, will load from config)
     */
    public ListEmailsCommand(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    @Option(
            names = {"--text"},
            description = "Output results in plain text format (default is JSON)"
    )
    private boolean text;

    @Override
    public Integer call() {
        try {
            EmailClient client = getEmailClient();

            EmailSearch search = buildSearchTerm();

            List<Message> messages = client.listEmails(folder, search != null ? search.build() : null);

            if (messages.isEmpty()) {
                if (text) {
                    String message = search != null
                        ? "No emails found matching the criteria in folder: " + folder
                        : "No emails found in folder: " + folder;
                    System.out.println(message);
                } else {
                    outputJson(messages, folder);
                }
                return 0;
            }

            if (text) {
                outputText(messages, folder);
            } else {
                outputJson(messages, folder);
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Error listing emails: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }


    private void outputText(List<Message> messages, String folder) {
        System.out.println("Emails in folder '" + folder + "' (" + messages.size() + "):");
        System.out.println();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            try {
                String fromStr = "Unknown";
                try {
                    if (msg.getFrom() != null && msg.getFrom().length > 0) {
                        fromStr = msg.getFrom()[0].toString();
                    }
                } catch (Exception e) {
                    // Ignore, use default "Unknown"
                }

                String subjectStr = "(No Subject)";
                try {
                    String subject = msg.getSubject();
                    if (subject != null && !subject.isBlank()) {
                        subjectStr = subject;
                    }
                } catch (Exception e) {
                    // Ignore, use default "(No Subject)"
                }

                Date sentDate = new Date();
                try {
                    Date date = msg.getSentDate();
                    if (date != null) {
                        sentDate = date;
                    }
                } catch (Exception e) {
                    // Ignore, use current date as default
                }

                String dateStr = dateFormat.format(sentDate);
                System.out.printf("%d. [%s] %s - %s%n", i + 1, dateStr, fromStr, subjectStr);
            } catch (Exception e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null || errorMsg.isBlank()) {
                    errorMsg = e.getClass().getSimpleName();
                }
                System.err.println("Error reading message " + (i + 1) + ": " + errorMsg);
            }
        }
    }

    private void outputJson(List<Message> messages, String folder) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<EmailInfo> emailInfos = new ArrayList<>();

            for (int i = 0; i < messages.size(); i++) {
                Message msg = messages.get(i);
                try {
                    String fromStr = "Unknown";
                    try {
                        if (msg.getFrom() != null && msg.getFrom().length > 0) {
                            fromStr = msg.getFrom()[0].toString();
                        }
                    } catch (Exception e) {
                        // Ignore, use default "Unknown"
                    }

                    String subjectStr = "(No Subject)";
                    try {
                        String subject = msg.getSubject();
                        if (subject != null && !subject.isBlank()) {
                            subjectStr = subject;
                        }
                    } catch (Exception e) {
                        // Ignore, use default "(No Subject)"
                    }

                    Date sentDate = new Date();
                    try {
                        Date date = msg.getSentDate();
                        if (date != null) {
                            sentDate = date;
                        }
                    } catch (Exception e) {
                        // Ignore, use current date as default
                    }

                    emailInfos.add(EmailInfo.fromMessage(i + 1, fromStr, subjectStr, sentDate));
                } catch (Exception e) {
                    // Skip invalid messages
                }
            }

            EmailListResponse response = new EmailListResponse(folder, emailInfos.size(), emailInfos);
            String json = objectMapper.writeValueAsString(response);
            System.out.println(json);
        } catch (Exception e) {
            System.err.println("Error generating JSON output: " + e.getMessage());
            e.printStackTrace();
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

