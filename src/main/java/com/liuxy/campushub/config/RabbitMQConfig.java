package com.liuxy.campushub.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String HOT_POST_QUEUE = "hot.post.queue";
    public static final String HOT_POST_EXCHANGE = "hot.post.exchange";
    public static final String HOT_POST_ROUTING_KEY = "hot.post.routing.key";
    
    @Bean
    public Queue hotPostQueue() {
        return new Queue(HOT_POST_QUEUE, true);
    }
    
    @Bean
    public DirectExchange hotPostExchange() {
        return new DirectExchange(HOT_POST_EXCHANGE);
    }
    
    @Bean
    public Binding hotPostBinding() {
        return BindingBuilder.bind(hotPostQueue())
                .to(hotPostExchange())
                .with(HOT_POST_ROUTING_KEY);
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
} 