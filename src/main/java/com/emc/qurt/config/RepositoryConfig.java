package com.emc.qurt.config;

import com.emc.qurt.domain.ClusterSettings;
import com.emc.qurt.domain.SystemSettings;
import com.emc.qurt.domain.VirtualMachineData;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

/**
 * Created by morand3 on 1/15/2015.
 */
@Configuration
public class RepositoryConfig extends RepositoryRestMvcConfiguration {
    @Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(SystemSettings.class);
        config.exposeIdsFor(ClusterSettings.class);
        config.exposeIdsFor(VirtualMachineData.class);
    }
}
