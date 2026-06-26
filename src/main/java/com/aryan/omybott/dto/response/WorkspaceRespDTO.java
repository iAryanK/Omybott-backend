package com.aryan.omybott.dto.response;

import com.aryan.omybott.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceRespDTO {
    private UUID id;
    private String name;
    private String slug;
    private boolean active;
}
