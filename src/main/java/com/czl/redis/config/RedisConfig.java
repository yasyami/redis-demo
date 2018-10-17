package com.czl.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport{

    @Autowired
    LettuceConnectionFactory factory;


    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        //设置序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer
                = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer); // key序列化
        template.setValueSerializer(jackson2JsonRedisSerializer); // value序列化
        template.setHashKeySerializer(stringSerializer); // Hash key序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer); // Hash value序列化
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        RedisCacheManager.RedisCacheManagerBuilder builder =
                RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(factory);
        Set<String> cacheNames = new HashSet<String>(){
            {
                add("redis-demo");
            }
        };
        builder.initialCacheNames(cacheNames);
        return builder.build();
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return  (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(":" + String.valueOf(obj));
            }
            String rsToUse = String.valueOf(sb);
            return rsToUse;
        };
    }



//    @Override
//    @Bean
//    public CacheErrorHandler errorHandler() {
//        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
//        lg.info("初始化 -> [{}]", "Redis CacheErrorHandler");
//        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
//            @Override
//            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
//                lg.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
//            }
//
//            @Override
//            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
//                lg.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
//            }
//
//            @Override
//            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key)    {
//                lg.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
//            }
//
//            @Override
//            public void handleCacheClearError(RuntimeException e, Cache cache) {
//                lg.error("Redis occur handleCacheClearError：", e);
//            }
//        };
//        return cacheErrorHandler;
//    }
//
//    /**
//     * 此内部类就是把yml的配置数据，进行读取，创建JedisConnectionFactory和JedisPool，以供外部类初始化缓存管理器使用
//     * 不了解的同学可以去看@ConfigurationProperties和@Value的作用
//     *
//     */
//    @ConfigurationProperties
//    class DataJedisProperties{
//        @Value("${spring.redis.host}")
//        private  String host;
//        @Value("${spring.redis.password}")
//        private  String password;
//        @Value("${spring.redis.port}")
//        private  int port;
//        @Value("${spring.redis.timeout}")
//        private  int timeout;
//        @Value("${spring.redis.jedis.pool.max-idle}")
//        private int maxIdle;
//        @Value("${spring.redis.jedis.pool.max-wait}")
//        private long maxWaitMillis;
//
//        @Bean
//        JedisConnectionFactory jedisConnectionFactory() {
//            lg.info("Create JedisConnectionFactory successful");
//            JedisConnectionFactory factory = new JedisConnectionFactory();
//            factory.setHostName(host);
//            factory.setPort(port);
//            factory.setTimeout(timeout);
//            factory.setPassword(password);
//            return factory;
//        }
//        @Bean
//        public JedisPool redisPoolFactory() {
//            lg.info("JedisPool init successful，host -> [{}]；port -> [{}]", host, port);
//            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//            jedisPoolConfig.setMaxIdle(maxIdle);
//            jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
//
//            JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
//            return jedisPool;
//        }
//    }

}
