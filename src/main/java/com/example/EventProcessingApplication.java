package com.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import com.example.domain.EventMessage;
import com.example.schema.utils.RegistrySchemaTool;
import com.example.schema.utils.SchemaUtils;import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

import com.example.avro.deserializer.KafkaSpecificRecordDeserializer;
import com.example.domain.EventMessage;
import com.example.domain.EventMessageTypeTwo;
import com.example.avro.schema.provider.RegistrySchemaProvider;
import com.example.avro.serializer.KafkaSpecificRecordSerializer;

@SpringBootApplication(exclude = {JpaRepositoriesAutoConfiguration.class, KafkaAutoConfiguration.class
		})
public class EventProcessingApplication implements CommandLineRunner{
	public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String EVENT_TOPIC = "events_topic";
    public static final String SCHEMA_REGISTRY_ENDPOINT = "http://localhost:8990";
	
	public static void main(String[] args) {
		// Launch the application
		SpringApplication.run(EventProcessingApplication.class, args);
	}
	
	@Override
    public void run(String... strings) throws Exception {
		add();
		produce();
		consume();
	}
	
	public void add() throws Exception {
        RegistrySchemaTool.main("--add", "--name", "EventMessage", "--version", "1",
              "--schema-file", "src/main/resources/avro/eventmessage_v1_1.avsc", "--schema.registry.endpoint", SCHEMA_REGISTRY_ENDPOINT);
    }
	
	public void produce() throws Exception {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.ACKS_CONFIG, "-1");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaSpecificRecordSerializer.class.getName());
        producerProps.put(KafkaSpecificRecordSerializer.VALUE_RECORD_CLASSNAME, EventMessage.class.getName());
        producerProps.put(SchemaUtils.SCHEMA_PROVIDER_FACTORY_CONFIG, RegistrySchemaProvider.RegistrySchemaProviderFactory.class.getName());
        producerProps.put(RegistrySchemaProvider.ENDPOINT_CONF, SCHEMA_REGISTRY_ENDPOINT);

        KafkaProducer<Integer, EventMessage> producer = new KafkaProducer<>(producerProps);

        for (EventMessage msg : new EventMessage[] {
                new EventMessage("EventMessage1", "EventMessage, One"),
                new EventMessage("EventMessage2", "EventMessage, Two"),
                new EventMessage("EventMessage3", "EventMessage, Three")
        })
            producer.send(new ProducerRecord<>(EVENT_TOPIC, msg.getIdentifier().hashCode(), msg)).get();
    }
	
	public void consume() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaSpecificRecordDeserializer.class.getName());
        consumerProps.put(KafkaSpecificRecordDeserializer.VALUE_RECORD_CLASSNAME, EventMessage.class.getName());
        consumerProps.put(SchemaUtils.SCHEMA_PROVIDER_FACTORY_CONFIG, RegistrySchemaProvider.RegistrySchemaProviderFactory.class.getName());
        consumerProps.put(RegistrySchemaProvider.ENDPOINT_CONF, SCHEMA_REGISTRY_ENDPOINT);

        KafkaConsumer<Integer, EventMessage> consumer = new KafkaConsumer<>(consumerProps);

        consumer.subscribe(Collections.singletonList(EVENT_TOPIC));
        while(true) {
            consumer.poll(1000).forEach(consumerRecord -> {
                EventMessage msg = consumerRecord.value();
                System.out.println(msg);
            });
        }

    }
}
