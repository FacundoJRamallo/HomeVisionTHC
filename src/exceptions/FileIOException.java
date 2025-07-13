package exceptions;

import java.io.IOException;

import utils.ErrorMessageEnum;

/**
 * Exception thrown when an I/O error occurs during file parsing.
 * <p>
 * This exception wraps lower-level {@link java.io.IOException} errors and enriches them
 * with a standardized error message provided via {@link ErrorMessageEnum}.
 * It typically indicates failures such as reading from or writing to files or directories.
 * </p>
 */
public class FileIOException extends FileParsingException {
    
    /**
     * Constructs a new {@code FileIOException} using a standardized error message.
     *
     * @param error     the predefined error type from {@code ErrorMessageEnum}
     * @param fileName  the name or path of the file involved in the operation
     * @param cause     the underlying {@link IOException} that triggered this exception
     */
    public FileIOException(ErrorMessageEnum error, String fileName, Throwable cause) {
        super(error.message(fileName), cause);
    }
}
