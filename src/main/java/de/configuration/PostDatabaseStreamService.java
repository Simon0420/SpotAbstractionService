package de.configuration;

import de.domainAux.Route;
import de.repositories.postDBRepositories.RouteRepository;
import de.repositories.preDBRepositories.SpotRepository;
import de.services.SpotService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PostDatabaseStreamService {

    @Autowired
    public AmqpTemplate template;

    @Autowired
    private FanoutExchange fanout;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    final SpotService spotService = new SpotService();

    public void postDatabaseStreamRoute(Long message){
        template.convertAndSend(fanout.getName(),"", message);
    }

    public void postDatabaseStreamMessage(Long message){
        System.out.println("Incoming request: route with id:"+message);
        Route route = routeRepository.getRoute(message);
        route = spotService.learningSpotStructure(route);
        System.out.println("Spot mapping for route: "+message+" done.");
    }

}
