package com.aryan.omybott.dto.request;

import com.aryan.omybott.enums.BotStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotReqDTO {

    private String name;

    private String description;

    private String slug;

    private String welcomeMessage;

    private String primaryColor = "#FF0000";

    private Set<String> allowedDomains = new HashSet<>();

    private BotStatus status = BotStatus.ACTIVE;

}
