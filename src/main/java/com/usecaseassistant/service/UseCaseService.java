package com.usecaseassistant.service;

import com.usecaseassistant.domain.UseCase;
import com.usecaseassistant.storage.StorageException;
import com.usecaseassistant.storage.UseCaseRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service layer for managing use case operations.
 * Coordinates validation and storage operations.
 */
@Service
public class UseCaseService {
    private final UseCaseRepository repository;
    private final ValidationService validationService;

    public UseCaseService(UseCaseRepository repository, ValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }

    /**
     * Creates a new use case after validation.
     * 
     * @param useCase the use case to create (without ID)
     * @return the created use case with generated ID
     * @throws ValidationException if validation fails
     * @throws StorageException if save operation fails
     */
    public UseCase createUseCase(UseCase useCase) {
        // Validate the use case
        ValidationResult validationResult = validationService.validateUseCase(useCase);
        if (!validationResult.isValid()) {
            throw new ValidationException("Use case validation failed", validationResult.getErrors());
        }

        // Generate ID if not present
        UseCase useCaseWithId = useCase;
        if (useCase.getId() == null || useCase.getId().trim().isEmpty()) {
            useCaseWithId = UseCase.builder()
                .id(UUID.randomUUID().toString())
                .title(useCase.getTitle())
                .primaryActor(useCase.getPrimaryActor())
                .goalLevel(useCase.getGoalLevel())
                .designScope(useCase.getDesignScope())
                .trigger(useCase.getTrigger())
                .preconditions(useCase.getPreconditions())
                .postconditions(useCase.getPostconditions())
                .successGuarantees(useCase.getSuccessGuarantees())
                .mainScenario(useCase.getMainScenario())
                .extensions(useCase.getExtensions())
                .stakeholders(useCase.getStakeholders())
                .build();
        }

        // Save to repository
        repository.save(useCaseWithId);
        return useCaseWithId;
    }

    /**
     * Loads a use case by ID.
     * 
     * @param id the unique identifier of the use case
     * @return the loaded use case
     * @throws StorageException if the use case is not found or cannot be loaded
     */
    public UseCase loadUseCase(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        return repository.load(id);
    }

    /**
     * Updates an existing use case after validation.
     * 
     * @param useCase the updated use case (must have existing ID)
     * @return the updated use case
     * @throws ValidationException if validation fails
     * @throws StorageException if the use case doesn't exist or save fails
     */
    public UseCase updateUseCase(UseCase useCase) {
        if (useCase == null) {
            throw new IllegalArgumentException("UseCase cannot be null");
        }
        if (useCase.getId() == null || useCase.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("UseCase ID cannot be null or empty for update");
        }

        // Verify the use case exists
        if (!repository.exists(useCase.getId())) {
            throw new StorageException("Use case not found: " + useCase.getId());
        }

        // Validate the updated use case
        ValidationResult validationResult = validationService.validateUseCase(useCase);
        if (!validationResult.isValid()) {
            throw new ValidationException("Use case validation failed", validationResult.getErrors());
        }

        // Save the updated use case
        repository.save(useCase);
        return useCase;
    }

    /**
     * Deletes a use case by ID.
     * Note: Confirmation should be handled by the caller (CLI layer).
     * 
     * @param id the unique identifier of the use case to delete
     * @throws StorageException if the use case doesn't exist or deletion fails
     */
    public void deleteUseCase(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        repository.delete(id);
    }

    /**
     * Lists all use cases sorted alphabetically by title.
     * 
     * @return list of all use cases sorted by title
     * @throws StorageException if loading fails
     */
    public List<UseCase> listUseCases() {
        List<UseCase> useCases = repository.loadAll();
        return useCases.stream()
            .sorted(Comparator.comparing(UseCase::getTitle, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
    }

    /**
     * Checks if a use case exists.
     * 
     * @param id the unique identifier of the use case
     * @return true if the use case exists, false otherwise
     */
    public boolean exists(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        return repository.exists(id);
    }
}
