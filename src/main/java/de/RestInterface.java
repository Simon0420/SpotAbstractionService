package de;

import de.domains.domain.Spot;
import de.repositories.preDBRepositories.SpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestInterface {

    @Autowired
    SpotRepository sr;

    @RequestMapping(value="/spotservice/getSpot", method = RequestMethod.GET)
    public Spot getSpotInDB(@RequestParam(value="id", defaultValue="0")long id){
        return sr.getSpot(id);
    }

    @RequestMapping(value="/spotservice/getAllSpots", method = RequestMethod.GET)
    public List<Spot> getSpotsInDB(){
        return sr.getAllSpots();
    }
}
