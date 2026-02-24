package kz.kbtu.tildau.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.kbtu.tildau.embeddedId.UserDefectTypeId;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "user_defect_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDefectType {

    @EmbeddedId
    private UserDefectTypeId id;

    @MapsId("userId")
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("defectTypeId")
    @ManyToOne
    @JoinColumn(name = "defect_type_id")
    private DefectType defectType;
}