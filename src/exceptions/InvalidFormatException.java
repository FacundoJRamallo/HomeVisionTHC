package exceptions;

import utils.ErrorMessageEnum;

public class InvalidFormatException extends FileParsingException {
    
    public InvalidFormatException(ErrorMessageEnum error, String message) {
        super(error.message(message));
    }
}
