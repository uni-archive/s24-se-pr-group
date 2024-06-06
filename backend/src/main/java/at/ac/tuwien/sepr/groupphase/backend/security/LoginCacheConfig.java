package at.ac.tuwien.sepr.groupphase.backend.security;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class LoginCacheConfig {

    @Bean
    public Cache<String, Integer> loginAttemptCache(){
        return CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }
}
