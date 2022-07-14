package redisconnection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.JedisPool;

public class RedisConnectionTest {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    @Test
    void testConnection(){
        var pool = new JedisPool(REDIS_HOST,REDIS_PORT);
        var connection = pool.getResource();
        Assertions.assertNotNull(connection);
    }

    @Test
    void testOperation(){
        var pool = new JedisPool(REDIS_HOST,REDIS_PORT);
        var connection = pool.getResource();
        connection.set("demo","demo");
        Assertions.assertNotNull(connection);
    }

}
