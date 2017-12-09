package de.repositories;

import de.domain.Spot;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface SpotRepository extends CrudRepository<Spot, Long>{

    /**
     * Custom add query
     * currently in CustomSpotQueries
     * @param spot
     */
    @Query
    public void addSpot(Spot spot);

    /**
     * Custom update
     * currently in CustomSpotQueries
     * @param spot
     */
    @Modifying
    @Query("UPDATE Spot s SET "+
            "s.location = " + "ST_SetSRID(ST_MakePoint(spot.latitude, spot.longitude), 4326), "+
            "s.latitude = :spot.latitude, " +
            "s.longitude = :spot.longitude, " +
            "s.spotHeading = :spot.spotHeading, " +
            "s.intersection = :spot.intersection, " +
            "s.numberOfNeighbours = :spot.numberOfNeighbours, " +
            "s.neighbors = :spot.neighbors, " +
            "s.latitudeSum = :spot.latitudeSum, " +
            "s.longitudeSum = :spot.longitudeSum, " +
            "s.numberCenterCalcPoints = :spot.numberCenterCalcPoints, " +
            "s.headSum = :spot.headSum, " +
            "s.headCalcPoints = :spot.headCalcPoints, " +
            "s.nodeProcessed = :spot.nodeProcessed, " +
            "s.edgeProcessed = :spot.edgeProcessed " +
            "WHERE s.spotID = :spot.spotID")
    public void updateSpot(Spot spot);

    @Query("Select s from Spot s WHERE s.spotID = :spotID")
    public Spot getSpot(Long spotID);

    @Query(value = "Selet * from spot WHERE location && " +
            "ST_Transform(ST_MakeEnvelope(:maxlongitude, :maxlatitude, :minlongitude, :minlatitdue, 4326), 2223);", nativeQuery = true)
    public ArrayList<Spot> getSpots(double maxlongitude, double maxlatitude, double minlongitude, double minlatitdue);

    @Query(value = "SELECT" +
            " * " +
            "FROM spot " +
            "ORDER BY location <-> ST_SetSRID(ST_POINT(:latitude, :longitude), 4326) " +
            "LIMIT 1;", nativeQuery = true)
    List<Spot> getClosestSpot(@Param("latitude")double latitude, @Param("longitude")double longitude);

    @Query
    public void addNeighbour(Long spotID, Long updatedSpotID, boolean intersectionCheck, boolean updatedIntersectionCheck);
}
