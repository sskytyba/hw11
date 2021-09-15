package com.example.hw11;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "redis")
@Data
@ConditionalOnProperty("redis.master")
public class RedisConfig {

    private String master;
    private List<String> slaves = Collections.emptyList();

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("tasks"));

        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisConsumer receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration staticMasterReplicaConfiguration = new RedisStandaloneConfiguration(
                this.getMaster().split(":")[0],
                Integer.parseInt(this.getMaster().split(":")[1]));
        return new LettuceConnectionFactory(staticMasterReplicaConfiguration);
    }

}