package nus.iss.revision15.config;

import static nus.iss.revision15.Constants.*;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import nus.iss.revision15.Revision15Application;

public class AppConfig {
    private static final Logger logger = Logger.getLogger(Revision15Application.class.getName());

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Optional<Integer> redisPort;

    @Value("${spring.redis.database}")
    private Integer redisDatabase;

    final private String redisPassword = System.getenv(ENV_REDIS_PW);

    @Bean
    @Scope("singleton")
    public RedisTemplate<String, String> createRedisTemplate() {
        final RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        if (redisPort.isPresent())
            config.setPort(redisPort.get());
        if (null != redisPassword) {
            config.setPassword(redisPassword);
            logger.info("Redis password set!");
        } else {
            logger.warning("Redis password not set!"); // set password in env variables
            System.exit(1);
        }
        config.setDatabase(redisDatabase);

        final JedisClientConfiguration jedisClient = JedisClientConfiguration.builder().build();
        final JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisClient);
        jedisFac.afterPropertiesSet();
        logger.log(Level.INFO, "redis host: %s, port: %s".formatted(redisHost, redisPort));

        // Use String instead of Object -> Object is broader and malicious objects can be used
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisFac);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        // from slides, different from kenneth
        // String serializer is preferred over Object serializer
        // template.setValueSerializer(new StringRedisSerializer());
        // RedisSerializer<Object> objSerializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
        // template.setValueSerializer(new JdkSerializationRedisSerializer(getClass().getClassLoader()));
        return template;
    }
}
