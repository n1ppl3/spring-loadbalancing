package ru.n1ppl3.spring.loadbalancing;

import lombok.AllArgsConstructor;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class SayHelloClient {

    public static final String SERVICE_ID = "say-hello";

    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    public Mono<String> hi(String name) {
        return loadBalancedWebClientBuilder.build().get().uri("http://say-hello/greeting")
                .retrieve().bodyToMono(String.class)
                .map(greeting -> String.format("%s, %s!", greeting, name));
    }

    public Mono<String> hello(String name) {
        return WebClient.builder().filter(lbFunction)
                .build().get().uri("http://say-hello/greeting")
                .retrieve().bodyToMono(String.class)
                .map(greeting -> String.format("%s, %s!", greeting, name));
    }
}
