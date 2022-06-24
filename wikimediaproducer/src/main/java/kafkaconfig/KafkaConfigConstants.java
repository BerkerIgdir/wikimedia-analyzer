package kafkaconfig;

public class KafkaConfigConstants {
    public static String COMPRESSION_TYPE = "snappy";
    public static String STREAM_SOURCE_URL = "https://stream.wikimedia.org/v2/stream/recentchange";
    public static String BOOTSTRAP_SERVER_URL = "127.0.0.1:9092";
    public static String WIKIMEDIA_RECENT_CHANGES_TOPIC = "wikimedia.recent.changes";
    public static int BATCH_SIZE = 32 * 1024;
    public static int LINGER_MS = 20;
}
