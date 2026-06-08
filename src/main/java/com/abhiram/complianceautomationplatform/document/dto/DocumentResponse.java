package com.abhiram.complianceautomationplatform.document.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DocumentResponse {
    private Long id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String documentUrl;

    private String uploadedBy;

    private LocalDateTime uploadedAt;
}