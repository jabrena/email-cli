package info.jab.cli.command;

import info.jab.email.EmailClient;
import info.jab.email.EmailSearch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Unit tests for EmailFilterCommand.
 */
@ExtendWith(MockitoExtension.class)
class EmailFilterCommandTest {

    @Mock
    private EmailClient mockEmailClient;

    // Use ListEmailsCommand to test EmailFilterCommand functionality
    private ListEmailsCommand createCommand() {
        return new ListEmailsCommand(mockEmailClient);
    }

    @Test
    void shouldBuildSearchTermWithUnreadFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--unread");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithReadFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--read");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithFromFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--from", "test@example.com");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithSubjectFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--subject", "Important");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithBodyFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--body", "urgent");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithToFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--to", "recipient@example.com");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithCcFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--cc", "cc@example.com");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithReceivedAfterFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--received-after", "2024-01-01");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithReceivedBeforeFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--received-before", "2024-12-31");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithSentAfterFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--sent-after", "2024-01-01");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldBuildSearchTermWithSentBeforeFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--sent-before", "2024-12-31");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldReturnNullWhenNoFiltersSpecified() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankFromFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--from", "   ");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankSubjectFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--subject", "");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankBodyFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--body", "   ");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankToFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--to", "");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankCcFilter() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--cc", "   ");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldCombineMultipleFilters() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--unread", "--from", "test@example.com", "--subject", "Important");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }

    @Test
    void shouldHandleInvalidReceivedAfterDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStreamCaptor));
        CommandLine commandLine = new CommandLine(command);

        try {
            // When
            int exitCode = commandLine.execute("INBOX", "--received-after", "invalid-date");

            // Then
            assertThat(exitCode).isEqualTo(1);
            String errorOutput = errorStreamCaptor.toString(StandardCharsets.UTF_8);
            assertThat(errorOutput).contains("Invalid date format for --received-after");
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void shouldHandleInvalidReceivedBeforeDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStreamCaptor));
        CommandLine commandLine = new CommandLine(command);

        try {
            // When
            int exitCode = commandLine.execute("INBOX", "--received-before", "2024/01/01");

            // Then
            assertThat(exitCode).isEqualTo(1);
            String errorOutput = errorStreamCaptor.toString(StandardCharsets.UTF_8);
            assertThat(errorOutput).contains("Invalid date format for --received-before");
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void shouldHandleInvalidSentAfterDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStreamCaptor));
        CommandLine commandLine = new CommandLine(command);

        try {
            // When
            int exitCode = commandLine.execute("INBOX", "--sent-after", "01-01-2024");

            // Then
            assertThat(exitCode).isEqualTo(1);
            String errorOutput = errorStreamCaptor.toString(StandardCharsets.UTF_8);
            assertThat(errorOutput).contains("Invalid date format for --sent-after");
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void shouldHandleInvalidSentBeforeDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStreamCaptor));
        CommandLine commandLine = new CommandLine(command);

        try {
            // When
            int exitCode = commandLine.execute("INBOX", "--sent-before", "not-a-date");

            // Then
            assertThat(exitCode).isEqualTo(1);
            String errorOutput = errorStreamCaptor.toString(StandardCharsets.UTF_8);
            assertThat(errorOutput).contains("Invalid date format for --sent-before");
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void shouldIgnoreBlankReceivedAfterDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--received-after", "   ");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankReceivedBeforeDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--received-before", "");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankSentAfterDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--sent-after", "   ");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldIgnoreBlankSentBeforeDate() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute("INBOX", "--sent-before", "");
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNull();
    }

    @Test
    void shouldCombineAllFilters() throws Exception {
        // Given
        ListEmailsCommand command = createCommand();
        when(mockEmailClient.listEmails(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(Collections.emptyList());
        CommandLine commandLine = new CommandLine(command);

        // When
        commandLine.execute(
            "INBOX",
            "--unread",
            "--from", "sender@example.com",
            "--subject", "Test",
            "--body", "Content",
            "--to", "recipient@example.com",
            "--cc", "cc@example.com",
            "--received-after", "2024-01-01",
            "--received-before", "2024-12-31",
            "--sent-after", "2024-01-01",
            "--sent-before", "2024-12-31"
        );
        EmailSearch search = command.buildSearchTerm();

        // Then
        assertThat(search).isNotNull();
    }
}

