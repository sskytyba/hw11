package com.example.hw11;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@ConditionalOnBean(RedisConfig.class)
@Component
public class RedisConsumer {

    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicLong lastLogTime = new AtomicLong(0);
    private final AtomicLong lastMaxExecTime = new AtomicLong(0);
    private final AtomicLong sumLastMaxExecTime = new AtomicLong(0);

    public void receiveMessage(String message) {
        counter.incrementAndGet();
        long currentTime = System.currentTimeMillis();
        long lastTime = lastLogTime.getAndSet(currentTime);
        lastMaxExecTime.getAndAccumulate(lastTime == 0 ? 0 : currentTime - lastTime, Math::max);
    }

    int count = 0;
    @Scheduled(fixedDelay = 2000)
    public void scheduled() {
        long v = lastMaxExecTime.getAndSet(0);
        count++;
        log.info("Avg max delay: {} s, total consumed: {}", sumLastMaxExecTime
                .getAndAccumulate(v, Long::sum) / 1000.0 / count, counter.get());
    }

}
