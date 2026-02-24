package kz.kbtu.tildau.embeddedId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUnitProgressId implements Serializable {

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "unit_id", columnDefinition = "uuid")
    private UUID unitId;

}