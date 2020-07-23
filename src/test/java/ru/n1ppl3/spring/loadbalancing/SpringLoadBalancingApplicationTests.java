package ru.n1ppl3.spring.loadbalancing;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.function.Consumer;

import static ru.n1ppl3.spring.loadbalancing.utils.SunHttpServer.startTestServer;

@SpringBootTest
@Import({SpringLoadBalancingApplicationTests.MyTestConfiguration.class})
class SpringLoadBalancingApplicationTests {

	private static final int port1 = 6666;
	private static final int port2 = 9999;

	static class MyTestConfiguration {
		@Bean
		HttpServer httpServer1() throws IOException {
			return startTestServer(port1);
		}
		@Bean
		HttpServer httpServer2() throws IOException {
			return startTestServer(port2);
		}
	}

	@Autowired
	private SayHelloClient sayHelloClient;

	@Test
	void roundRobinBalancingTest() {
		Assertions.assertTrue(sayHelloClient.hi("").doOnNext(System.err::println).block().contains("" + port1));
		Assertions.assertTrue(sayHelloClient.hi("").doOnNext(System.err::println).block().contains("" + port2));
		Assertions.assertTrue(sayHelloClient.hello("").doOnNext(System.err::println).block().contains("" + port1));
		Assertions.assertTrue(sayHelloClient.hello("").doOnNext(System.err::println).block().contains("" + port2));
	}

}
