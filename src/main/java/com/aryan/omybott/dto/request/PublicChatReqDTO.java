package com.aryan.omybott.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicChatReqDTO {

    private UUID conversationId;

    private String message;

}
