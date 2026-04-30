package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "achievements")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Achievement {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
