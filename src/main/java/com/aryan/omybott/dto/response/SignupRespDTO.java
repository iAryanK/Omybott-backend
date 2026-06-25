package com.aryan.omybott.dto.response;

import java.util.UUID;

public record SignupRespDTO(
        UUID id,
        String accessToken,
        String refreshToken
) {
}
