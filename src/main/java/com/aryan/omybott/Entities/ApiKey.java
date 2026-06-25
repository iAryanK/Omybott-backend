package com.aryan.omybott.Entities;

import com.aryan.omybott.enums.ApiKeyStatus;
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
@Table(name = "api_key")
public class ApiKey extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String hashedKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    private Instant lastUsedAt;

    private Instant revokedAt;

    private ApiKeyStatus Status = ApiKeyStatus.ACTIVE;

}
