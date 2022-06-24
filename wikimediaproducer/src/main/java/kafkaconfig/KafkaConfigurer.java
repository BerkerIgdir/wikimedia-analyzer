package kafkaconfig;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

//Will be replaced with spring cloud config in the future
public class KafkaConfigurer {
    private KafkaConfigurer() {
    }

    private final Properties properties = new Properties();

    public KafkaConfigurer bootstrapServer(String serverAddress) {
        Objects.requireNonNull(serverAddress);
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddress);
        return this;
    }

    public KafkaConfigurer keySerializer(Class<?> keySerializer) {
        if (Arrays.stream(keySerializer.getInterfaces()).noneMatch(aClass -> aClass.equals(Serializer.class))) {
            throw new RuntimeException("This method only accepts serializer interface");
        }
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.getName());
        return this;
    }

    public KafkaConfigurer valueSerializer(Class<?> valueSerializer) {
        if (Arrays.stream(valueSerializer.getInterfaces()).noneMatch(aClass -> aClass.equals(Serializer.class))) {
            throw new RuntimeException("This method only accepts serializer interface");
        }
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.getName());
        return this;
    }

    public KafkaConfigurer batchSize(int size) {
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(size));
        return this;
    }

    public KafkaConfigurer lingerMs(int ms) {
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, String.valueOf(ms));
        return this;
    }

    public KafkaConfigurer compressionType(String compressionType) {
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
        return this;
    }

    public static <T, K> KafkaProducer<T, K> createProducer(Consumer<KafkaConfigurer> configurerConsumer) {
        var configurer = new KafkaConfigurer();
        configurerConsumer.accept(configurer);
        return new KafkaProducer<>(configurer.properties);
    }

}
