package ru.elytrium.host.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import ru.elytrium.host.api.request.Request;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MasterListener implements HttpHandler {
    public MasterListener(String hostname, int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        server.createContext("/api/v1", this);
        server.setExecutor(threadPoolExecutor);
        server.start();

        ElytraHostAPI.getLogger().info(String.format("Slave server is starting on %s:%d", hostname, port));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] host = exchange.getRequestURI().getPath().split(" ");
        String type = host[host.length - 2];
        String method = host[host.length - 1];
        String payload = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
        String token = exchange.getRequestHeaders().getFirst("Authorization");
        Request request = new Request(type, method, payload, token);

        request.proceedRequest(response -> {
            try {
                OutputStream outputStream = exchange.getResponseBody();
                String result = ElytraHostAPI.getGson().toJson(response);
                exchange.sendResponseHeaders(response.getCode(), result.length());
                outputStream.write(result.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                ElytraHostAPI.getLogger().fatal("Error while proceeding MasterRequest");
                ElytraHostAPI.getLogger().fatal(e);
            }
        });

        exchange.getResponseBody();
    }
}