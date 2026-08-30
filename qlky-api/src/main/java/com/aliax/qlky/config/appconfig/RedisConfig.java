package com.aliax.qlky.config.appconfig;

import com.aliax.qlky.config.appconfig.configuration.RedisRedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 艾莉希雅
 */
public class RedisConfig {

    @Autowired
    private RedisRedissonConfiguration redisRedissonConfiguration;
//    @Bean
//    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
//        RedisTemplate<String,Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//
//        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        // key采用String的序列化方式
//        template.setKeySerializer(stringRedisSerializer);
//        // hash的key也采用String的序列化方式
//        template.setHashKeySerializer(stringRedisSerializer);
//        // value序列化方式采用jackson
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        // hash的value序列化方式采用jackson
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
//        template.afterPropertiesSet();
//
//        return template;
//    }
//    @Bean
//    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        StringRedisTemplate template = new StringRedisTemplate();
//        template.setConnectionFactory(redisConnectionFactory);
//        return template;
//    }
//
//    @Bean
//    RedissonClient singleRedisson(){
//        // 创建配置
//        Config config = new Config();
//        config.setCodec(StringCodec.INSTANCE);
//        config.setTransportMode(TransportMode.NIO);
//        SingleServerConfig singleServerConfig = config.useSingleServer()
//                .setAddress(redisRedissonConfiguration.getAddress())
//                .setPassword(redisRedissonConfiguration.getPassword())
//                .setDatabase(0);
//
//        //初始化
//        var redisson = Redisson.create(config);
//        redisson.getBloomFilter("task").tryInit(10000,0.03);
//        return redisson;
//    }
}
