package com.aryan.omybott.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "workspace")
public class Workspace extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private boolean active = true;

}
