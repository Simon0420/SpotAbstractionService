package de;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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

    @RequestMapping(value="/spotservice/getSpotJson", method = RequestMethod.GET)
    public String getSpotInJSONinDB(@RequestParam(value="id", defaultValue="0")long id){
        Spot spot = sr.getSpot(id);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String input = "error";
        try {
            input = ow.writeValueAsString(spot);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return input;
    }

    @RequestMapping(value="/spotservice/getAllSpotsJson", method = RequestMethod.GET)
    public String getSpotsInJSONinDB(){
        List<Spot> spots = sr.getAllSpots();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String input = "error";
        try {
            input = ow.writeValueAsString(spots);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return input;
    }
}
