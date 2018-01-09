package de.configuration;

import de.services.PostDatabaseStreamService;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class RabbitPostDatabaseStreamConfig {


    /**
     * Bean to create a ConnectionFactory that handles the "Upstream - Queue". Credentials and connection - name
     * are set here.
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory1() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("134.155.49.88:15672");
        connectionFactory.setUsername("ines");
        connectionFactory.setPassword("ines");
        return connectionFactory;
    }

    /**
     * A Container that listens on the defined Queue (PostDatabaseStreamQueue in this case) and call a specific function defined in the
     * MessageListener.
     * @param postDatabaseStreamService  The Service which's listener Method is called.
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer container1(PostDatabaseStreamService postDatabaseStreamService) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory1());
        container.setQueueNames("PostDatabaseStreamQueue");
        MessageListenerAdapter messageListenerAdapter = postDatabaseStreamMessageAdapter(postDatabaseStreamService);
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
    MessageListenerAdapter postDatabaseStreamMessageAdapter(PostDatabaseStreamService receiver){
        return new MessageListenerAdapter(receiver, "postDatabaseStreamMessage");
    }



}
