/**
 * Enumeration of predefined error message templates used across the application.
 * <p>
 * Each enum constant holds a message template that may contain placeholders (e.g., {@code %s})
 * to be formatted dynamically at runtime.
 * <p>
 */
public enum ErrorMessageEnum {
    /**
     * Error message shown when a file cannot be written.
     */
    WRITE_TO_FILE_ERROR("Error: Failed to write file -> %s"),

    /**
     * Error message shown when the output directory could not be created.
     */
    DIRECTORY_CREATE_ERROR("Error: Could not create output directory -> %s"),

    /**
     * Error message shown when the input file does not exist.
     */
    FILE_NOT_FOUND_ERROR("Error: File not found -> %s");

    private String message;

    ErrorMessageEnum(String message) {
        this.message = message;
    }

    /**
     * Returns the raw message template for this error.
     *
     * @return the message template string
     */
    public String message() {
        return this.message;
    }

    /**
     * Formats the message template by injecting the provided argument.
     *
     * @param arg the value to insert into the message placeholder
     * @return the formatted error message
     */
    public String message(String arg) {
        return String.format(this.message, arg);
    }
}
