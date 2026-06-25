package com.aryan.omybott.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversation",
        indexes = {
                @Index(name = "idx_conversations_bot_id", columnList = "bot_id"),
                @Index(name = "idx_conversations_visitor_id", columnList = "visitor_id")
        })
public class Conversation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(nullable = false, length = 120)
    private String visitorId;

    @Column(length = 500)
    private String origin;

    private Instant lastMessageAt;

    @Column(nullable = false)
    private boolean playground = false;

}
