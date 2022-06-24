package kafkaconsumerconfig;

import java.util.List;
import java.util.Properties;

public interface KafkaConfigurer {
    Properties getKafkaProperties();
    List<String> getTopicName();
}
