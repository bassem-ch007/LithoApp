package com.lithoapp.analysis.service.validation;

import com.lithoapp.analysis.exception.InvalidPdfUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Validates that an uploaded file is a genuine, safe PDF before it reaches storage.
 *
 * Checks performed (in order):
 *  1. File must not be null or empty.
 *  2. Original filename must be present and must end with ".pdf" (case-insensitive).
 *  3. Filename must not contain path-traversal sequences or absolute-path separators.
 *  4. Content-Type must be "application/pdf" or "application/octet-stream".
 *     If octet-stream, magic bytes are still required to be valid.
 *  5. First four bytes of content must equal the PDF magic header "%PDF".
 *  6. File size must not exceed the configured maximum.
 */
@Slf4j
@Component
public class PdfUploadValidator {

    private static final byte[] PDF_MAGIC = {'%', 'P', 'D', 'F'};
    private static final long BYTES_PER_MB = 1024L * 1024L;

    @Value("${analysis.upload.max-pdf-size-mb:10}")
    private int maxPdfSizeMb;

    public void validate(MultipartFile file) {
        rejectIfNullOrEmpty(file);
        String filename = resolveFilename(file);
        rejectUnsafeFilename(filename);
        rejectBadExtension(filename);
        rejectIncompatibleContentType(file);
        rejectIfMagicBytesInvalid(file);
        rejectIfOversized(file);
    }

    // ── Individual checks ─────────────────────────────────────────────────

    private void rejectIfNullOrEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPdfUploadException("Uploaded file must not be empty.");
        }
    }

    private String resolveFilename(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            throw new InvalidPdfUploadException("Uploaded file must have an original filename.");
        }
        return name;
    }

    private void rejectUnsafeFilename(String filename) {
        if (filename.contains("../") || filename.contains("..\\")
                || filename.startsWith("/") || filename.startsWith("\\")
                || (filename.length() > 1 && filename.charAt(1) == ':')) {
            throw new InvalidPdfUploadException(
                    "Filename contains unsafe path characters and was rejected.");
        }
    }

    private void rejectBadExtension(String filename) {
        if (!filename.toLowerCase().endsWith(".pdf")) {
            throw new InvalidPdfUploadException(
                    "Only PDF files are accepted. Received filename: " + filename);
        }
    }

    private void rejectIncompatibleContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new InvalidPdfUploadException("File content type is missing.");
        }
        if (!contentType.equals("application/pdf")
                && !contentType.equals("application/octet-stream")) {
            throw new InvalidPdfUploadException(
                    "Unsupported content type: " + contentType +
                    ". Expected application/pdf.");
        }
    }

    private void rejectIfMagicBytesInvalid(MultipartFile file) {
        byte[] header = new byte[PDF_MAGIC.length];
        try (InputStream in = file.getInputStream()) {
            int read = in.read(header);
            if (read < PDF_MAGIC.length) {
                throw new InvalidPdfUploadException(
                        "File is too short to be a valid PDF.");
            }
        } catch (IOException e) {
            throw new InvalidPdfUploadException(
                    "Could not read uploaded file content.");
        }

        for (int i = 0; i < PDF_MAGIC.length; i++) {
            if (header[i] != PDF_MAGIC[i]) {
                throw new InvalidPdfUploadException(
                        "File does not appear to be a valid PDF (invalid magic bytes). " +
                        "Only genuine PDF documents are accepted.");
            }
        }
    }

    private void rejectIfOversized(MultipartFile file) {
        long maxBytes = (long) maxPdfSizeMb * BYTES_PER_MB;
        if (file.getSize() > maxBytes) {
            throw new InvalidPdfUploadException(
                    "File size (" + toMb(file.getSize()) + " MB) exceeds the maximum allowed size of "
                    + maxPdfSizeMb + " MB.");
        }
    }

    private String toMb(long bytes) {
        return String.format("%.2f", (double) bytes / BYTES_PER_MB);
    }
}
