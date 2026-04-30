package com.lithoapp.analysis.service.validation;

import com.lithoapp.analysis.exception.InvalidPdfUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class PdfUploadValidatorTest {

    private PdfUploadValidator validator;

    // Minimal valid PDF header followed by dummy body
    private static final byte[] VALID_PDF_BYTES = buildValidPdfBytes(100);
    private static final byte[] VALID_PDF_BYTES_LARGE = buildValidPdfBytes(11 * 1024 * 1024); // 11 MB

    @BeforeEach
    void setUp() {
        validator = new PdfUploadValidator();
        ReflectionTestUtils.setField(validator, "maxPdfSizeMb", 10);
    }

    // ── Happy path ────────────────────────────────────────────────────────

    @Test
    void valid_pdf_is_accepted() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "blood_test.pdf", "application/pdf", VALID_PDF_BYTES);
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    void octet_stream_with_valid_magic_bytes_is_accepted() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "blood_test.pdf", "application/octet-stream", VALID_PDF_BYTES);
        assertDoesNotThrow(() -> validator.validate(file));
    }

    // ── Empty / null ──────────────────────────────────────────────────────

    @Test
    void empty_file_is_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("empty"));
    }

    // ── Extension ─────────────────────────────────────────────────────────

    @Test
    void txt_extension_is_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "report.txt", "application/pdf", VALID_PDF_BYTES);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("PDF files"));
    }

    @Test
    void pdf_extension_is_case_insensitive() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "REPORT.PDF", "application/pdf", VALID_PDF_BYTES);
        assertDoesNotThrow(() -> validator.validate(file));
    }

    // ── Magic bytes ───────────────────────────────────────────────────────

    @Test
    void fake_pdf_with_wrong_content_is_rejected() {
        byte[] fakeContent = "This is totally not a PDF".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "fake.pdf", "application/pdf", fakeContent);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("magic bytes") || ex.getMessage().contains("valid PDF"));
    }

    // ── File size ─────────────────────────────────────────────────────────

    @Test
    void oversized_pdf_is_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "huge.pdf", "application/pdf", VALID_PDF_BYTES_LARGE);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("exceeds the maximum"));
    }

    // ── Unsafe filenames ──────────────────────────────────────────────────

    @Test
    void path_traversal_forward_slash_is_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "../etc/passwd.pdf", "application/pdf", VALID_PDF_BYTES);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("unsafe path"));
    }

    @Test
    void path_traversal_backslash_is_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "..\\windows\\system32.pdf", "application/pdf", VALID_PDF_BYTES);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("unsafe path"));
    }

    @Test
    void absolute_unix_path_is_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "/etc/shadow.pdf", "application/pdf", VALID_PDF_BYTES);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("unsafe path"));
    }

    // ── Content type ──────────────────────────────────────────────────────

    @Test
    void image_content_type_is_rejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.pdf", "image/png", VALID_PDF_BYTES);
        InvalidPdfUploadException ex = assertThrows(
                InvalidPdfUploadException.class, () -> validator.validate(file));
        assertTrue(ex.getMessage().contains("Unsupported content type"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static byte[] buildValidPdfBytes(int totalSize) {
        byte[] bytes = new byte[Math.max(totalSize, 4)];
        bytes[0] = '%';
        bytes[1] = 'P';
        bytes[2] = 'D';
        bytes[3] = 'F';
        return bytes;
    }
}
