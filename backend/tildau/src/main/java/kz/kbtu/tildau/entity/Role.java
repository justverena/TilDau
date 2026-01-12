package kz.kbtu.tildau.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role")
@Data
@Getter
@Setter
public class Role {

    @Id
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public Role() {}
    public Role(String name) {
        this.name = name;
    }

}