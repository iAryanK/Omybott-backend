package com.aryan.omybott.dto.request;

import com.aryan.omybott.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocReqDTO {

    private String fileName;

    private DocumentType fileType;

    private String mimeType;

    private Long fileSizeBytes;

}
