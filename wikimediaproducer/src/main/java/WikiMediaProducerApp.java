import com.launchdarkly.eventsource.EventSource;
import eventhandler.WikimediaEventHandler;
import kafkaconfig.KafkaConfigConstants;
import kafkaconfig.KafkaConfigurer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.function.Consumer;

public class WikiMediaProducerApp {

    public static void main(String[] args) {
        KafkaProducer<String, String> producer = KafkaConfigurer.createProducer(getKafkaConfigurerConsumer());
        var handler = new WikimediaEventHandler(producer, KafkaConfigConstants.WIKIMEDIA_RECENT_CHANGES_TOPIC);
        var source = new EventSource.Builder(handler, URI.create(KafkaConfigConstants.STREAM_SOURCE_URL)).build();
        source.start();
        handler.block();
    }

    @NotNull
    private static Consumer<KafkaConfigurer> getKafkaConfigurerConsumer() {
        return kafkaConfigurer ->
                kafkaConfigurer.bootstrapServer(KafkaConfigConstants.BOOTSTRAP_SERVER_URL)
                        .keySerializer(StringSerializer.class)
                        .valueSerializer(StringSerializer.class)
                        .batchSize(KafkaConfigConstants.BATCH_SIZE)
                        .lingerMs(KafkaConfigConstants.LINGER_MS)
                        .compressionType(KafkaConfigConstants.COMPRESSION_TYPE);
    }
}
