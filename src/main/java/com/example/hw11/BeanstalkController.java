package com.example.hw11;

import com.dinstone.beanstalkc.BeanstalkClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

@RestController
@ConditionalOnBean(BeanstalkdConfig.class)
@RequiredArgsConstructor
public class BeanstalkController {

    private final BeanstalkClient beanstalkClient;
    private final BeanstalkConsumer beanstalkConsumer;

    @PostConstruct
    public void runConsumer() {
        IntStream.range(0, 10).forEach(i -> beanstalkConsumer.consume());
    }

    @PostMapping("/put")
    public long put(@RequestBody PutJob job) {
        return beanstalkClient.putJob(job.getPriority(), job.getDelay(), job.getTtr(), job.getData().toString().getBytes(StandardCharsets.UTF_8));
    }
}
