package kafkaconfig;

public final class KafkaConfigConstants {
    private KafkaConfigConstants(){}
    public static final String COMPRESSION_TYPE = "snappy";
    public static final String STREAM_SOURCE_URL = "https://stream.wikimedia.org/v2/stream/recentchange";
    public static final String BOOTSTRAP_SERVER_URL = "127.0.0.1:9092";
    public static final String WIKIMEDIA_RECENT_CHANGES_TOPIC = "wikimedia.recent.changes";
    public static final int BATCH_SIZE = 32 * 1024;
    public static final int LINGER_MS = 20;
}
