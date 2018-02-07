package de.repositories.preDBRepositories;

import de.domains.domain.Spot;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SpotRepository extends CrudRepository<Spot, Long>{

    @Query(value = "Select s from Spot s WHERE s.spotID = :spotID")
    public Spot getSpot(Long spotID);

    @Query(value = "Select s from Spot s")
    public List<Spot> getAllSpots();

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
