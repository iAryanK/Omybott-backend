package com.aryan.omybott.util;

import com.aryan.omybott.enums.DocumentType;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public final class DocumentFileUtils {

    private static final Set<String> TEXT_MIME_TYPES = Set.of(
            "text/plain",
            "text/markdown",
            "text/x-markdown"
    );

    private DocumentFileUtils() {
    }

    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        requireFilename(file);
    }

    public static String resolveMimeType(MultipartFile file, String providedMimeType) {
        if (providedMimeType != null && !providedMimeType.isBlank()) {
            return providedMimeType;
        }

        String contentType = file.getContentType();
        if (contentType != null
                && !contentType.isBlank()
                && !MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(contentType)) {
            return contentType;
        }

        return mimeTypeFromFilename(requireFilename(file));
    }

    public static DocumentType resolveDocumentType(String mimeType, String filename) {
        return switch (mimeType) {
            case "application/pdf" -> DocumentType.PDF;
            case "text/plain" -> DocumentType.TXT;
            case "text/markdown", "text/x-markdown" -> DocumentType.MARKDOWN;
            default -> documentTypeFromFilename(filename);
        };
    }

    public static boolean isSupportedMimeType(String mimeType) {
        return "application/pdf".equals(mimeType) || TEXT_MIME_TYPES.contains(mimeType);
    }

    private static DocumentType documentTypeFromFilename(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return DocumentType.PDF;
        }
        if (lower.endsWith(".txt")) {
            return DocumentType.TXT;
        }
        if (lower.endsWith(".md") || lower.endsWith(".markdown")) {
            return DocumentType.MARKDOWN;
        }
        throw new IllegalArgumentException("Unsupported file type: " + filename);
    }

    private static String mimeTypeFromFilename(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lower.endsWith(".txt")) {
            return "text/plain";
        }
        if (lower.endsWith(".md") || lower.endsWith(".markdown")) {
            return "text/markdown";
        }
        throw new IllegalArgumentException("Unsupported file extension: " + filename);
    }

    private static String requireFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Uploaded file must have a filename");
        }
        return filename;
    }

}
