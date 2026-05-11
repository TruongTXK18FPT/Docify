package com.docify.docify_be.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CONVERSION_EXCHANGE = "conversion-exchange";
    public static final String CONVERSION_QUEUE = "conversion-queue";
    public static final String CONVERSION_ROUTING_KEY = "conversion.task";

    public static final String DLX_EXCHANGE = "conversion-dlx";
    public static final String DLQ_QUEUE = "conversion-dlq";

    @Bean
    public DirectExchange conversionExchange() {
        return new DirectExchange(CONVERSION_EXCHANGE);
    }

    @Bean
    public Queue conversionQueue() {
        return QueueBuilder.durable(CONVERSION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", CONVERSION_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding conversionBinding(Queue conversionQueue, DirectExchange conversionExchange) {
        return BindingBuilder.bind(conversionQueue).to(conversionExchange).with(CONVERSION_ROUTING_KEY);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(CONVERSION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
