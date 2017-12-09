package de.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.domain.Route;
import de.services.SpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

public class SpotController {

    final SpotService spotService;

    @Autowired
    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @RequestMapping(value="/processRoute", method = RequestMethod.POST)
    public String getRouteInJSON(@RequestBody String jsonRoute) {

        ObjectMapper mapper = new ObjectMapper();
        Route route = null;
        try {
            route = mapper.readValue(jsonRoute, Route.class);
        } catch (Exception e) {
            CharArrayWriter cw = new CharArrayWriter();
            PrintWriter w = new PrintWriter(cw);
            e.printStackTrace(w);
            w.close();
            System.out.println(cw.toString());
            return cw.toString();
        }

        // map into spots
        spotService.learningSpotStructure(route);
        return "Route processed";
    }

}
