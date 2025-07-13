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
        
        String fileName = validateAndRetrieveFileNameAegument(args);

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
     * Validates that the program was executed with at least one command-line argument.
     * 
     * If no arguments are provided, prints a usage message and terminates the program
     * with a non-zero exit code.
     *
     * @param args the array of command-line arguments passed to the program
     */
    private static String validateAndRetrieveFileNameAegument(String[] args) {
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
     * Checks that the specified file exists in the file system.
     * 
     * If the file does not exist, prints an error message and terminates the program
     * with a non-zero exit code.
     *
     * @param fileName the name (or path) of the input file to validate
     */
    private static void validateFile(String fileName) {
        Path inputFile = Path.of(fileName);

        if (!Files.exists(inputFile)) {
            System.err.println("Error: File not found -> " + fileName);
            System.exit(1);
        }
    }

    /**
     * Checks if a given filename ends with a dot followed by the specified format (extension).
     *
     * Eg.: "<fileName>.env"
     *  
     * @param name   The filename to check.
     * @param format The expected file extension, without the dot (e.g. "env").
     * @return true if the filename has the correct extension; false otherwise.
     */
    private static boolean isValidFormat(String name, String format) {
        return name != null && format != null 
                && name.endsWith(Constants.DOT_MARKER + format);
    }
}
