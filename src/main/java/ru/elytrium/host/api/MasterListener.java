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
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MasterListener implements HttpHandler, Listener {
    private final HttpServer server;

    public MasterListener(String hostname, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        server.createContext("/v1", this);
        server.setExecutor(threadPoolExecutor);
        server.start();

        ElytraHostAPI.getLogger().info(String.format("Master server is starting on %s:%d", hostname, port));
    }

    @Override
    public void stop() {
        server.stop(0);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        String[] host = path.split("/");
        String type = host[host.length - 2].toUpperCase(Locale.ROOT);
        String method = host[host.length - 1].toUpperCase(Locale.ROOT);
        String payload = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
        String token = exchange.getRequestHeaders().getFirst("Authorization");
        Request request = new Request(type, method, payload, token);

        request.proceedRequest(response -> {
            try {
                String result = ElytraHostAPI.getGson().toJson(response);
                byte[] answ = result.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(response.getCode(), answ.length);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(answ);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                ElytraHostAPI.getLogger().fatal("Error while proceeding MasterRequest");
                ElytraHostAPI.getLogger().fatal(e);
            }
        });
    }
}