package exceptions;

import utils.ErrorMessageEnum;

public class FileMissingException extends FileParsingException {
    
    public FileMissingException(String fileName) {
        super(ErrorMessageEnum.FILE_NOT_FOUND_ERROR.message(fileName));
    }
}
