package com.aryan.omybott.dto.response;

import com.aryan.omybott.enums.ApiKeyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyRespDTO {

    private UUID id;

    private String name;

    private Instant lastUsedAt;

    private Instant revokedAt;

    private ApiKeyStatus status;
}
