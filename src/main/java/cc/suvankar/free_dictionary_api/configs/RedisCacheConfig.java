package cc.suvankar.free_dictionary_api.configs;

import org.springframework.boot.autoconfigure.cache.*;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.*;

import java.time.*;


@Configuration(proxyBeanMethods = false)
public class RedisCacheConfig {

  @Bean
  @ConditionalOnProperty(
      name = "app.redis.cache-serializer",
      havingValue = "json"
  )
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {

    return builder -> {
      RedisCacheConfiguration config =
          RedisCacheConfiguration.defaultCacheConfig()
              .entryTtl(Duration.ofDays(7))
              .disableCachingNullValues()
              .serializeKeysWith(
                  RedisSerializationContext.SerializationPair
                      .fromSerializer(new StringRedisSerializer())
              )
              .serializeValuesWith(
                  RedisSerializationContext.SerializationPair
                      .fromSerializer(new GenericJackson2JsonRedisSerializer())
              );

      builder.cacheDefaults(config);
    };
  }
}
