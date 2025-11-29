package com.food.delivery.server;

import com.food.delivery.store.FoodDataStore;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpJsonServer {
    private final int port;
    private final FoodDataStore dataStore;

    public HttpJsonServer(int port) {
        this.port = port;
        this.dataStore = new FoodDataStore();
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/health", new HealthHandler());
            server.createContext("/merchants", new MerchantsHandler());
            server.createContext("/dishes", new DishesHandler());
            server.createContext("/orders", new OrdersHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Food delivery backend started on http://localhost:" + port);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start HTTP server", e);
        }
    }

    private abstract class JsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                byte[] response = process(exchange).getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            } catch (IllegalArgumentException ex) {
                byte[] response = ex.getMessage().getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(400, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }

        protected abstract String process(HttpExchange exchange) throws IOException;
    }

    private class HealthHandler extends JsonHandler {
        @Override
        protected String process(HttpExchange exchange) {
            return "{\"status\":\"ok\"}";
        }
    }

    private class MerchantsHandler extends JsonHandler {
        @Override
        protected String process(HttpExchange exchange) {
            List<String> merchants = dataStore.getMerchants().stream()
                    .map(m -> String.format("{\"id\":%d,\"name\":\"%s\",\"address\":\"%s\"}", m.id(), escape(m.name()), escape(m.address())))
                    .collect(Collectors.toList());
            return jsonArray(merchants);
        }
    }

    private class DishesHandler extends JsonHandler {
        @Override
        protected String process(HttpExchange exchange) {
            Map<String, String> params = QueryUtils.parseQuery(exchange.getRequestURI().getRawQuery());
            Optional<Long> merchantId = QueryUtils.getLong(params, "merchantId");
            return jsonArray(dataStore.getDishes(merchantId).stream()
                    .map(d -> String.format("{\"id\":%d,\"merchantId\":%d,\"name\":\"%s\",\"price\":%.2f}",
                            d.id(), d.merchantId(), escape(d.name()), d.price()))
                    .collect(Collectors.toList()));
        }
    }

    private class OrdersHandler extends JsonHandler {
        @Override
        protected String process(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = QueryUtils.parseBody(body);
                long merchantId = QueryUtils.getLong(params, "merchantId")
                        .orElseThrow(() -> new IllegalArgumentException("merchantId is required"));
                int itemCount = QueryUtils.getInt(params, "items")
                        .orElseThrow(() -> new IllegalArgumentException("items is required"));
                long orderId = dataStore.createOrder(merchantId, itemCount);
                return String.format("{\"id\":%d,\"status\":\"CREATED\"}", orderId);
            }

            List<String> orders = dataStore.getOrders().stream()
                    .map(o -> String.format("{\"id\":%d,\"merchantId\":%d,\"items\":%d,\"status\":\"%s\"}",
                            o.id(), o.merchantId(), o.items(), escape(o.status())))
                    .collect(Collectors.toList());
            return jsonArray(orders);
        }
    }

    private String jsonArray(List<String> entries) {
        return "[" + String.join(",", entries) + "]";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
