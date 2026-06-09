package com.abhiram.complianceautomationplatform.document.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abhiram.complianceautomationplatform.document.dto.DocumentResponse;
import com.abhiram.complianceautomationplatform.document.dto.DownloadUrlResponse;
import com.abhiram.complianceautomationplatform.document.service.DocumentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

        private final DocumentService documentService;

        @PostMapping(value = "/upload/{complianceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public DocumentResponse uploadDocument(
                        @PathVariable Long complianceId,
                        @RequestParam("file") MultipartFile file,
                        Authentication authentication)
                        throws IOException {

                return documentService.uploadDocument(
                                complianceId,
                                file,
                                authentication);
        }

        @GetMapping("/compliance/{complianceId}")
        public List<DocumentResponse> getDocumentsByCompliance(
                        @PathVariable Long complianceId,
                        Authentication authentication) {

                return documentService.getDocumentsByCompliance(
                                complianceId,
                                authentication);
        }

        @GetMapping("/download/{documentId}")
        public DownloadUrlResponse downloadDocument(
                        @PathVariable Long documentId,
                        Authentication authentication) {

                return documentService.generateDownloadUrl(
                                documentId,
                                authentication);
        }
}