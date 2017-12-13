package de.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class ExchangeAndQueueConfig {

    @Bean
    Queue postDatabaseStreamQueue(){
        return new Queue("PostDatabaseStreamQueue", false);
    }

    @Bean
    FanoutExchange exchange1(){
        return new FanoutExchange("SmartCity-Exchange1");
    }

    @Bean
    List<Binding> bindings(){
        return Arrays.asList(//BindingBuilder.bind(postDatabaseStreamQueue()).to(exchange1()).with("PostDatabaseStreamQueue"));
                BindingBuilder.bind(postDatabaseStreamQueue()).to(exchange1()));
    }
}
