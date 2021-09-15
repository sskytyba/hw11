package com.example.hw11;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnBean(RedisConfig.class)
@RequiredArgsConstructor
public class RedisController {

    private final StringRedisTemplate template;

    @PostMapping("/put")
    public void put(@RequestBody String task) {
        template.convertAndSend("tasks", task);
    }

}
