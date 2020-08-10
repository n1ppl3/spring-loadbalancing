package ru.n1ppl3.spring.loadbalancing;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
class CircuitBreakerTest {

    private static final int SECONDS_TO_WAIT = 5;
    private static final int STAT_CALLS = 10;

    @FunctionalInterface
    interface MathService {
        int transform(int x);
    }

    static class Doubler implements MathService {
        @Override
        public int transform(int x) {
            if (x < 0) throw new RuntimeException("x=" + x);
            if (x % 3 ==0) throw new RuntimeException("x=" + x);
            return 2 * x;
        }
    }

    static class ResilientDoubler implements MathService {

        private final CircuitBreaker circuitBreaker;
        private final MathService mathService;

        ResilientDoubler(MathService mathService) {
            CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .waitDurationInOpenState(Duration.ofSeconds(SECONDS_TO_WAIT))
                .writableStackTraceEnabled(false) // не печатать stacktrace у CallNotPermittedException
                .failureRateThreshold(30) // 30%
                .minimumNumberOfCalls(STAT_CALLS) // сколько вызовов нужно набрать для статистики
                .build();
            this.circuitBreaker = CircuitBreaker.of("myCircuitBreaker", circuitBreakerConfig);
            this.mathService = mathService;
        }

        @Override
        public int transform(int x) {
            return circuitBreaker.executeSupplier(() -> mathService.transform(x));
        }
    }

    @Test
    void test() throws InterruptedException {
        ResilientDoubler resilientDoubler = new ResilientDoubler(new Doubler());

        // набираем статистику
        for (int i=1; i <= STAT_CALLS; i++) {
            try {
                Assertions.assertEquals(CircuitBreaker.State.CLOSED, resilientDoubler.circuitBreaker.getState());
                resilientDoubler.transform(i);
            } catch (Exception ignored) {
                // брейкер всё равно печатает
            }
        }
        // после 10 переходит в OPEN
        Assertions.assertEquals(CircuitBreaker.State.OPEN, resilientDoubler.circuitBreaker.getState());

        // вызовы запрещены
        Assertions.assertThrows(CallNotPermittedException.class, () -> resilientDoubler.transform(5));
        Assertions.assertEquals(CircuitBreaker.State.OPEN, resilientDoubler.circuitBreaker.getState());

        // подождём, чтобы можно было опять делать вызовы
        TimeUnit.SECONDS.sleep(SECONDS_TO_WAIT);

        Assertions.assertEquals(CircuitBreaker.State.OPEN, resilientDoubler.circuitBreaker.getState());
        Assertions.assertEquals(2, resilientDoubler.transform(1)); // приоткрылся и перешёл в HALF_OPEN
        Assertions.assertEquals(CircuitBreaker.State.HALF_OPEN, resilientDoubler.circuitBreaker.getState());
    }
}
