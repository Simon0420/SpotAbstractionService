package de.domainAux;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by simon on 29.07.2017.
 */
@Entity
public class Route implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    public Long id;

    @ManyToOne(targetEntity=AppUser.class, fetch=FetchType.EAGER)
    @JoinColumn(name="userId")
    public AppUser user;

    @Transient
    public GpsPoint[] route;
    
    @Column(nullable = true)
    public int realID;

    public Route(){}

    public GpsPoint[] getRoute() {
        return route;
    }

    public void setRoute(GpsPoint[] route) {
        this.route = route;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public int getRealID() {
        return realID;
    }

    public void setRealID(int realID) {
        this.realID = realID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
