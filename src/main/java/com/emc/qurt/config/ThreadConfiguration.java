package com.emc.qurt.config;

import com.emc.qurt.sampling.SamplingTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by morand3 on 12/24/2014.
 */
@Configuration
public class ThreadConfiguration {

    @Bean
    public SamplingTask getSamplingTask() {
        return new SamplingTask();
    }
}
