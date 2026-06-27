package com.aryan.omybott.dto.response;

import com.aryan.omybott.entities.Bot;
import com.aryan.omybott.enums.DocumentStatus;
import com.aryan.omybott.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocRespDTO {

    private UUID id;

    private String fileName;

    private DocumentType fileType;

    private String mimeType;

    private Long fileSizeBytes;

    private DocumentStatus status;

    private Integer chunkCount;

    private String failureReason;

}
