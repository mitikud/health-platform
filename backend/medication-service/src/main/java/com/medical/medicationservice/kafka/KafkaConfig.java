package com.medical.medicationservice.kafka;


import com.medical.commonsevents.events.DiagnosisSuggestedEvent;
import com.medical.commonsevents.events.MedicationPlanCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;

@Configuration
public class KafkaConfig {
    @Bean
    public ConsumerFactory<String, DiagnosisSuggestedEvent> diagnosisConsumerFactory() {
        var props = new HashMap<String,Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("KAFKA_BOOTSTRAP_SERVERS","localhost:9092"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "medication-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new org.apache.kafka.common.serialization.StringDeserializer(),
                new org.springframework.kafka.support.serializer.JsonDeserializer<>(DiagnosisSuggestedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DiagnosisSuggestedEvent> diagnosisListenerFactory() {
        var f = new ConcurrentKafkaListenerContainerFactory<String, DiagnosisSuggestedEvent>();
        f.setConsumerFactory(diagnosisConsumerFactory());
        return f;
    }

    @Bean
    public ProducerFactory<String, MedicationPlanCreatedEvent> planProducerFactory() {
        var props = new HashMap<String,Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("KAFKA_BOOTSTRAP_SERVERS","localhost:9092"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, MedicationPlanCreatedEvent> planTemplate() {
        return new KafkaTemplate<>(planProducerFactory());
    }
}
