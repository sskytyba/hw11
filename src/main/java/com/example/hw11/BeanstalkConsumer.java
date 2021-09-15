package com.example.hw11;

import com.dinstone.beanstalkc.BeanstalkClient;
import com.dinstone.beanstalkc.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
@ConditionalOnBean(BeanstalkdConfig.class)
@RequiredArgsConstructor
@Slf4j
public class BeanstalkConsumer {

    private final BeanstalkClient beanstalkClient;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicLong lastMaxExecTime = new AtomicLong(0);
    private final AtomicLong sumLastMaxExecTime = new AtomicLong(0);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    @PreDestroy
    public void onDestroy() throws Exception {
        isRunning.set(false);
        Thread.sleep(2000);
    }

    @Async
    public void consume() {
        while(isRunning.get() || !Thread.interrupted()) {
            long timestamp = System.currentTimeMillis();
            Job job = beanstalkClient.reserveJob(2000);
            if (job != null) {
                beanstalkClient.deleteJob(job.getId());
                Integer count = counter.incrementAndGet();
                long delay = System.currentTimeMillis() - timestamp;
                long lastDelay = lastMaxExecTime.getAndAccumulate(delay, Math::max);
            }
        }
    }

    int count = 0;
    @Scheduled(fixedDelay = 2000)
    public void scheduled() {
        long v = lastMaxExecTime.getAndSet(0);
        if (v > 1000L) {
            log.info("skip initial max value: " + v);
            return;
        }
        count++;
        log.info("Avg max delay: {} s, total consumed: {}", sumLastMaxExecTime
                .getAndAccumulate(v, Long::sum) / 1000.0 / count, counter.get());
    }

}
