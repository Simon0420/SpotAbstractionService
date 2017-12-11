package de.domain;

import com.vividsolutions.jts.geom.Geometry;
import de.services.InfoBundle;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by simon on 26.07.2017.
 */
@Entity
public class GpsPoint implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    public Long id;

    public long date;

    public double latitude;

    public double longitude;

    public double heading;

    public double speed;

    public double acceleration;

    @Column(columnDefinition="geometry(Point,4326)")
    public Geometry location;

    @ManyToOne(targetEntity=Route.class, fetch=FetchType.EAGER)
    @JoinColumn(name="routeId")
    public Route route;

    @Column(nullable = true)
    public int realID;

    // -----------------------------------------------------------
    // Variables for abstraction with spots
    /**
     * true if GPS-point was already mapped into a spot
     */
    private boolean mappedToSpot;
    /**
     * information about assigned information
     */
    private Spot spot;
    /**
     * information about nearest spot
     */
    @Transient
    private InfoBundle closestSpotInfo;

    public GpsPoint(){}

    public GpsPoint(long date, double latitude, double longitude, double heading, double speed, double acceleration, Geometry location, Route route, int realID) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
        this.speed = speed;
        this.acceleration = acceleration;
        this.location = location;
        this.route = route;
        this.realID = realID;
    }

    public GpsPoint(int realID){
        this.realID = realID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public int getRealID() {
        return realID;
    }

    public void setRealID(int realID) {
        this.realID = realID;
    }

    public Geometry getLocation() {
        return location;
    }

    public void setLocation(Geometry location) {
        this.location = location;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public boolean isMappedToSpot() {
        return mappedToSpot;
    }

    public void setMappedToSpot(boolean mappedToSpot) {
        this.mappedToSpot = mappedToSpot;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }

    public InfoBundle getClosestSpotInfo() {
        return closestSpotInfo;
    }

    public void setClosestSpotInfo(InfoBundle closestSpotInfo) {
        this.closestSpotInfo = closestSpotInfo;
    }
}
