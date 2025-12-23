package info.jab.cli.command;

import info.jab.email.EmailClient;
import jakarta.mail.search.SearchTerm;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeleteEmailsCommand.
 */
@ExtendWith(MockitoExtension.class)
class DeleteEmailsCommandTest {

    @Mock
    private EmailClient mockEmailClient;

    private DeleteEmailsCommand command;
    private CommandLine commandLine;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        command = new DeleteEmailsCommand(mockEmailClient);
        commandLine = new CommandLine(command);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void shouldHaveCorrectCommandAnnotation() {
        // Given/When/Then
        assertTrue(DeleteEmailsCommand.class.isAnnotationPresent(picocli.CommandLine.Command.class));
        picocli.CommandLine.Command annotation = DeleteEmailsCommand.class.getAnnotation(picocli.CommandLine.Command.class);
        assertNotNull(annotation);
        assertEquals("delete-emails", annotation.name());
    }

    @Test
    void shouldHaveConstructorForDependencyInjection() {
        // Given/When
        DeleteEmailsCommand cmd = new DeleteEmailsCommand(null);

        // Then
        assertNotNull(cmd);
    }

    @Test
    void shouldDeleteEmailsSuccessfully() throws Exception {
        // Given
        when(mockEmailClient.deleteEmails(eq("INBOX"), any(SearchTerm.class))).thenReturn(true);

        // When
        int exitCode = commandLine.execute("INBOX", "--from", "test@example.com");

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).deleteEmails(eq("INBOX"), any(SearchTerm.class));
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Emails deleted successfully from folder: INBOX");
    }

    @Test
    void shouldHandleNoEmailsFound() throws Exception {
        // Given
        when(mockEmailClient.deleteEmails(eq("INBOX"), any(SearchTerm.class))).thenReturn(false);

        // When
        int exitCode = commandLine.execute("INBOX", "--from", "test@example.com");

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).deleteEmails(eq("INBOX"), any(SearchTerm.class));
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("No emails found matching the criteria in folder: INBOX");
    }

    @Test
    void shouldRejectDeletionWithoutFilters() throws Exception {
        // Given/When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isEqualTo(1);
        verify(mockEmailClient, never()).deleteEmails(anyString(), any(SearchTerm.class));
        String output = errorStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Error: At least one filter option must be specified");
    }

    @Test
    void shouldHandleExceptionWhenDeletingEmails() throws Exception {
        // Given
        when(mockEmailClient.deleteEmails(eq("INBOX"), any(SearchTerm.class)))
            .thenThrow(new RuntimeException("Connection failed"));

        // When
        int exitCode = commandLine.execute("INBOX", "--from", "test@example.com");

        // Then
        assertThat(exitCode).isEqualTo(1);
        verify(mockEmailClient, times(1)).deleteEmails(eq("INBOX"), any(SearchTerm.class));
        String output = errorStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Error deleting emails");
        assertThat(output).contains("Connection failed");
    }

    @Test
    void shouldHandleIllegalArgumentException() throws Exception {
        // Given
        when(mockEmailClient.deleteEmails(eq("INBOX"), any(SearchTerm.class)))
            .thenThrow(new IllegalArgumentException("Invalid search term"));

        // When
        int exitCode = commandLine.execute("INBOX", "--from", "test@example.com");

        // Then
        assertThat(exitCode).isEqualTo(1);
        verify(mockEmailClient, times(1)).deleteEmails(eq("INBOX"), any(SearchTerm.class));
        String output = errorStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Error: Invalid search term");
    }

    @Test
    void shouldAcceptSubjectFilter() throws Exception {
        // Given
        when(mockEmailClient.deleteEmails(eq("INBOX"), any(SearchTerm.class))).thenReturn(true);

        // When
        int exitCode = commandLine.execute("INBOX", "--subject", "Important");

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).deleteEmails(eq("INBOX"), any(SearchTerm.class));
    }

    @Test
    void shouldAcceptUnreadFilter() throws Exception {
        // Given
        when(mockEmailClient.deleteEmails(eq("INBOX"), any(SearchTerm.class))).thenReturn(true);

        // When
        int exitCode = commandLine.execute("INBOX", "--unread");

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).deleteEmails(eq("INBOX"), any(SearchTerm.class));
    }
}
