package ru.n1ppl3.spring.loadbalancing;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import ru.n1ppl3.spring.loadbalancing.dto.MyKotlinDataClass;

import java.io.IOException;
import java.util.UUID;

import static ru.n1ppl3.spring.loadbalancing.SayHelloConfiguration.PORT1;
import static ru.n1ppl3.spring.loadbalancing.SayHelloConfiguration.PORT2;
import static ru.n1ppl3.spring.loadbalancing.utils.SunHttpServer.startTestServer;

@SpringBootTest
@Import({SpringLoadBalancingApplicationTests.MyTestConfiguration.class})
class SpringLoadBalancingApplicationTests {

	private static final int port1 = PORT1;
	private static final int port2 = PORT2;

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
		MyKotlinDataClass myKotlinDataClass = new MyKotlinDataClass(UUID.randomUUID());
		System.err.println(myKotlinDataClass);
		Assertions.assertTrue(sayHelloClient.hi("").doOnNext(System.err::println).block().contains("" + port1));
		Assertions.assertTrue(sayHelloClient.hi("").doOnNext(System.err::println).block().contains("" + port2));
		Assertions.assertTrue(sayHelloClient.hello("").doOnNext(System.err::println).block().contains("" + port1));
		Assertions.assertTrue(sayHelloClient.hello("").doOnNext(System.err::println).block().contains("" + port2));
	}

}
