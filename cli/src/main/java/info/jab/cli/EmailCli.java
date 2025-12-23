package info.jab.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import info.jab.cli.command.ListEmailsCommand;
import info.jab.cli.command.ListFoldersCommand;

/**
 * Main CLI application for email operations.
 */
@Command(
        name = "email-cli",
        description = "Email CLI tool for listing folders, emails, and filtering",
        subcommands = {
                ListFoldersCommand.class,
                ListEmailsCommand.class
        },
        mixinStandardHelpOptions = true,
        usageHelpAutoWidth = true
)
public class EmailCli implements Callable<Integer> {

    @Override
    public Integer call() {
        // If no subcommand is provided, show usage
        CommandLine.usage(this, System.out);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new EmailCli()).execute(args);
        System.exit(exitCode);
    }
}

