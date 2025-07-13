package utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.FileIOException;
import exceptions.FileMissingException;
import exceptions.FileParsingException;
import exceptions.InvalidFormatException;

/**
 * Utility class for processing structured archive-like data files containing embedded files.
 * <p>
 * This class provides static methods to parse a specially structured binary input file,
 * extract individual file sections based on header markers, and save them into an output directory.
 * </p>
 * <p>
 * Each embedded file block is identified by a set of markers (headers and metadata), and is
 * extracted either as plain text or raw binary, depending on its extension.
 * Extracted files are saved into the {@code ./output} directory by default.
 * </p>
 * 
 * <h2>Example flow:</h2>
 * <ul>
 *   <li>Open and read a binary file.</li>
 *   <li>Search for section headers that identify embedded files.</li>
 *   <li>Extract metadata (filename, extension) and content for each block.</li>
 *   <li>Write the content to disk using proper format detection.</li>
 * </ul>
 * 
 * <p>
 * This class throws specialized exceptions (such as {@link FileIOException}, {@link FileMissingException},
 * {@link InvalidFormatException}) when parsing fails, to allow better error handling.
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
     * Parses a custom-structured binary file and extracts embedded content blocks.
     * <p>
     * The method identifies embedded files by looking for section headers,
     * then delegates extraction and saving logic to helper methods.
     * </p>
     * <p>
     * If the file is empty or the expected section marker is missing, an
     * {@link InvalidFormatException} is thrown. The output directory is created
     * only after validation passes.
     * </p>
     *
     * @param fileName the path to the input file
     * @throws FileParsingException if the file cannot be read or is invalid
     */
    public static void processFile(String fileName) throws FileParsingException {
        
        File file = validateFile(fileName);

        try (InputStream input = new FileInputStream(file)) {

            byte[] data = readFileContent(input);

            int offset = findHeaderOcurrence(0, Constants.SECTION_MARKER, data);

            if (offset >= data.length) {
                throw new InvalidFormatException(ErrorMessageEnum.EMPTY_FILE_ERROR, fileName);
            }

            Path outputDir = Path.of(Constants.OUTPUT_DIRECTORY);
            createDirectory(outputDir);

            while(offset < data.length) {
                offset = processFilesData(offset, data, outputDir);
            }
        } catch (FileNotFoundException e) {
            throw new FileMissingException(file.getName());
        } catch (IOException ex) {
            throw new FileIOException(ErrorMessageEnum.READ_WRITE_FILE_ERROR, file.getName(), ex);
        }
        
        System.out.println(String.format("Content saved into %s directory", Constants.OUTPUT_DIRECTORY));
        printMetadata(files);
    }

    /**
     * Validates the existence, readability, and non-emptiness of a file given its filename.
     *
     * <p>This method checks if the file exists and can be read. It also verifies that the
     * file is not empty (i.e., its length is greater than zero).</p>
     *
     * <p>If any of these validations fail, it throws a specific {@link FileParsingException} subclass
     * indicating the error:
     * <ul>
     *   <li>{@link FileMissingException} if the file is missing or not readable.</li>
     *   <li>{@link InvalidFormatException} if the file is empty.</li>
     * </ul>
     * </p>
     *
     * @param fileName the path or name of the file to validate
     * @return a validated {@link File} object representing the file
     * @throws FileParsingException if the file does not exist, is not readable, or is empty
     */
    private static File validateFile(String fileName) throws FileParsingException{
        File file = new File(fileName);

        if (!file.exists() || !file.canRead()) {
            throw new FileMissingException(fileName);
        }

        if (file.length() == 0) {
            throw new InvalidFormatException(ErrorMessageEnum.EMPTY_FILE_ERROR, fileName);
        }

        return file;
    }

    /**
     * Reads all bytes from an input stream.
     *
     * @param input the input stream to read from
     * @return the full byte array read from the stream
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
     * Parses and extracts a single embedded file section starting at the given offset.
     * <p>
     * It locates the extension, filename, and content markers, and then delegates
     * writing the extracted content to the output directory.
     * </p>
     *
     * @param offset    the starting offset in the data buffer
     * @param data      the complete file data buffer
     * @param outputDir the directory where extracted files will be saved
     * @return the updated offset pointing to the next file section (or end of buffer)
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
     * Searches the data buffer for the first occurrence of a specific byte header starting at a given offset.
     *
     * @param pos    the starting offset for the search
     * @param header the byte pattern to search for
     * @param data   the data buffer
     * @return the offset where the header is found, or the buffer length if not found
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
            System.out.println(Constants.OUTPUT_DIRECTORY + "/");
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
            throw new FileIOException(ErrorMessageEnum.DIRECTORY_CREATE_ERROR, outputDir.toString(), e);
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
            throw new FileIOException(ErrorMessageEnum.READ_WRITE_FILE_ERROR, path.toString(), e);
        }
    }
}
