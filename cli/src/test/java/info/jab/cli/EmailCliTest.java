package info.jab.cli;

import info.jab.cli.command.DeleteEmailsCommand;
import info.jab.cli.command.ListEmailsCommand;
import info.jab.cli.command.ListFoldersCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailCli structure and command registration.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NullAway.Init")
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

    @Test
    void shouldCreateEmailCliInstanceWithNullCommands() {
        // Given/When
        EmailCli instance = new EmailCli(null, null, null);

        // Then
        assertNotNull(instance);
    }

    @Test
    void shouldCreateEmailCliInstanceWithMockedCommands() {
        // Given/When
        EmailCli instance = new EmailCli(mockListFoldersCommand, mockListEmailsCommand, mockDeleteEmailsCommand);

        // Then
        assertNotNull(instance);
    }

    @Test
    void shouldHaveMainMethod() {
        // Given/When/Then - Verify main method exists
        assertDoesNotThrow(() -> {
            EmailCli.class.getMethod("main", String[].class);
        });
    }

    @Test
    void shouldImplementCallable() {
        // Given
        EmailCli cli = new EmailCli(null, null, null);

        // When/Then
        assertTrue(cli instanceof Callable);
    }

    @Test
    void shouldHaveCommandAnnotation() {
        // Given/When/Then
        assertTrue(EmailCli.class.isAnnotationPresent(picocli.CommandLine.Command.class));
        picocli.CommandLine.Command annotation = EmailCli.class.getAnnotation(picocli.CommandLine.Command.class);
        assertNotNull(annotation);
        assertEquals("email-cli", annotation.name());
        assertTrue(annotation.mixinStandardHelpOptions());
    }

    @Test
    void shouldCallListFoldersCommandWhenExecuted() throws Exception {
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
    void shouldCallListEmailsCommandWhenExecuted() throws Exception {
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
    void shouldCallDeleteEmailsCommandWhenExecuted() throws Exception {
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
    void shouldShowUsageWhenNoSubcommandProvided() throws Exception {
        // Given
        EmailCli cli = new EmailCli(null, null, null);

        // When
        int exitCode = cli.call();

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("email-cli");
    }

    @Test
    void shouldShowUsageWhenInvalidCommandProvided() {
        // Given
        CommandLine commandLine = EmailCli.createCommandLine(emailCliWithMocks);

        // When
        int exitCode = commandLine.execute("invalid-command");

        // Then
        assertThat(exitCode).isNotZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Unknown command");
    }
}
