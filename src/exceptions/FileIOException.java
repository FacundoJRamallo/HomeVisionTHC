package exceptions;

import utils.ErrorMessageEnum;

public class FileIOException extends FileParsingException {
    
    public FileIOException(ErrorMessageEnum error, String fileName, Throwable cause) {
        super(error.message(fileName), cause);
    }
}
