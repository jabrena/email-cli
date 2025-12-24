package info.jab.cli.command;

import info.jab.email.EmailClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ListFoldersCommand.
 */
@ExtendWith(MockitoExtension.class)
class ListFoldersCommandTest {

    @Mock
    private EmailClient mockEmailClient;

    private ListFoldersCommand command;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        command = new ListFoldersCommand(mockEmailClient);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void shouldListFoldersSuccessfully() throws Exception {
        // Given
        List<String> folders = Arrays.asList("INBOX", "Sent", "Drafts", "Trash");
        when(mockEmailClient.listFolders()).thenReturn(folders);

        // When
        int exitCode = command.call();

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).listFolders();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Folders:");
        assertThat(output).contains("- INBOX");
        assertThat(output).contains("- Sent");
        assertThat(output).contains("- Drafts");
        assertThat(output).contains("- Trash");
    }

    @Test
    void shouldHandleEmptyFolderList() throws Exception {
        // Given
        when(mockEmailClient.listFolders()).thenReturn(Collections.emptyList());

        // When
        int exitCode = command.call();

        // Then
        assertThat(exitCode).isZero();
        verify(mockEmailClient, times(1)).listFolders();
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("No folders found.");
    }

    @Test
    void shouldHandleExceptionWhenListingFolders() throws Exception {
        // Given
        when(mockEmailClient.listFolders()).thenThrow(new RuntimeException("Connection failed"));

        // When
        int exitCode = command.call();

        // Then
        assertThat(exitCode).isEqualTo(1);
        verify(mockEmailClient, times(1)).listFolders();
        String output = errorStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("Error listing folders");
        assertThat(output).contains("Connection failed");
    }
}
