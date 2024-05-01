package school.sptech.filesexample.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import school.sptech.filesexample.entity.FileEntity;
import school.sptech.filesexample.model.FileModel;
import school.sptech.filesexample.repository.FileEntityRepository;

import java.io.IOException;

/**
 * Service class for handling file operations.
 * Integrates storage operations with database operations for file metadata.
 * Author: Diego Brito
 */
@Slf4j
@Service
public class FileService {

    private final StorageService storageService;
    private final FileEntityRepository fileEntityRepository;

    public FileService(StorageService storageService, FileEntityRepository fileEntityRepository) {
        this.storageService = storageService;
        this.fileEntityRepository = fileEntityRepository;
    }

    /**
     * Saves a file to the storage and records its metadata in the database.
     *
     * @param file the file to save
     * @return the saved file entity
     * @throws ResponseStatusException if saving the file fails
     * @author: Diego Brito
     */
    @Transactional
    public FileEntity save(MultipartFile file) {
        try {
            FileEntity fileEntity = createFileEntity(file);
            FileEntity savedEntity = this.fileEntityRepository.save(fileEntity);

            storageService.save(savedEntity.getStoredName(), file.getBytes());

            return savedEntity;
        } catch (IOException e) {
            log.error("[FILE-ERROR] Failed to save file", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Failed to save file due to IO issues", e);
        }
    }

    /**
     * Loads a file's data and metadata based on its database ID.
     *
     * @param fileId the ID of the file to load
     * @return the file model containing file data and metadata
     * @throws ResponseStatusException if the file is not found or cannot be loaded
     * @author: Diego Brito
     */
    public FileModel load(int fileId) {
        FileEntity fileEntity = this.fileEntityRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        byte[] bytes = storageService.load(fileEntity.getStoredName());
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

        FileModel model = createFileModel(fileEntity, bytes);

        return model;
    }

    /**
     * Deletes a file from storage and its metadata from the database.
     *
     * @param fileId the ID of the file to delete
     * @throws ResponseStatusException if the file is not found or cannot be deleted
     * @author: Diego Brito
     */
    public void delete(int fileId) {
        FileEntity fileEntity = this.fileEntityRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        storageService.delete(fileEntity.getStoredName());
        this.fileEntityRepository.delete(fileEntity);
    }

    private FileEntity createFileEntity(MultipartFile file) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setOriginalName(file.getOriginalFilename());
        fileEntity.setSize(file.getSize());
        fileEntity.setContentType(file.getContentType());
        fileEntity.setStoredName(generateStoredName(file));

        return fileEntity;
    }

    private String generateStoredName(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }

    private FileModel createFileModel(FileEntity fileEntity, byte[] bytes) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);

        FileModel model = new FileModel();
        model.setName(fileEntity.getOriginalName());
        model.setContentType(fileEntity.getContentType());
        model.setSize(fileEntity.getSize());
        model.setContent(byteArrayResource);

        return model;
    }
}
