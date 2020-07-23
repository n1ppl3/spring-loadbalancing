package ru.n1ppl3.spring.loadbalancing;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static ru.n1ppl3.spring.loadbalancing.SayHelloClient.SERVICE_ID;

@Configuration
public class SayHelloConfiguration {

    @Bean
    @Primary
    ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return ServiceInstanceListSupplier.fixed(SERVICE_ID)
                .instance(6666)
                .instance(9999)
                .build();
    }

}
