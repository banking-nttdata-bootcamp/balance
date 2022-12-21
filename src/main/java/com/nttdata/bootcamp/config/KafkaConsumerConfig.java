package com.nttdata.bootcamp.config;

import com.nttdata.bootcamp.events.EventKafka;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
	
	private final String bootstrapAddress = "localhost:9092";

    @Bean
    public ConsumerFactory<String, EventKafka<?>> consumerFactory() {
        Map<String, String> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(JsonSerializer.TYPE_MAPPINGS,"com.nttdata.bootcamp:com.nttdata.bootcamp.events.EventKafka");

        final JsonDeserializer<EventKafka<?>> jsonDeserializer = new JsonDeserializer<>();
        return new DefaultKafkaConsumerFactory(
                props,
                new StringDeserializer(),
                jsonDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventKafka<?>>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, EventKafka<?>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
