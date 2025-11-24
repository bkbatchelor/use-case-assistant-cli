package com.usecaseassistant.storage;

import com.usecaseassistant.domain.UseCase;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Repository for persisting and retrieving use cases from the file system.
 * Uses atomic file operations to ensure data integrity.
 */
public class UseCaseRepository {
    private final Path storageDirectory;
    private final Serializer serializer;

    /**
     * Creates a repository with the default storage location.
     */
    public UseCaseRepository() {
        this(getDefaultStorageDirectory(), new Serializer());
    }

    /**
     * Creates a repository with a custom storage location and serializer.
     * 
     * @param storageDirectory the directory where use cases will be stored
     * @param serializer the serializer for JSON conversion
     */
    public UseCaseRepository(Path storageDirectory, Serializer serializer) {
        this.storageDirectory = storageDirectory;
        this.serializer = serializer;
        ensureStorageDirectoryExists();
    }

    private static Path getDefaultStorageDirectory() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, ".usecase-assistant", "use-cases");
    }

    private void ensureStorageDirectoryExists() {
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException e) {
            throw new StorageException("Failed to create storage directory: " + storageDirectory, e);
        }
    }

    /**
     * Saves a use case to the file system using atomic write operations.
     * 
     * @param useCase the use case to save
     * @throws StorageException if the save operation fails
     */
    public void save(UseCase useCase) {
        if (useCase == null) {
            throw new IllegalArgumentException("UseCase cannot be null");
        }

        Path filePath = getFilePath(useCase.getId());
        Path tempFile = null;

        try {
            // Serialize to JSON
            String json = serializer.serialize(useCase);

            // Write to temporary file first
            tempFile = Files.createTempFile(storageDirectory, "usecase-", ".tmp");
            Files.writeString(tempFile, json, StandardOpenOption.WRITE);

            // Atomic move to final location
            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

        } catch (SerializationException e) {
            cleanupTempFile(tempFile);
            throw new StorageException("Failed to serialize use case: " + e.getMessage(), e);
        } catch (IOException e) {
            cleanupTempFile(tempFile);
            throw new StorageException("Failed to save use case to file: " + filePath, e);
        }
    }

    /**
     * Loads a use case from the file system.
     * 
     * @param id the unique identifier of the use case
     * @return the loaded use case
     * @throws StorageException if the load operation fails
     */
    public UseCase load(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        Path filePath = getFilePath(id);

        if (!Files.exists(filePath)) {
            throw new StorageException("Use case not found: " + id);
        }

        try {
            String json = Files.readString(filePath);
            return serializer.deserialize(json);
        } catch (SerializationException e) {
            throw new StorageException("Failed to deserialize use case: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new StorageException("Failed to read use case file: " + filePath, e);
        }
    }

    /**
     * Loads all use cases from the storage directory.
     * 
     * @return list of all use cases
     * @throws StorageException if the operation fails
     */
    public List<UseCase> loadAll() {
        List<UseCase> useCases = new ArrayList<>();

        try (Stream<Path> paths = Files.list(storageDirectory)) {
            paths.filter(path -> path.toString().endsWith(".json"))
                 .forEach(path -> {
                     try {
                         String json = Files.readString(path);
                         UseCase useCase = serializer.deserialize(json);
                         useCases.add(useCase);
                     } catch (IOException e) {
                         throw new StorageException("Failed to read file: " + path, e);
                     } catch (SerializationException e) {
                         throw new StorageException("Failed to deserialize file: " + path, e);
                     }
                 });
        } catch (IOException e) {
            throw new StorageException("Failed to list use cases in directory: " + storageDirectory, e);
        }

        return useCases;
    }

    /**
     * Deletes a use case from the file system.
     * Note: Confirmation should be handled by the caller.
     * 
     * @param id the unique identifier of the use case to delete
     * @throws StorageException if the delete operation fails
     */
    public void delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        Path filePath = getFilePath(id);

        if (!Files.exists(filePath)) {
            throw new StorageException("Use case not found: " + id);
        }

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new StorageException("Failed to delete use case file: " + filePath, e);
        }
    }

    /**
     * Checks if a use case exists in storage.
     * 
     * @param id the unique identifier of the use case
     * @return true if the use case exists, false otherwise
     */
    public boolean exists(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return Files.exists(getFilePath(id));
    }

    private Path getFilePath(String id) {
        return storageDirectory.resolve(id + ".json");
    }

    private void cleanupTempFile(Path tempFile) {
        if (tempFile != null) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
                // Best effort cleanup
            }
        }
    }
}
