package com.abhiram.complianceautomationplatform.document.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadUrlResponse {
    private String downloadUrl;
}
