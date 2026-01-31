package com.sca.smartcampusbackend.controller.api;

import com.sca.smartcampusbackend.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for File Upload/Download operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Management", description = "File upload and download endpoints")
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Upload a single file
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload a file", description = "Upload a single file to the server")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(filename)
                .toUriString();

        Map<String, Object> response = new HashMap<>();
        response.put("fileName", filename);
        response.put("originalFileName", file.getOriginalFilename());
        response.put("fileDownloadUri", fileDownloadUri);
        response.put("fileType", file.getContentType());
        response.put("size", file.getSize());
        response.put("message", "File uploaded successfully");

        log.info("File uploaded: {}", filename);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload multiple files
     * POST /api/files/upload-multiple
     */
    @PostMapping("/upload-multiple")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload multiple files", description = "Upload multiple files at once")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        int successCount = 0;

        for (MultipartFile file : files) {
            try {
                fileStorageService.storeFile(file);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            }
        }

        response.put("totalFiles", files.length);
        response.put("uploadedFiles", successCount);
        response.put("message", successCount + " of " + files.length + " files uploaded successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Download a file
     * GET /api/files/download/{filename}
     */
    @GetMapping("/download/{filename:.+}")
    @Operation(summary = "Download a file", description = "Download a file by filename")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // Try to determine file content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            log.info("Could not determine file type for: {}", filename);
        }

        // Default to binary stream if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Delete a file (Admin only)
     * DELETE /api/files/{filename}
     */
    @DeleteMapping("/{filename:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a file", description = "Delete a file by filename (Admin only)")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String filename) {
        boolean deleted = fileStorageService.deleteFile(filename);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "File deleted successfully");
            response.put("filename", filename);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "File not found or could not be deleted");
            response.put("filename", filename);
            return ResponseEntity.notFound().build();
        }
    }
}
