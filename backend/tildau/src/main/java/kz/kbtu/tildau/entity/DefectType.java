package kz.kbtu.tildau.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "defect_types")
@Data
@Getter
@Setter
public class DefectType {
    @Id
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    public DefectType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public DefectType() {}
    public DefectType(String name) {
        this.name = name;
    }
}
