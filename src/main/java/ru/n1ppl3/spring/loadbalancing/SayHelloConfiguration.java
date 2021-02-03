package ru.n1ppl3.spring.loadbalancing;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static ru.n1ppl3.spring.loadbalancing.SayHelloClient.SERVICE_ID;

@Configuration
public class SayHelloConfiguration {

    public static final int PORT1 = 6666;
    public static final int PORT2 = 9999;

    @Bean
    @Primary
    ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return ServiceInstanceListSupplier.fixed(SERVICE_ID)
                .instance(PORT1)
                .instance(PORT2)
                .build();
    }

}
