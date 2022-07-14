package kafkaconsumerconfig;

import org.apache.kafka.common.KafkaException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public final class KafkaConsumerConfigurerImpl implements KafkaConfigurer {
    private final Properties kafkaProperties = new Properties();
    private static final String TOPIC_NAME = "topic.name";
    public KafkaConsumerConfigurerImpl() {
        try {
            kafkaProperties.load(ClassLoader.getSystemResourceAsStream("kafka-consumer.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new KafkaException("CONSUMER COULD NOT BE CREATED!");
        }
    }

    public KafkaConsumerConfigurerImpl(String pathToProperty){
        try (var fileStream = new FileInputStream(pathToProperty)){
            kafkaProperties.load(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new KafkaException("CONSUMER COULD NOT BE CREATED!");
        }
    }

    @Override
    public Properties getKafkaProperties() {
        var property = new Properties();
        property.putAll(kafkaProperties);
        return property;
    }

    @Override
    public List<String> getTopicName() {
        return List.of(kafkaProperties.getProperty(TOPIC_NAME));
    }
}
