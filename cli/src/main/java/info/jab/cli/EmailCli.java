package info.jab.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import info.jab.cli.command.DeleteEmailsCommand;
import info.jab.cli.command.ListEmailsCommand;
import info.jab.cli.command.ListFoldersCommand;
import info.jab.email.EmailClient;
import info.jab.email.EmailClientBuilder;
import info.jab.email.EmailConfig;

/**
 * Main CLI application for email operations.
 */
@Command(
        name = "email-cli",
        description = "Email CLI tool for listing folders, emails, filtering, and deleting",
        mixinStandardHelpOptions = true,
        usageHelpAutoWidth = true
)
public class EmailCli implements Callable<Integer> {

    private final ListFoldersCommand listFoldersCommand;
    private final ListEmailsCommand listEmailsCommand;
    private final DeleteEmailsCommand deleteEmailsCommand;

    /**
     * Constructor that accepts EmailConfig and creates EmailClient, then injects it into commands.
     *
     * @param config the EmailConfig instance
     */
    public EmailCli(EmailConfig config) {
        EmailClient emailClient = EmailClientBuilder.builder()
                .hostname(config.getHostname())
                .imapPort(config.getImapPort())
                .smtpPort(config.getSmtpPort())
                .user(config.getUser())
                .password(config.getPassword())
                .build();

        this.listFoldersCommand = new ListFoldersCommand(emailClient);
        this.listEmailsCommand = new ListEmailsCommand(emailClient);
        this.deleteEmailsCommand = new DeleteEmailsCommand(emailClient);
    }

    /**
     * Constructor for testing with dependency injection.
     *
     * @param listFoldersCommand the list-folders command instance (if null, uses annotation-based command)
     * @param listEmailsCommand the list-emails command instance (if null, uses annotation-based command)
     * @param deleteEmailsCommand the delete-emails command instance (if null, uses annotation-based command)
     */
    public EmailCli(ListFoldersCommand listFoldersCommand, ListEmailsCommand listEmailsCommand, DeleteEmailsCommand deleteEmailsCommand) {
        this.listFoldersCommand = listFoldersCommand;
        this.listEmailsCommand = listEmailsCommand;
        this.deleteEmailsCommand = deleteEmailsCommand;
    }

    @Override
    public Integer call() {
        // If no subcommand is provided, show usage
        CommandLine.usage(this, System.out);
        return 0;
    }

    public static void main(String[] args) {
        EmailCli cli = new EmailCli(EmailConfig.load());
        CommandLine cmd = createCommandLine(cli);
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

    /**
     * Creates a CommandLine instance with proper subcommand registration.
     * If custom commands are provided, they are used; otherwise, annotation-based commands are used.
     *
     * @param cli the EmailCli instance
     * @return configured CommandLine instance
     */
    static CommandLine createCommandLine(EmailCli cli) {
        CommandLine commandLine = new CommandLine(cli);

        // Register subcommands - always register manually to use pre-created instances with injected dependencies
        if (cli.listFoldersCommand != null) {
            commandLine.addSubcommand("list-folders", cli.listFoldersCommand);
        }
        if (cli.listEmailsCommand != null) {
            commandLine.addSubcommand("list-emails", cli.listEmailsCommand);
        }
        if (cli.deleteEmailsCommand != null) {
            commandLine.addSubcommand("delete-emails", cli.deleteEmailsCommand);
        }

        return commandLine;
    }
}

