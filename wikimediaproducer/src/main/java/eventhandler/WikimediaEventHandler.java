package eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import kafkaconfig.dto.WikiMediaMessageDTO;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class WikimediaEventHandler implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WikimediaEventHandler.class);

    private final String topic;
    private final KafkaProducer<String, String> kafkaProducer;
    private final AtomicInteger numberOfProducedMessages = new AtomicInteger(0);
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean isHandlerClosed;

    public WikimediaEventHandler(KafkaProducer<String, String> kafkaProducer, String topic) {
        this.kafkaProducer = kafkaProducer;
        this.topic = topic;
    }

    public synchronized void block() {
        try {
            while (!isHandlerClosed)
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onOpen() {
        //Not necessary for now
    }

    @Override
    public void onClosed() {
        synchronized (this) {
            kafkaProducer.close();
            isHandlerClosed = true;
            notifyAll();
        }
    }

    @Override
    public void onMessage(String s, MessageEvent messageEvent) {
        var partitionKey = getKey(messageEvent.getData());
        var recordToSend = new ProducerRecord<String, String>(topic, partitionKey, messageEvent.getData());
        kafkaProducer.send(recordToSend);
        numberOfProducedMessages.incrementAndGet();
    }

    @Override
    public void onComment(String s) {
        //Not necessary for now

    }

    @Override
    public void onError(Throwable throwable) {
        onClosed();
        throw new RuntimeException(throwable);
    }

    private String getKey(String wikiMedJson) {
        try {
            var wikiMediaDto = mapper.readValue(wikiMedJson, WikiMediaMessageDTO.class);
            return Optional.ofNullable(wikiMediaDto.getWiki()).orElse("Key can not be resolved");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
