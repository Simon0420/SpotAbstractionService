package de;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.domains.domain.Spot;
import de.repositories.preDBRepositories.SpotRepository;
import de.services.CustomSpotQueries;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
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

    Session session;
    SessionFactory sessionFactory;

    @RequestMapping(value="/spotservice/getAllSpotsJsonAlternative", method = RequestMethod.GET)
    public String getSpotsAlternative(){
        if(this.session == null || !this.session.isOpen()) {
            if(session == null){
                Configuration config = new Configuration();
                config.configure();
                this.sessionFactory = config.buildSessionFactory();
                this.session = sessionFactory.openSession();
            }
            if(!session.isOpen()){
                this.session = sessionFactory.openSession();
            }
        }
        CustomSpotQueries cq = new CustomSpotQueries();
        String s = cq.getSpots(session);
        session.close();
        return s;
    }

    @RequestMapping(value="/spotservice/getAllSpotsRelationsJsonAlternative", method = RequestMethod.GET)
    public String getSpotsRelationsAlternative(){
        if(this.session == null || !this.session.isOpen()) {
            if(session == null){
                Configuration config = new Configuration();
                config.configure();
                this.sessionFactory = config.buildSessionFactory();
                this.session = sessionFactory.openSession();
            }
            if(!session.isOpen()){
                this.session = sessionFactory.openSession();
            }
        }
        CustomSpotQueries cq = new CustomSpotQueries();
        String s = cq.getSpotsRelations(session);
        session.close();
        return s;
    }

    @Deprecated
    @RequestMapping(value="/spotservice/getSpot", method = RequestMethod.GET)
    public Spot getSpotInDB(@RequestParam(value="id", defaultValue="0")long id){
        return sr.getSpot(id);
    }

    @Deprecated
    @RequestMapping(value="/spotservice/getAllSpots", method = RequestMethod.GET)
    public List<Spot> getSpotsInDB(){
        return sr.getAllSpots();
    }

    @Deprecated
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

    @Deprecated
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
