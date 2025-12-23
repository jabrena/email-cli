package info.jab.cli.command;

import info.jab.email.EmailClient;
import info.jab.email.EmailClientBuilder;
import picocli.CommandLine.Command;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Command to list all folders in the email store.
 */
@Command(
        name = "list-folders",
        description = "List all folders in the email store"
)
public class ListFoldersCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        try {
            EmailConfig config = EmailConfig.load();
            EmailClient client = createEmailClient(config);

            List<String> folders = client.listFolders();

            if (folders.isEmpty()) {
                System.out.println("No folders found.");
                return 0;
            }

            System.out.println("Folders:");
            for (String folder : folders) {
                System.out.println("  - " + folder);
            }

            return 0;
        } catch (Exception e) {
            System.err.println("Error listing folders: " + e.getMessage());
            e.printStackTrace();
            return 1;
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

