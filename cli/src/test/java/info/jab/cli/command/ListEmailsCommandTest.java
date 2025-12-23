package info.jab.cli.command;

import info.jab.email.EmailClient;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ListEmailsCommand.
 */
@ExtendWith(MockitoExtension.class)
class ListEmailsCommandTest {

    @Mock
    private EmailClient mockEmailClient;

    @Mock
    private Message mockMessage1;

    @Mock
    private Message mockMessage2;

    private ListEmailsCommand command;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        command = new ListEmailsCommand(mockEmailClient);

        // Setup mock messages
        when(mockMessage1.getFrom()).thenReturn(new Address[]{
            new InternetAddress("sender1@example.com", "Sender One")
        });
        when(mockMessage1.getSubject()).thenReturn("Test Subject 1");
        when(mockMessage1.getSentDate()).thenReturn(new Date());

        when(mockMessage2.getFrom()).thenReturn(new Address[]{
            new InternetAddress("sender2@example.com", "Sender Two")
        });
        when(mockMessage2.getSubject()).thenReturn("Test Subject 2");
        when(mockMessage2.getSentDate()).thenReturn(new Date());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void shouldHaveCorrectCommandAnnotation() {
        // Given/When/Then
        assertTrue(ListEmailsCommand.class.isAnnotationPresent(picocli.CommandLine.Command.class));
        picocli.CommandLine.Command annotation = ListEmailsCommand.class.getAnnotation(picocli.CommandLine.Command.class);
        assertNotNull(annotation);
        assertEquals("list-emails", annotation.name());
    }

    @Test
    void shouldHaveConstructorForDependencyInjection() {
        // Given/When
        ListEmailsCommand cmd = new ListEmailsCommand(null);

        // Then
        assertNotNull(cmd);
    }

    @Test
    void shouldListEmailsSuccessfullyInJsonFormat() throws Exception {
        // Given
        List<Message> messages = Arrays.asList(mockMessage1, mockMessage2);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).listEmails(eq("INBOX"), isNull());
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("\"folder\"");
        assertThat(output).contains("\"INBOX\"");
        assertThat(output).contains("\"count\"");
        assertThat(output).contains("\"emails\"");
    }

    @Test
    void shouldListEmailsSuccessfullyInTextFormat() throws Exception {
        // Given
        List<Message> messages = Arrays.asList(mockMessage1, mockMessage2);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--text");

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).listEmails(eq("INBOX"), isNull());
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Emails in folder 'INBOX'");
        assertThat(output).contains("Test Subject 1");
        assertThat(output).contains("Test Subject 2");
    }

    @Test
    void shouldHandleEmptyEmailList() throws Exception {
        // Given
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).listEmails(eq("INBOX"), isNull());
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("No emails found in folder: INBOX");
    }

    @Test
    void shouldHandleEmptyEmailListWithTextFormat() throws Exception {
        // Given
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--text");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("No emails found in folder: INBOX");
    }

    @Test
    void shouldHandleExceptionWhenListingEmails() throws Exception {
        // Given
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenThrow(new RuntimeException("Connection failed"));
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isEqualTo(1);
        verify(mockEmailClient, times(1)).listEmails(eq("INBOX"), isNull());
        String output = errorStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Error listing emails");
        assertThat(output).contains("Connection failed");
    }

    @Test
    void shouldHandleMessageWithNullFrom() throws Exception {
        // Given
        when(mockMessage1.getFrom()).thenReturn(null);
        when(mockMessage1.getSubject()).thenReturn("Test Subject");
        when(mockMessage1.getSentDate()).thenReturn(new Date());
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--text");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Unknown");
    }

    @Test
    void shouldHandleMessageWithNullSubject() throws Exception {
        // Given
        when(mockMessage1.getSubject()).thenReturn(null);
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--text");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("(No Subject)");
    }
}
