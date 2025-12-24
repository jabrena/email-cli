package info.jab.cli.command;

import info.jab.email.EmailClient;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.times;

/**
 * Unit tests for ListEmailsCommand.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        // Empty list outputs JSON format by default
        assertThat(output).contains("\"folder\"");
        assertThat(output).contains("\"INBOX\"");
        assertThat(output).contains("\"count\" : 0");
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

    @Test
    void shouldHandleMessageWithNullSentDate() throws Exception {
        // Given
        when(mockMessage1.getSentDate()).thenReturn(null);
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--text");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).isNotEmpty();
    }

    @Test
    void shouldHandleMessageWithEmptyFromArray() throws Exception {
        // Given
        when(mockMessage1.getFrom()).thenReturn(new Address[0]);
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
    void shouldHandleMessageWithBlankSubject() throws Exception {
        // Given
        when(mockMessage1.getSubject()).thenReturn("   ");
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

    @Test
    void shouldHandleExceptionWhenGettingFrom() throws Exception {
        // Given
        when(mockMessage1.getFrom()).thenThrow(new MessagingException("Error"));
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
    void shouldHandleExceptionWhenGettingSubject() throws Exception {
        // Given
        when(mockMessage1.getSubject()).thenThrow(new MessagingException("Error"));
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

    @Test
    void shouldHandleExceptionWhenGettingSentDate() throws Exception {
        // Given
        when(mockMessage1.getSentDate()).thenThrow(new MessagingException("Error"));
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--text");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).isNotEmpty();
    }

    @Test
    void shouldHandleExceptionWhenProcessingMessage() throws Exception {
        // Given - Message that throws exception from getFrom(), which is caught by inner try-catch
        when(mockMessage1.getFrom()).thenThrow(new RuntimeException("Unexpected error"));
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--text");

        // Then
        assertThat(exitCode).isZero();
        // Exception from getFrom() is caught by inner try-catch and handled gracefully
        // The message is still processed with "Unknown" as the from value
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Unknown");
    }

    @Test
    void shouldHandleEmptyListWithSearchCriteria() throws Exception {
        // Given
        when(mockEmailClient.listEmails(eq("INBOX"), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX", "--from", "test@example.com", "--text");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("No emails found matching the criteria");
    }

    @Test
    void shouldHandleMessageWithNullFromInJson() throws Exception {
        // Given
        when(mockMessage1.getFrom()).thenReturn(null);
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("\"from\" : \"Unknown\"");
    }

    @Test
    void shouldHandleMessageWithNullSubjectInJson() throws Exception {
        // Given
        when(mockMessage1.getSubject()).thenReturn(null);
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("\"subject\" : \"(No Subject)\"");
    }

    @Test
    void shouldHandleMessageWithNullSentDateInJson() throws Exception {
        // Given
        // When sentDate is null, the code uses new Date() as default, so sentDate will never be null in EmailInfo
        // This test verifies the code handles null sentDate gracefully by using current date
        when(mockMessage1.getSentDate()).thenReturn(null);
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isZero();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        // The code uses new Date() as default when sentDate is null, so sentDate will have a value
        assertThat(output).contains("\"sentDate\"");
        assertThat(output).doesNotContain("\"sentDate\" : null");
    }

    @Test
    void shouldHandleExceptionInJsonOutput() throws Exception {
        // Given
        when(mockMessage1.getFrom()).thenThrow(new RuntimeException("Error"));
        List<Message> messages = Collections.singletonList(mockMessage1);
        when(mockEmailClient.listEmails(eq("INBOX"), isNull())).thenReturn(messages);
        CommandLine commandLine = new CommandLine(command);

        // When
        int exitCode = commandLine.execute("INBOX");

        // Then
        assertThat(exitCode).isZero();
        // Should still output JSON even if some messages fail
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).isNotEmpty();
    }
}
