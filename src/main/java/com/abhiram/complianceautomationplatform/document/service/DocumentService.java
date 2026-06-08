package com.abhiram.complianceautomationplatform.document.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.abhiram.complianceautomationplatform.assignment.repository.ComplianceAssignmentRepository;
import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;
import com.abhiram.complianceautomationplatform.compliance.repository.ComplianceRepository;
import com.abhiram.complianceautomationplatform.document.dto.DocumentResponse;
import com.abhiram.complianceautomationplatform.document.dto.DownloadUrlResponse;
import com.abhiram.complianceautomationplatform.document.entity.ComplianceDocument;
import com.abhiram.complianceautomationplatform.document.repository.ComplianceDocumentRepository;
import com.abhiram.complianceautomationplatform.exception.BusinessException;
import com.abhiram.complianceautomationplatform.exception.ResourceNotFoundException;
import com.abhiram.complianceautomationplatform.role.RoleConstants;
import com.abhiram.complianceautomationplatform.security.CustomUserPrincipal;
import com.abhiram.complianceautomationplatform.user.entity.User;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final ComplianceDocumentRepository documentRepository;

    private final ComplianceRepository complianceRepository;

    private final ComplianceAssignmentRepository complianceAssignmentRepository;

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Transactional
    public DocumentResponse uploadDocument(
            Long complianceId,
            MultipartFile file,
            Authentication authentication)
            throws IOException {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        User currentUser = principal.getUser();

        Compliance compliance = complianceRepository.findById(complianceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compliance not found"));

        boolean assigned = complianceAssignmentRepository
                .existsByComplianceAndAssignedTo(
                        compliance,
                        currentUser);

        boolean isDepartmentManager = RoleConstants.DEPARTMENT_MANAGER.equals(
                currentUser.getRole().getName());

        if (!assigned && !isDepartmentManager) {
            throw new BusinessException(
                    "You are not allowed to upload documents");
        }

        String s3Key = UUID.randomUUID()
                + "-"
                + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(
                        file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(
                        file.getBytes()));

        String documentUrl = "s3://"
                + bucketName
                + "/"
                + s3Key;

        ComplianceDocument document = ComplianceDocument.builder()
                .fileName(
                        file.getOriginalFilename())
                .fileType(
                        file.getContentType())
                .fileSize(
                        file.getSize())
                .s3Key(s3Key)
                .documentUrl(documentUrl)
                .uploadedAt(
                        LocalDateTime.now())
                .uploadedBy(
                        currentUser)
                .compliance(
                        compliance)
                .build();

        document = documentRepository.save(
                document);

        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(
                        document.getFileName())
                .fileType(
                        document.getFileType())
                .fileSize(
                        document.getFileSize())
                .documentUrl(
                        document.getDocumentUrl())
                .uploadedBy(
                        currentUser.getName())
                .uploadedAt(
                        document.getUploadedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByCompliance(
            Long complianceId) {

        Compliance compliance = complianceRepository.findById(complianceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compliance not found"));

        return documentRepository
                .findByCompliance(compliance)
                .stream()
                .map(document -> DocumentResponse.builder()
                        .id(document.getId())
                        .fileName(document.getFileName())
                        .fileType(document.getFileType())
                        .fileSize(document.getFileSize())
                        .documentUrl(document.getDocumentUrl())
                        .uploadedBy(
                                document.getUploadedBy()
                                        .getName())
                        .uploadedAt(
                                document.getUploadedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public DownloadUrlResponse generateDownloadUrl(
            Long documentId) {
        ComplianceDocument document = documentRepository.findById(
                documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found"));
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(document.getS3Key())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(
                        Duration.ofMinutes(5))
                .getObjectRequest(
                        getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                presignRequest);

        return DownloadUrlResponse.builder()
                .downloadUrl(
                        presignedRequest.url()
                                .toString())
                .build();
    }
}
