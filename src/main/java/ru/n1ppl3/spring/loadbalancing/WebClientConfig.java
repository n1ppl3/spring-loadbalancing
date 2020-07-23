package ru.n1ppl3.spring.loadbalancing;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static ru.n1ppl3.spring.loadbalancing.SayHelloClient.SERVICE_ID;

@Configuration
@LoadBalancerClient(name = SERVICE_ID, configuration = SayHelloConfiguration.class)
public class WebClientConfig {

    @Bean
    @LoadBalanced
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}
