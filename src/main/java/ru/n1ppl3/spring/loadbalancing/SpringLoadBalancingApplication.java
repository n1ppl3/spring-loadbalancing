package ru.n1ppl3.spring.loadbalancing;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@SpringBootApplication
public class SpringLoadBalancingApplication {

	private final SayHelloClient sayHelloClient;

	@RequestMapping("/hi")
	public Mono<String> hi(@RequestParam(value = "name", defaultValue = "Mary") String name) {
		return sayHelloClient.hi(name);
	}

	@RequestMapping("/hello")
	public Mono<String> hello(@RequestParam(value = "name", defaultValue = "John") String name) {
		return sayHelloClient.hello(name);
	}

	/**
	 *
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpringLoadBalancingApplication.class, args);
	}
}
