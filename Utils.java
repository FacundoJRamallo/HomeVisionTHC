import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for processing structured data files in a custom archival format.
 * <p>
 * This class provides static methods to parse a bundled file, extract individual file
 * sections based on byte-level headers, and save them as separate output files.
 * </p>
 * <p>
 * The output files are written to the <code>./output</code> directory.
 * </p>
 */
public class Utils {

    /**
     * Private constructor to prevent instantiation.
     * This class is intended to be used only via static methods.
     */
    private Utils() {

    }

    private static final List<String> files = new ArrayList<String>();
    
    /**
     * Processes the given archival input file by parsing its structure,
     * identifying individual embedded files, and saving them to the <code>./output</code> directory.
     *
     * This method expects the input file to contain one or more sections, each preceded by a
     * known header marker. The method reads the full file into memory, identifies each embedded
     * file (based on markers and metadata), and delegates extraction and saving to other helpers.
     *
     * @param fileName the name of the file to be processed
     * @throws RuntimeException if the file cannot be read or processed
     */
    public static void processFile(String fileName) {
        Path outputDir = Path.of(Constants.OUTPUT_DIRECTORY);

        createDirectory(outputDir);

        File file = new File(fileName);

        try (InputStream input = new FileInputStream(file)) {

            byte[] data = readFileContent(input);

            int offset = findHeaderOcurrence(0, Constants.SECTION_MARKER, data);

            while(offset < data.length) {
                offset = processFilesData(offset, data, outputDir);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println("Content saved into ./output directory");
        printMetadata(files);
    }

    /**
     * Reads all bytes from the given InputStream.
     *
     * @param input the input stream to read from
     * @return a byte array containing the entire content of the stream
     * @throws IOException if an I/O error occurs
     */
    public static byte[] readFileContent(InputStream input) throws IOException {
        byte[] data = input.readAllBytes();
        return data;
    }


    /**
     * Checks whether the byte sequence at a given offset in a buffer matches a given header pattern.
     *
     * @param header the byte sequence to match
     * @param dataBuffer the data buffer to search within
     * @param offset the offset in the buffer where the comparison starts
     * @return true if the header matches the data at the given offset, false otherwise
     */
    private static boolean matchesHeader(byte[] header, byte[] dataBuffer, int offset) {
        
        if (offset + header.length > dataBuffer.length) {
            return false;
        }

        for (int i = 0; i < header.length; i++) {
            if (dataBuffer[offset + i] != header[i]) {
                return false;
            }
        }
    
        return true;
    }


    /**
     * Extracts a printable string value following a known header from the given byte array.
     *
     * @param headerName the logical name of the header
     * @param offset the starting index of the value in the data
     * @param data the data array containing the value
     * @return the string value following the header
     */
    private static String getHeaderValue(String headerName, int offset, byte[] data) {
        byte b = data[offset];
        StringBuilder sb = new StringBuilder();

        while(isPrintable(b)){
            sb.append((char) b);
            b = data[++offset];
        }

        return sb.toString();
    }


    /**
     * Processes a single logical file block starting at the given offset by locating
     * metadata headers and extracting the file content.
     *
     * @param offset the offset where the current file block begins
     * @param data the complete data buffer
     * @param outputDir directory where the files should be placed
     * @return the offset of the next file block (or end of buffer if none)
     */
    public static int processFilesData(int offset, byte[] data, Path outputDir) {
        if (offset > 0) {

            offset = findHeaderOcurrence(offset, Constants.EXTENSION_HEADER, data);
            String extension = getHeaderValue("Extension", offset + Constants.EXTENSION_HEADER_LEN, data);

            offset = findHeaderOcurrence(offset, Constants.FILENAME_HEADER, data);
            String filename = getHeaderValue("Filename", offset + Constants.FILENAME_HEADER_LEN, data);

            files.add(filename);

            offset = findHeaderOcurrence(offset, Constants.SECTION_START_DATA, data);
            offset = offset + Constants.SECTION_START_DATA_LEN - 1;

            offset = saveContent(offset, data, filename, extension, outputDir);
          }

        return offset;
    }


    /**
     * Finds the first occurrence of a given header pattern in the data starting from a position.
     *
     * @param pos the starting offset for the search
     * @param header the byte sequence to match
     * @param data the data array to search within
     * @return the offset of the first match found, or the data length if not found
     */
    public static int findHeaderOcurrence(int pos, byte[] header, byte[] data) {

        for (int i = pos; i < data.length - header.length; i++) {
            if (matchesHeader(header, data, i)) {
                return i;
            }
        }

        return data.length;
    }


    /**
     * Extracts and saves the content of a logical file from the buffer into the output directory.
     *
     * @param offset the offset where the file content starts
     * @param data the full data buffer
     * @param filename the name of the output file (with extension)
     * @param extension the file extension used to determine text/binary format
     * @param outputDir directory where the files should be placed
     * @return the offset of the next header after this file
     * @throws IOException if an error occurs while saving the file
     */
    private static int saveContent(int offset,  byte[] data, String filename, String extension, Path outputDir) {
        int nextStartingPoint = findHeaderOcurrence(offset, Constants.SECTION_MARKER, data);
    
        if (nextStartingPoint > 0) {
            byte[] content = Arrays.copyOfRange(data, offset + Constants.TRASH_BYTES_AFTER_META, nextStartingPoint);

            Path path = outputDir.resolve(filename);

            writeContent(path, content, extension);
        }

        return nextStartingPoint;
    }


    /**
     * Checks whether a byte corresponds to a printable ASCII character.
     *
     * @param b the byte to check
     * @return true if the byte is in the printable ASCII range (32–126), false otherwise
     */
    private static boolean isPrintable(byte b) {
        int u = b & 0xFF;
        return u >= 32 && u <= 126;
    }


    /**
     * Determines whether a file extension corresponds to a textual file format.
     *
     * @param ext the file extension (without dot)
     * @return true if the extension is known to be textual, false otherwise
     */
    private static boolean isTextual(String ext) {
        return switch (ext.toLowerCase()) {
            case "xml", "txt", "json", "csv", "html" -> true;
            default -> false;
        };
    }


    /**
     * Prints a simple file tree structure of the extracted files inside the output directory.
     *
     * The output is formatted to visually represent the list of files stored in the "./output" folder.
     * Each file is printed with a tree-like prefix.
     *
     * Example:
     * output/
     * ├── file1.txt
     * ├── image.jpg
     * └── document.xml
     *
     * @param files a list of extracted filenames to display
     */
    private static void printMetadata(List<String> files) {
            System.out.println("output/");
            for (String file : files) {
                System.out.println("├── " + file);
        }
    }


    /**
     * Ensures that the specified directory exists by creating it if necessary.
     * 
     * If the directory (or any necessary parent directories) cannot be created due to an I/O error,
     * the method prints an error message and terminates the program.
     *
     * @param outputDir The path of the directory to create.
     */
    private static void createDirectory(Path outputDir) {
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            System.err.println("Error: Could not create output directory " + outputDir);
            System.exit(1);
        }
    }

    /**
     * Writes the given content to a specified file path, handling both text and binary formats.
     * 
     * If the file extension indicates a textual format (such as XML, TXT, etc.), the content is
     * interpreted as UTF-8 encoded text. Otherwise, it is written as raw binary.
     * 
     * In case of an I/O error during the writing process, the method logs the error and terminates
     * the program.
     *
     * @param path      The destination file path where the content will be written.
     * @param content   The file content as a byte array.
     * @param extension The file extension (used to determine whether the content is textual or binary).
     */
    private static void writeContent(Path path, byte[] content, String extension) {
        try {
            if (isTextual(extension)){
                String contentFormatted = new String(content, StandardCharsets.UTF_8);
                Files.writeString(path, contentFormatted, StandardCharsets.UTF_8);
            } else {
                Files.write(path, content);
            }
        } catch (IOException e) {
            System.err.println("Error: Failed to write file -> " + path);
            System.exit(1);
        }
    }
}
