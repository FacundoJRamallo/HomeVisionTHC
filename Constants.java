/**
 * Defines constants used to parse and extract data from a custom archival file format.
 * <p>
 * This class includes byte-level headers and marker sequences used to locate different sections
 * (such as file metadata, filenames, and content blocks) within the binary input data.
 * </p>
 * <p>
 * The constants are used by utility methods to identify boundaries of each embedded file.
 * </p>
 */
public class Constants {
    
    /**
     * Byte marker that indicates the beginning of a new file section: "**%%DOCU"
     */
    public static final byte[] SECTION_MARKER = new byte[] {
        (byte) 0x2A, (byte) 0x2A, (byte) 0x25, (byte) 0x25,
        (byte) 0x44, (byte) 0x4F, (byte) 0x43, (byte) 0x55
    };

    /**
     * Length of the {@link #SECTION_MARKER} in bytes.
     */
    public static final int SECTION_MARKER_LEN = SECTION_MARKER.length;

    /**
     * Byte header used to identify the file name section: "FILENAME/"
     */
    public static final byte[] FILENAME_HEADER = new byte[] {
        (byte) 0x46, (byte) 0x49, (byte) 0x4C, (byte) 0x45, 
        (byte) 0x4E, (byte) 0x41, (byte) 0x4D, (byte) 0x45, (byte) 0x2F
    };

    /**
     * Length of the {@link #FILENAME_HEADER} in bytes.
     */
    public static final int FILENAME_HEADER_LEN = FILENAME_HEADER.length;

    /**
     * Byte marker that indicates the start of the data payload section: "_SIG/D.C."
     */
    public static final byte[] SECTION_START_DATA = new byte[] {
        (byte) 0x5F, (byte) 0x53, (byte) 0x49, (byte) 0x47,
        (byte) 0x2F, (byte) 0x44, (byte) 0x2E, (byte) 0x43, (byte) 0x2E
    };

    /**
     * Length of the {@link #SECTION_START_DATA} in bytes.
     */
    public static final int SECTION_START_DATA_LEN = SECTION_START_DATA.length;

    /**
     * Byte header used to indicate file extension section: "EXT/"
     */
    public static final byte[] EXTENSION_HEADER = new byte[] {
        (byte) 0x45, (byte) 0x58, (byte) 0x54, (byte) 0x2F
    };

    /**
     * Length of the {@link #EXTENSION_HEADER} in bytes.
     */
    public static final int EXTENSION_HEADER_LEN = EXTENSION_HEADER.length;

    /**
     * Number of non-printable or irrelevant bytes to skip after metadata
     * before actual file content begins. This is format-specific.
     */
    public static final int TRASH_BYTES_AFTER_META = 5;
    
    // Prevent instantiation
    private Constants() {}
}
