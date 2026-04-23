package com.lithoapp.analysis.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ─────────────────────────────────────────────────────

    @ExceptionHandler(AnalysisNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(AnalysisNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EpisodeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEpisodeNotFound(EpisodeNotFoundException ex) {
        log.warn("Episode not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(PatientNotFoundException ex) {
        log.warn("Patient not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 503 Service Unavailable ────────────────────────────────────────────

    @ExceptionHandler(PatientServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handlePatientServiceUnavailable(PatientServiceUnavailableException ex) {
        log.error("patient-service unavailable: {}", ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(EpisodeServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleEpisodeServiceUnavailable(EpisodeServiceUnavailableException ex) {
        log.error("episode-service unavailable: {}", ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    // ── 409 Conflict ──────────────────────────────────────────────────────

    @ExceptionHandler(RequestAlreadyCompletedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyCompleted(RequestAlreadyCompletedException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleBadTransition(InvalidStatusTransitionException ex) {
        log.warn("Invalid transition: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Concurrent update conflict: two biologists modified the same StoneResult
     * or AnalysisRequest simultaneously. The client should reload and retry.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        log.warn("Optimistic locking conflict: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT,
                "Concurrent modification detected. Please reload the resource and retry your changes.");
    }

    // ── 422 Unprocessable Entity ──────────────────────────────────────────

    @ExceptionHandler(CompletionNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleCompletionNotAllowed(CompletionNotAllowedException ex) {
        log.warn("Completion not allowed: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /**
     * Raised when the provided episodeId and patientId do not refer to the same case.
     * This protects the patient → episode → analysis consistency chain.
     */
    @ExceptionHandler(EpisodePatientMismatchException.class)
    public ResponseEntity<ErrorResponse> handleEpisodePatientMismatch(EpisodePatientMismatchException ex) {
        log.warn("Episode-patient mismatch: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // ── 400 Bad Request ───────────────────────────────────────────────────

    @ExceptionHandler(InvalidAnalysisTypeOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidType(InvalidAnalysisTypeOperationException ex) {
        log.warn("Invalid analysis type operation: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors.toString(),
                LocalDateTime.now());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSize(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.BAD_REQUEST, "Uploaded file exceeds the maximum allowed size.");
    }

    // ── 500 Internal Server Error ─────────────────────────────────────────

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorage(StorageException ex) {
        log.error("Storage error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "File storage operation failed: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    // ── Builder ───────────────────────────────────────────────────────────

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, LocalDateTime.now()));
    }
}
