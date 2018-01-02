package de.repositories.postDBRepositories;


import de.domainAux.Route;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends CrudRepository<Route, Long> {
    @Query("Select r from Route r WHERE r.id = :id")
    public Route getRoute(Long spotID);
}
