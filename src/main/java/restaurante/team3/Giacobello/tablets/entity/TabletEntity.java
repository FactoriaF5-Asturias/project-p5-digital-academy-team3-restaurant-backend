package restaurante.team3.Giacobello.tablets.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "tablets")
public class TabletEntity {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 30)
    private String name;

    public TabletEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public TabletEntity() {
    }

    public Integer getId(){
        return id;
    }

    public String getName() {
        return name;
    }
}
