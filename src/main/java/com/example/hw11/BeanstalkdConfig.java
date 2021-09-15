package com.example.hw11;

import com.dinstone.beanstalkc.BeanstalkClient;
import com.dinstone.beanstalkc.BeanstalkClientFactory;
import com.dinstone.beanstalkc.Configuration;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "beanstalk")
@ConditionalOnProperty("beanstalk.host")
@Data
public class BeanstalkdConfig {

    private String host;
    private Integer port;

    @Bean
    public BeanstalkClientFactory beanstalkClientFactory() {
        // create beanstalkc config,default loading properties from file beanstalkc.properties in classpath
        Configuration config = new Configuration();
        config.setServiceHost(host);
        config.setServicePort(port);
        config.setConnectTimeout(2000);
        config.setReadTimeout(3000);
        // create job producer and consumer
        return new BeanstalkClientFactory(config);
    }

    @Bean(destroyMethod = "close")
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public BeanstalkClient beanstalkClient(BeanstalkClientFactory beanstalkClientFactory) {
        return beanstalkClientFactory.createBeanstalkClient();
    }

}
