package de.configuration;

import de.services.SpotService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class RabbitUpStreamConfig {

    /**
     * Bean to create a ConnectionFactory that handles the "Upstream - Queue". Credentials and connection - name
     * are set here.
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    /**
     * Bean to create a RabbitTemplate. Credentials and connection - name of the corresponding ConnectionFactory Bean are used.
     * The name of the RoutingKey (UpStreamQueue in this case) is set, as well as the MessageConverter (Jackson2Json to allow
     * the use of Json - Strings as messages.
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        //The routing key is set to the name of the queue by the broker for the default exchange.
        template.setRoutingKey("UpStreamQueue");
        //Where we will synchronously receive messages from
        template.setQueue("UpStreamQueue");
        return template;
    }


    /**
     * A Container that listens on the defined Queue (UpStreamQueue in this case) and call a specific function defined in the
     * MessageListener.
     * @param spotService The Service which's listener Method is called.
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer container(SpotService spotService) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames("UpStreamQueue");
        MessageListenerAdapter messageListenerAdapter = upStreamListenerAdapter(spotService);
        messageListenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
        container.setMessageListener(messageListenerAdapter);
        return container;
    }

    /**
     * Bean to create a MessageListenerAdapter that is later used in the SimpleMessageListenerContainer Bean. It simply defines
     * the service and a method in the service that will be called if a rabbit queue sends a "listening" signal to the SimpleMessageListenerContainer
     * @param receiver The Service which's listener Method is called.
     * @return
     */
    @Bean
    MessageListenerAdapter upStreamListenerAdapter(SpotService receiver){
        return new MessageListenerAdapter(receiver, "upStreamMessage");
    }


}
