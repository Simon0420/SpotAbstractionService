package de.repositories.postDBRepositories;

import de.domain.Route;
import de.domain.Spot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface PostDbSpotRepository extends CrudRepository<Spot, Long> {

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


}
