package ru.n1ppl3.spring.loadbalancing.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SunHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(SunHttpServer.class);

    private static final AtomicInteger instanceCounter = new AtomicInteger(0);


    /**
     *
     */
    public static void main(String[] args) throws Exception {
        startTestServer(6666);
        startTestServer(9999);
    }


    public static HttpServer startTestServer(int port) throws IOException {
        return SunHttpServer.startHttpServer(port, Map.of(
            "/greeting", exchange -> stringResponseWithOk(exchange, "response from localhost:" + port +  "/greeting")
        ));
    }

    public static HttpServer startHttpServer(int port, Map<String, HttpHandler> pathHandlers) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        pathHandlers.forEach((path, httpHandler) -> server.createContext(path, httpHandler));
        server.start();
        logger.info("Successfully started HttpServer#{} on port {}", instanceCounter.incrementAndGet(), port);
        return server;
    }

    public static void stringResponseWithOk(HttpExchange httpExchange, String response) throws IOException {
        logger.info("Processing {}", httpExchange);
        httpExchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
