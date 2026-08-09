package com.aryan.omybott.dto.response;

import com.aryan.omybott.enums.BotStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotRespDTO {

    private UUID id;

    private String name;

    private String description;

    private String slug;

    private String welcomeMessage;

    private String primaryColor;

    private Set<String> allowedDomains;

    private BotStatus status;

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy", timezone = "IST")
    private Instant createdAt;

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy", timezone = "IST")
    private Instant updatedAt;

}
