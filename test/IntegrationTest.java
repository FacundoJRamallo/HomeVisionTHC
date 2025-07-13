import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.*;

import org.junit.jupiter.api.*;

import exceptions.FileMissingException;
import exceptions.InvalidFormatException;
import utils.ErrorMessageEnum;
import utils.Utils;

public class IntegrationTest {
    
    private static final String OUTPUT_DIR = "output";
    private static final String RESOURCES_DIR = "../test/resources/";

    @BeforeEach
    public void cleanUpBefore() throws IOException {
        deleteDirectoryIfExists();
    }

    @AfterEach
    public void cleanUpAfter() throws IOException {
        deleteDirectoryIfExists();
    }

    @Test
    public void testSampleEnvCreatesFiles() throws IOException {
        String inputFile = RESOURCES_DIR + "sample.env";
        Utils.processFile(inputFile);

        Path outputPath = Paths.get(OUTPUT_DIR);
        assertTrue(Files.exists(outputPath), "Output directory should be created");

        try (Stream<Path> files = Files.list(outputPath)) {
            long count = files.count();
            assertTrue(count > 0, "Should extract at least one file");
        }
    }

    @Test
    public void testEmptyEnvCreatesNothing() throws IOException {
        String inputFile = "empty.env";
        String relativeFilPath = RESOURCES_DIR + inputFile;

        InvalidFormatException ex = assertThrows(InvalidFormatException.class, () -> {
            Utils.processFile(relativeFilPath);
        });

        assertTrue(ex.getMessage().equals(ErrorMessageEnum.EMPTY_FILE_ERROR.message(relativeFilPath)), 
            "Exception message should reference empty file");
    }

    @Test
    public void testNotExistingEnvReturnsError() {
        String inputFile = "notExisting.env";
        String relativeFilPath = RESOURCES_DIR + inputFile;

        FileMissingException ex = assertThrows(FileMissingException.class, () -> {
            Utils.processFile(relativeFilPath);
        });

        assertTrue(ex.getMessage().equals(ErrorMessageEnum.FILE_NOT_FOUND_ERROR.message(inputFile)), 
            "Exception message should reference missing file");
    }

    private void deleteDirectoryIfExists() throws IOException {
        Path dir = Paths.get(OUTPUT_DIR);
        if (Files.exists(dir)) {
            try (Stream<Path> files = Files.walk(dir)) {
                files.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
    }
}
