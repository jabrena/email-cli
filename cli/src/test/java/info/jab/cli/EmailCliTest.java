package info.jab.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailCli structure and command registration.
 * Note: Command execution tests are limited due to Mockito/ByteBuddy
 * limitations on Java 25/GraalVM that prevent mocking EmailClient interface.
 */
class EmailCliTest {

    private PrintStream originalOut;
    private PrintStream originalErr;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errorStream;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalErr = System.err;
        outputStream = new ByteArrayOutputStream();
        errorStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void shouldCreateEmailCliInstanceWithNullCommands() {
        // Given/When
        EmailCli instance = new EmailCli(null, null);

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
        EmailCli cli = new EmailCli(null, null);

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
    void shouldHaveCorrectCommandDescription() {
        // Given/When/Then
        picocli.CommandLine.Command annotation = EmailCli.class.getAnnotation(picocli.CommandLine.Command.class);
        assertNotNull(annotation);
        assertEquals("Email CLI tool for listing folders, emails, and filtering", annotation.description()[0]);
    }


    @Test
    void shouldAcceptNullCommandsForTesting() {
        // Given/When
        EmailCli cli = new EmailCli(null, null);

        // Then
        assertNotNull(cli);
        // Constructor accepts null commands - EmailCli will use annotation-based commands
    }

    @Test
    void shouldHaveConstructorForCommandInjection() {
        // Given/When
        EmailCli cli = new EmailCli(null, null);

        // Then
        assertNotNull(cli);
        // Constructor signature allows dependency injection for testing
    }
}
