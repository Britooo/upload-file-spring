package school.sptech.filesexample.service;

/**
 * Interface defining methods that must be implemented by a storage service class.
 * This interface standardizes the basic file operations across different storage backends such as local filesystems, cloud storage, or any other form of storage.
 *
 * Author: Diego Brito
 */
public interface StorageService {

    /**
     * Saves a file with the specified content under a given file name.
     * Implementations of this method are responsible for writing the byte array to the specified storage medium.
     * It is expected that the method handles all lower-level storage exceptions internally or throws them wrapped in runtime exceptions for uniform handling.
     *
     * @param fileName the name under which the file should be saved; typically a unique identifier
     * @param content the content of the file in byte array form; represents the actual data of the file to be stored
     */
    void save(String fileName, byte[] content);

    /**
     * Loads the content of the file specified by fileName from the storage.
     * This method should retrieve and return the content of the file as a byte array.
     * If the file does not exist or cannot be read, implementations should handle the error appropriately and may throw a runtime exception indicating the issue.
     *
     * @param fileName the name of the file to be loaded
     * @return the content of the file as a byte array, typically read from the storage medium
     */
    byte[] load(String fileName);

    /**
     * Deletes the file specified by fileName from the storage.
     * Implementations should handle all aspects of file deletion, including checking for file existence and handling deletion failures.
     * If the file cannot be deleted or does not exist, implementations may throw a runtime exception to indicate the error.
     *
     * @param fileName the name of the file to be deleted
     */
    void delete(String fileName);
}
