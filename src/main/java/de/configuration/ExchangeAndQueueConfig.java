package de.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class ExchangeAndQueueConfig {

    /**
     * Defines the Queue name of a new Queue
     * @return
     */
    @Bean
    Queue upStreamQueue(){
        return new Queue("UpStreamQueue", false);
    }

    /**
     * Defines the Queue name of a new Queue
     * @return
     */
    @Bean
    Queue postDatabaseStreamQueue(){
        return new Queue("PostDatabaseStreamQueue", false);
    }

    /**
     * Defines the exchange name of the new Topic - Exchange. Topic Exchange will continously send messages to all registered services in a Round Robin order.
     * @return
     */
    @Bean
    TopicExchange exchange(){
        return new TopicExchange("SmartCity-Exchange");
    }

    /**
     * Defines the exchange name of the new FanoutExchange. FanoutExchanges will send messages to all services registered to it.
     * @return
     */
    @Bean
    FanoutExchange exchange1(){
        return new FanoutExchange("SmartCity-Exchange1");
    }

    /**
     * Defines bindings between defined queues and exchanges. Core function as the defined configurations only work as expected if the bindings are correct.
     * The current configuration uses the UpStreamQueue to accept every message send to the server, and send it to the GpsPointService to create database entries.
     * The GpsPointService then fills the PostDatabaseStreamQueue which is bound to the FanoutExchange. Every process being placed on the server will be able
     * to connect to the PostDatabaseStreamQueue via its FanoutExchange and receive all messages needed.
     * @return
     */
    @Bean
    List<Binding> bindings(){
        return Arrays.asList(BindingBuilder.bind(upStreamQueue()).to(exchange()).with("UpStreamQueue"),
                //BindingBuilder.bind(postDatabaseStreamQueue()).to(exchange1()).with("PostDatabaseStreamQueue"));
                BindingBuilder.bind(postDatabaseStreamQueue()).to(exchange1()));
    }
}
