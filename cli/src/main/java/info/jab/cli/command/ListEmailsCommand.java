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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
public class ListEmailsCommand implements Callable<Integer> {

    @Parameters(
            index = "0",
            description = "Folder name to list emails from (e.g., INBOX)",
            defaultValue = "INBOX"
    )
    private String folder = "INBOX";

    @Option(
            names = {"--unread"},
            description = "Filter unread emails"
    )
    private boolean unread;

    @Option(
            names = {"--read"},
            description = "Filter read emails"
    )
    private boolean read;

    @Option(
            names = {"--from"},
            description = "Filter emails from sender (email address or name)"
    )
    private String from;

    @Option(
            names = {"--subject"},
            description = "Filter emails with subject containing text"
    )
    private String subject;

    @Option(
            names = {"--body"},
            description = "Filter emails with body containing text"
    )
    private String body;

    @Option(
            names = {"--to"},
            description = "Filter emails sent to recipient"
    )
    private String to;

    @Option(
            names = {"--cc"},
            description = "Filter emails CC'd to recipient"
    )
    private String cc;

    @Option(
            names = {"--received-after"},
            description = "Filter emails received after date (format: yyyy-MM-dd)"
    )
    private String receivedAfter;

    @Option(
            names = {"--received-before"},
            description = "Filter emails received before date (format: yyyy-MM-dd)"
    )
    private String receivedBefore;

    @Option(
            names = {"--sent-after"},
            description = "Filter emails sent after date (format: yyyy-MM-dd)"
    )
    private String sentAfter;

    @Option(
            names = {"--sent-before"},
            description = "Filter emails sent before date (format: yyyy-MM-dd)"
    )
    private String sentBefore;

    @Option(
            names = {"--text"},
            description = "Output results in plain text format (default is JSON)"
    )
    private boolean text;

    @Override
    public Integer call() {
        try {
            EmailConfig config = EmailConfig.load();
            EmailClient client = createEmailClient(config);

            EmailSearch search = buildSearchTerm();

            List<Message> messages;
            if (search != null) {
                messages = client.listEmails(folder, search.build());
            } else {
                messages = client.listEmails(folder);
            }

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

    private EmailSearch buildSearchTerm() {
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

    private EmailSearch combineSearch(EmailSearch existing, EmailSearch newSearch) {
        if (existing == null) {
            return newSearch;
        }
        return existing.and(newSearch);
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

    private EmailClient createEmailClient(EmailConfig config) {
        return EmailClientBuilder.builder()
                .hostname(config.getHostname())
                .imapPort(config.getImapPort())
                .smtpPort(config.getSmtpPort())
                .user(config.getUser())
                .password(config.getPassword())
                .build();
    }
}

