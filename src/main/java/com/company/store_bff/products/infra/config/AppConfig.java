package com.company.store_bff.products.infra.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import({ WebClientCustomConfig.class
        , WebClientConfig.class
})
public class AppConfig {

}
