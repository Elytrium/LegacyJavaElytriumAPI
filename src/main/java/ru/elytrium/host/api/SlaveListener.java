package ru.elytrium.host.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.elytrium.host.api.request.slave.SlaveRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SlaveListener implements HttpHandler {
    public SlaveListener(String hostname, int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        server.createContext("/api", this);
        server.setExecutor(threadPoolExecutor);
        server.start();

        ElytraHostAPI.getLogger().info(String.format("Slave server is starting on %s:%d", hostname, port));
    }

    @Override
    public void handle(HttpExchange exchange) {
        SlaveRequest request = ElytraHostAPI.getGson().fromJson(new InputStreamReader(exchange.getRequestBody()), SlaveRequest.class);
        request.proceedRequest(ElytraHostAPI.getConfig().getMaster_key(), string -> {
            try {
                OutputStream outputStream = exchange.getResponseBody();
                exchange.sendResponseHeaders(200, string.length());
                outputStream.write(string.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                ElytraHostAPI.getLogger().fatal("Error while proceeding SlaveRequest");
                ElytraHostAPI.getLogger().fatal(e);
            }
        });

        exchange.getResponseBody();
    }
}
