package com.aryan.omybott.dto.response;

public record LoginRespDTO(
        String accessToken,
        String refreshToken
) {
}
