package exceptions;

import utils.ErrorMessageEnum;

/**
 * Exception thrown when a file does not conform to the expected format.
 * <p>
 * This includes cases such as missing headers, corrupted structure, or unrecognized content.
 * The error message is generated using a corresponding value from {@link ErrorMessageEnum}.
 * </p>
 */
public class InvalidFormatException extends FileParsingException {
    

    /**
     * Constructs a new {@code InvalidFormatException} with a formatted error message.
     *
     * @param error   the predefined format error from {@code ErrorMessageEnum}
     * @param message context or description to be included in the final message
     */
    public InvalidFormatException(ErrorMessageEnum error, String message) {
        super(error.message(message));
    }
}
