package exceptions;

/**
 * Base exception class for errors encountered during file parsing operations.
 * <p>
 * This exception serves as the root of a custom exception hierarchy for parsing errors,
 * including cases such as missing files, unreadable content, or invalid file format.
 * </p>
 * <p>
 * All custom parsing-related exceptions should extend from this class.
 * </p>
 */
public class FileParsingException extends RuntimeException {

    /**
     * Constructs a new {@code FileParsingException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public FileParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code FileParsingException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the cause of the exception
     * @param cause   the underlying exception that caused this error
     */
    public FileParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
