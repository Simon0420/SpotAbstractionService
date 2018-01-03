package de.domains.domainAux;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by simon on 29.07.2017.
 */
@Entity
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    public Long id;

    public String name;

    @Column(nullable = true)
    public int realID;

    public AppUser(){}

    public AppUser(String name){
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRealID() {
        return realID;
    }

    public void setRealID(int realID) {
        this.realID = realID;
    }
}
