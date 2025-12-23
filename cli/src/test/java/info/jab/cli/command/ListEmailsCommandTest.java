package info.jab.cli.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ListEmailsCommand structure.
 * Note: Tests that require EmailClient mocking are skipped due to Java 25/GraalVM compatibility issues.
 */
class ListEmailsCommandTest {

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
        // Constructor with EmailClient parameter should be available for testing
    }
}
