import java.nio.file.Files;
import java.nio.file.Path;

import exceptions.FileParsingException;
import utils.Constants;
import utils.Utils;

/**
 * Entry point of the archival file parser application.

 * This class validates input arguments, checks file existence, and delegates
 * processing to utility methods for extracting structured data from a custom
 * archival format. The extracted files are saved into the <code>./output</code> directory.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * java FileParser <inputfile>
 * }</pre>
 *
 * <h2>Arguments</h2>
 * <ul>
 *   <li><b>inputfile</b> – the path to the archive file to be parsed (e.g., <code>sample.env</code>)</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * java FileParser sample.env
 * }</pre>
 *
 * @author Facundo Ramallo
 */
public class FileParser {
    
    public static void main(String[] args) {
        
        String fileName = validateAndRetrieveFileNameArgument(args);

        validateFile(fileName);

        try {
            Utils.processFile(fileName);
        }
        catch (FileParsingException e) {
            System.err.println("Parser error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Validates that at least one command-line argument is provided and that it matches
     * the expected input format (e.g., ".env").
     * <p>
     * If the validation fails, an error message is printed and the program exits.
     *
     * @param args the command-line arguments
     * @return the input file name extracted from the arguments
     */
    private static String validateAndRetrieveFileNameArgument(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java FileParser <inputfile>.env");
            System.exit(1);
        }

        String fileName = args[0];

        if (!isValidFormat(fileName, Constants.ENV_FORMAT)) {
            System.out.println("Invalid format type. The extension should be \"" + Constants.ENV_FORMAT + "\"");
            System.exit(1);
        }

        return fileName;
    }

    /**
     * Verifies that the input file exists in the file system.
     * <p>
     * If the file does not exist, an error message is printed and the application exits.
     *
     * @param fileName the name or path of the input file to validate
     */
    private static void validateFile(String fileName) {
        Path inputFile = Path.of(fileName);

        if (!Files.exists(inputFile)) {
            System.err.println("Error: File not found -> " + fileName);
            System.exit(1);
        }
    }

    /**
     * Checks if the given file name ends with the specified format (extension).
     * <p>
     * This method does a basic suffix check using the dot marker.
     *
     * @param name   the file name to check
     * @param format the expected extension (e.g., {@code "env"}), without the dot
     * @return {@code true} if the file name ends with the given format; {@code false} otherwise
     */
    private static boolean isValidFormat(String name, String format) {
        return name != null && format != null 
                && name.endsWith(Constants.DOT_MARKER + format);
    }
}
