package com.aryan.omybott.dto.response;

import com.aryan.omybott.enums.BotStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
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

    private String createdAt;

    private String updatedAt;
    
}
