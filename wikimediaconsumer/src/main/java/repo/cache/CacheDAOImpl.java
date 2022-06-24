package repo.cache;

import dto.WikiMediaRecentChangesDTO;
import org.apache.kafka.common.KafkaException;
import redis.clients.jedis.JedisPool;
import repo.cache.interfaces.CacheDAO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class CacheDAOImpl implements CacheDAO<WikiMediaRecentChangesDTO> {
    private static final String KEY = "PROCESSED_IDS";
    private static final String ID_KEY = "id";
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
    public void put(List<WikiMediaRecentChangesDTO> dtos) {
        var connection = pool.getResource();
        dtos.forEach(id -> connection.sadd(KEY,id.getMeta().get(ID_KEY)));
    }

    @Override
    public void remove(WikiMediaRecentChangesDTO dto) {
        var connection = pool.getResource();
        connection.spop(dto.getMeta().get(ID_KEY));
    }

    @Override
    public Optional<WikiMediaRecentChangesDTO> get(WikiMediaRecentChangesDTO dto) {
        var connection = pool.getResource();
        //if exists return the dto itself. Bad solution!
        return Optional.ofNullable(connection.get(dto.getMeta().get(ID_KEY))).map(s -> dto);
    }
}
