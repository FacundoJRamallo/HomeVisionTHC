package exceptions;

import utils.ErrorMessageEnum;

/**
 * Exception thrown when the specified input file cannot be found.
 * <p>
 * This exception uses a predefined error message from {@link ErrorMessageEnum}
 * to indicate that the file path provided for parsing does not exist or is inaccessible.
 * </p>
 */
public class FileMissingException extends FileParsingException {
    
    /**
     * Constructs a new {@code FileMissingException} with a standardized message.
     *
     * @param fileName the name or path of the missing file
     */
    public FileMissingException(String fileName) {
        super(ErrorMessageEnum.FILE_NOT_FOUND_ERROR.message(fileName));
    }
}
