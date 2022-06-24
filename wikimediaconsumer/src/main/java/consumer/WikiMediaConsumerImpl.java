package consumer;

import business.WikiMediaRecentChangesAnalyzerService;
import business.enums.WikiType;
import business.interfaces.WikiMediaAnalyzer;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.WikiMediaRecentChangesDTO;
import kafkaconsumerconfig.KafkaConfigurer;
import kafkaconsumerconfig.KafkaConsumerConfigurerImpl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.jetbrains.annotations.NotNull;
import repo.cache.CacheDAOImpl;
import repo.cache.interfaces.CacheDAO;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WikiMediaConsumerImpl implements WikiMediaConsumer<WikiMediaRecentChangesDTO> {
    private final KafkaConfigurer kafkaConsumerConfigurer = new KafkaConsumerConfigurerImpl();
    private final String topicName = kafkaConsumerConfigurer.getTopicName().get(0);
    private final ObjectMapper mapper = new ObjectMapper();
    private final CacheDAO<WikiMediaRecentChangesDTO> cache = new CacheDAOImpl();

    private final List<WikiMediaAnalyzer> services = Arrays.stream(WikiType.values())
            .map(WikiMediaRecentChangesAnalyzerService::new)
            .collect(Collectors.toList());

    public void startConsuming() {
        var consumer = createAndPrepareConsumer();
        consumeLoop(consumer);
    }

    private void consumeLoop(KafkaConsumer<String, String> consumer) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                consume(consumer);
            } catch (Exception e) {
                e.printStackTrace();
                recover(consumer);
            }
            consumer.commitSync();
        }
    }

    private void recover(KafkaConsumer<String, String> consumer) {
        try {
            // Wait for some time to let the things settle
            Thread.sleep(Duration.ofSeconds(5).toMillis());
            seekAllPartitions(consumer);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void seekAllPartitions(KafkaConsumer<String, String> consumer) {
        var partitions = consumer.partitionsFor(topicName)
                .stream()
                .map(partitionInfo -> new TopicPartition(topicName, partitionInfo.partition()))
                .collect(Collectors.toList());
        consumer.seekToEnd(partitions);
    }

    private void consume(KafkaConsumer<String, String> consumer) {
        List<WikiMediaRecentChangesDTO> dtoList = getDtoList(consumer).stream()
                .filter(this::isProcessedBefore)
                .collect(Collectors.toList());

        if (!dtoList.isEmpty()) {
            //Is this method idempotent?
            services.parallelStream()
                    .forEach(service -> service.analyze(dtoList));
        }
    }

    private boolean isProcessedBefore(WikiMediaRecentChangesDTO dto) {
        return cache.get(dto).isPresent();
    }

    @NotNull
    private List<WikiMediaRecentChangesDTO> getDtoList(KafkaConsumer<String, String> consumer) {
        var consumerRecords = consumer.poll(Duration.of(3000, ChronoUnit.MILLIS));

        return StreamSupport.stream(consumerRecords.records(topicName).spliterator(), false)
                .map(ConsumerRecord::value)
                .map(this::getKey)
                .collect(Collectors.toList());
    }

    public List<WikiMediaAnalyzer> getCurrentServices() {
        return Collections.unmodifiableList(services);
    }

    private KafkaConsumer<String, String> createAndPrepareConsumer() {
        var consumer = new KafkaConsumer<String, String>(kafkaConsumerConfigurer.getKafkaProperties());
        consumer.subscribe(kafkaConsumerConfigurer.getTopicName());
        return consumer;
    }

    private WikiMediaRecentChangesDTO getKey(String wikiMedJson) {
        try {
            return mapper.readValue(wikiMedJson, WikiMediaRecentChangesDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
