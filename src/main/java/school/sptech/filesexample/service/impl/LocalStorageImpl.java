package school.sptech.filesexample.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import school.sptech.filesexample.service.StorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Service class for handling local file storage operations.
 * This class is defined to be active under the 'local' profile.
 * @author: Diego Brito
 */
@Slf4j
@Service
@Profile("local")
public class LocalStorageImpl implements StorageService {

    // Define the base path where files will be stored, defaulting to the system's temporary directory.
    private final Path basePath = Paths.get(System.getProperty("java.io.tmpdir"), "files");

    /**
     * Save a file with the given content under the specified file name.
     * This method ensures the directory exists and writes the file content to the resolved path.
     *
     * @param fileName the name of the file to save
     * @param content  the content of the file as a byte array
     * @author: Diego Brito
     */
    @Override
    public void save(String fileName, byte[] content) {
        try {
            // Ensure the directory for the file exists, create it if it doesn't.
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }

            // Resolve the complete path for the file within the base path.
            Path resolvedPath = basePath.resolve(fileName);

            // Write the content to the file, creating the file if it doesn't exist.
            Files.write(resolvedPath, content);
        } catch (IOException e) {
            // Log an error if unable to save the file and rethrow as a runtime exception.
            log.error("[FILE-ERROR] Failed to save file", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Load the content of the specified file as a byte array.
     * This method reads all bytes from the resolved file path.
     *
     * @param fileName the name of the file to load
     * @return the content of the file as a byte array
     * @author: Diego Brito
     */
    @Override
    public byte[] load(String fileName) {
        try {
            // Resolve the complete path for the file within the base path.
            Path resolvedPath = basePath.resolve(fileName);

            // Read all bytes from the file and return the content.
            return Files.readAllBytes(resolvedPath);
        } catch (IOException e) {
            // Log an error if unable to load the file and rethrow as a runtime exception.
            log.error("[FILE-ERROR] Failed to load file", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete the specified file.
     * This method removes the file at the resolved path.
     *
     * @param fileName the name of the file to delete
     * @author: Diego Brito
     */
    @Override
    public void delete(String fileName) {
        try {
            // Resolve the complete path for the file within the base path.
            Path resolvedPath = basePath.resolve(fileName);

            // Delete the file.
            Files.delete(resolvedPath);
        } catch (IOException e) {
            // Log an error if unable to delete the file and rethrow as a runtime exception.
            log.error("[FILE-ERROR] Failed to delete file", e);
            throw new RuntimeException(e);
        }
    }
}
