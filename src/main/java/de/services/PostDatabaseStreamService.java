package de.services;

import de.domains.domainAux.Route;
import de.repositories.postDBRepositories.RouteRepository;
import de.services.SpotService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PostDatabaseStreamService {

    @Autowired
    RouteRepository routes;

    SpotService s = new SpotService();

    @Autowired
    public AmqpTemplate template;

    @Autowired
    private FanoutExchange fanout;

    /**
     * Method being called whenever the FanoutExchange (connected to the PostDatabaseStreamService class) is triggered.
     * @param message
     */
    public void postDatabaseStreamRoute(Long message){
        template.convertAndSend(fanout.getName(),"", message);
    }

    /**
     * Method being called whenever the FanoutExchange (connected to the PostDatabaseStreamService class) is triggered.
     * @param message
     */
    public void postDatabaseStreamMessage(Long message){

        Route r = routes.getRoute(message);
        s.learningSpotStructure(r);

    }

}
