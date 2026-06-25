package com.aryan.omybott.entities;

import com.aryan.omybott.enums.BotStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bot")
public class Bot extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String welcomeMessage;

    @Column(nullable = false)
    private String primaryColor = "#FF0000";

    private Set<String> allowedDomains = new HashSet<>();

    @Column(nullable = false)
    private BotStatus status = BotStatus.ACTIVE;

}
