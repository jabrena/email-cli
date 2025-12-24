package info.jab.cli;

import info.jab.cli.command.DeleteEmailsCommand;
import info.jab.cli.command.ListEmailsCommand;
import info.jab.cli.command.ListFoldersCommand;
import info.jab.email.EmailConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EmailCli structure and command registration.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NullAway.Init")
@DisplayName("EmailCli Tests")
class EmailCliTest {

    @Mock
    private ListFoldersCommand mockListFoldersCommand;

    @Mock
    private ListEmailsCommand mockListEmailsCommand;

    @Mock
    private DeleteEmailsCommand mockDeleteEmailsCommand;

    private EmailCli emailCliWithMocks;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        emailCliWithMocks = new EmailCli(mockListFoldersCommand, mockListEmailsCommand, mockDeleteEmailsCommand);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Nested
    @DisplayName("Command Execution Tests")
    class CommandExecutionTests {

        @Test
        @DisplayName("Should execute list-folders command successfully")
        void shouldExecuteListFoldersCommand() throws Exception {
            // Given
            CommandLine commandLine = EmailCli.createCommandLine(emailCliWithMocks);
            when(mockListFoldersCommand.call()).thenReturn(0);

            // When
            int exitCode = commandLine.execute("list-folders");

            // Then
            assertThat(exitCode).isZero();
            verify(mockListFoldersCommand, times(1)).call();
        }

        @Test
        @DisplayName("Should execute list-emails command successfully")
        void shouldExecuteListEmailsCommand() throws Exception {
            // Given
            CommandLine commandLine = EmailCli.createCommandLine(emailCliWithMocks);
            when(mockListEmailsCommand.call()).thenReturn(0);

            // When
            int exitCode = commandLine.execute("list-emails", "INBOX");

            // Then
            assertThat(exitCode).isZero();
            verify(mockListEmailsCommand, times(1)).call();
        }

        @Test
        @DisplayName("Should execute delete-emails command successfully")
        void shouldExecuteDeleteEmailsCommand() throws Exception {
            // Given
            CommandLine commandLine = EmailCli.createCommandLine(emailCliWithMocks);
            when(mockDeleteEmailsCommand.call()).thenReturn(0);

            // When
            int exitCode = commandLine.execute("delete-emails", "INBOX", "--from", "test@example.com");

            // Then
            assertThat(exitCode).isZero();
            verify(mockDeleteEmailsCommand, times(1)).call();
        }

        @Test
        @DisplayName("Should return non-zero exit code for invalid command")
        void shouldReturnNonZeroExitCodeForInvalidCommand() {
            // Given
            CommandLine commandLine = EmailCli.createCommandLine(emailCliWithMocks);

            // When
            int exitCode = commandLine.execute("invalid-command");

            // Then
            assertThat(exitCode).isNotZero();
        }
    }

    @Nested
    @DisplayName("Usage and Help Tests")
    class UsageAndHelpTests {

        @Test
        @DisplayName("Should show usage when no subcommand is provided")
        void shouldShowUsageWhenNoSubcommandProvided() throws Exception {
            // Given
            EmailCli cli = new EmailCli(null, null, null);

            // When
            int exitCode = cli.call();

            // Then
            assertThat(exitCode).isZero();
            String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
            assertThat(output)
                    .contains("email-cli")
                    .contains("Email CLI tool");
            // Note: When commands are null, they won't appear in usage output
        }

        @Test
        @DisplayName("Should show help when --help flag is used")
        void shouldShowHelpWhenHelpFlagIsUsed() {
            // Given
            CommandLine commandLine = EmailCli.createCommandLine(emailCliWithMocks);

            // When
            int exitCode = commandLine.execute("--help");

            // Then
            assertThat(exitCode).isZero();
            String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
            assertThat(output).contains("email-cli");
        }
    }

    @Nested
    @DisplayName("CommandLine Creation Tests")
    class CommandLineCreationTests {

        @Test
        @DisplayName("Should create CommandLine when all commands are null")
        void shouldCreateCommandLineWithAllNullCommands() {
            // Given
            EmailCli cli = new EmailCli(null, null, null);

            // When
            CommandLine commandLine = EmailCli.createCommandLine(cli);

            // Then
            assertThat(commandLine).isNotNull();
            assertThat(commandLine.getSubcommands()).isEmpty();
        }

        @Test
        @DisplayName("Should create CommandLine and register commands when some are null")
        void shouldCreateCommandLineWithPartialNullCommands() {
            // Given
            EmailCli cli = new EmailCli(mockListFoldersCommand, null, mockDeleteEmailsCommand);

            // When
            CommandLine commandLine = EmailCli.createCommandLine(cli);

            // Then
            assertThat(commandLine).isNotNull();
            assertThat(commandLine.getSubcommands()).containsKeys("list-folders", "delete-emails");
            assertThat(commandLine.getSubcommands()).doesNotContainKey("list-emails");
        }

        @Test
        @DisplayName("Should create CommandLine and register all commands when all are provided")
        void shouldCreateCommandLineWithAllCommands() {
            // Given
            EmailCli cli = new EmailCli(mockListFoldersCommand, mockListEmailsCommand, mockDeleteEmailsCommand);

            // When
            CommandLine commandLine = EmailCli.createCommandLine(cli);

            // Then
            assertThat(commandLine).isNotNull();
            assertThat(commandLine.getSubcommands())
                    .containsKeys("list-folders", "list-emails", "delete-emails")
                    .hasSize(3);
        }
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create EmailCli with EmailConfig and register all commands")
        void shouldCreateEmailCliWithEmailConfig() {
            // Given
            EmailConfig config = EmailConfig.forTesting("localhost", 143, 25, "test@example.com", "password");

            // When
            EmailCli cli = new EmailCli(config);

            // Then
            assertThat(cli).isNotNull();
            CommandLine commandLine = EmailCli.createCommandLine(cli);
            assertThat(commandLine).isNotNull();
            assertThat(commandLine.getSubcommands())
                    .containsKeys("list-folders", "list-emails", "delete-emails")
                    .hasSize(3);
        }

        @Test
        @DisplayName("Should create EmailCli with EmailConfig and register commands correctly")
        void shouldCreateEmailCliWithEmailConfigAndRegisterCommands() {
            // Given
            EmailConfig config = EmailConfig.forTesting("localhost", 143, 25, "test@example.com", "password");
            EmailCli cli = new EmailCli(config);

            // When
            CommandLine commandLine = EmailCli.createCommandLine(cli);

            // Then - verify all commands are registered
            assertThat(commandLine.getSubcommands())
                    .containsKeys("list-folders", "list-emails", "delete-emails")
                    .hasSize(3);

            // Verify command help can be accessed (without executing the command)
            CommandLine listFoldersSubcommand = commandLine.getSubcommands().get("list-folders");
            assertThat(listFoldersSubcommand).isNotNull();
            assertThat(listFoldersSubcommand.getCommandName()).isEqualTo("list-folders");
        }

        @Test
        @DisplayName("Should create EmailCli with dependency injection constructor")
        void shouldCreateEmailCliWithDependencyInjection() {
            // Given & When
            EmailCli cli = new EmailCli(mockListFoldersCommand, mockListEmailsCommand, mockDeleteEmailsCommand);

            // Then
            assertThat(cli).isNotNull();
            CommandLine commandLine = EmailCli.createCommandLine(cli);
            assertThat(commandLine).isNotNull();
            assertThat(commandLine.getSubcommands())
                    .containsKeys("list-folders", "list-emails", "delete-emails");
        }
    }
}
