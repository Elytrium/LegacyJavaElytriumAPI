package ru.elytrium.host.api;

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

public class SlaveListener implements HttpHandler, Listener {
    private final HttpServer server;

    public SlaveListener(String hostname, int port) throws IOException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        server.createContext("/api", this);
        server.setExecutor(threadPoolExecutor);
        server.start();

        ElytraHostAPI.getLogger().info(String.format("Slave server is starting on %s:%d", hostname, port));
    }

    @Override
    public void stop() {
        server.stop(0);
    }

    @Override
    public void handle(HttpExchange exchange) {
        SlaveRequest request = ElytraHostAPI.getGson().fromJson(new InputStreamReader(exchange.getRequestBody()), SlaveRequest.class);
        request.proceedRequest(ElytraHostAPI.getConfig().getMasterKey(), string -> {
            try {
                OutputStream outputStream = exchange.getResponseBody();
                byte[] answ = string.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, answ.length);
                outputStream.write(answ);
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
