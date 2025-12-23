package info.jab.cli.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ListFoldersCommand structure.
 * Note: Tests that require EmailClient mocking are skipped due to Java 25/GraalVM compatibility issues.
 */
class ListFoldersCommandTest {

    @Test
    void shouldHaveCorrectCommandAnnotation() {
        // Given/When/Then
        assertTrue(ListFoldersCommand.class.isAnnotationPresent(picocli.CommandLine.Command.class));
        picocli.CommandLine.Command annotation = ListFoldersCommand.class.getAnnotation(picocli.CommandLine.Command.class);
        assertNotNull(annotation);
        assertEquals("list-folders", annotation.name());
        assertEquals("List all folders in the email store", annotation.description()[0]);
    }

    @Test
    void shouldHaveConstructorForDependencyInjection() {
        // Given/When
        ListFoldersCommand cmd = new ListFoldersCommand(null);

        // Then
        assertNotNull(cmd);
        // Constructor with EmailClient parameter should be available for testing
    }
}
