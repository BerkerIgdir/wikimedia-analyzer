package repo.cache;

import dto.WikiMediaRecentChangesDTO;
import org.apache.kafka.common.KafkaException;
import redis.clients.jedis.JedisPool;
import repo.cache.interfaces.CacheDAO;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class CacheDAOImpl implements CacheDAO<WikiMediaRecentChangesDTO> {
    private static final String KEY = "PROCESSED_IDS";
    private final JedisPool pool;

    public CacheDAOImpl() {
        try {
            this.pool = initPool();
        } catch (IOException e) {
            e.printStackTrace();
            throw new KafkaException("CONSUMER COULD NOT BE CREATED!");
        }

    }

    private JedisPool initPool() throws IOException {
        var properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("redis-cache.properties"));
        int port = Integer.parseInt(properties.getProperty("redis.port"));
        String host = properties.getProperty("redis.host");
        return new JedisPool(host, port);
    }

    @Override
    public void put(WikiMediaRecentChangesDTO messageId) {
        var connection = pool.getResource();
        connection.sadd(KEY,messageId.getUser())
    }

    @Override
    public void put(List<WikiMediaRecentChangesDTO> messageIds) {

    }

    @Override
    public void remove(WikiMediaRecentChangesDTO messageId) {

    }
}
