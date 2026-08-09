package com.aryan.omybott.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceRespDTO {
    private UUID id;
    private String name;
    private String slug;
    private boolean active;

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy", timezone = "IST")
    private Instant createdAt;

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy", timezone = "IST")
    private Instant updatedAt;
}
