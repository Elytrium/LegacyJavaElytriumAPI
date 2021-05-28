package ru.elytrium.host.api;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.authorized.AuthorizedRequest;
import ru.elytrium.host.api.request.unauthorized.UnauthorizedRequest;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class MasterListener extends WebSocketServer {
    private final HashMap<WebSocket, User> userHashMap = new HashMap<>();
    private final Gson gson = new Gson();

    public MasterListener(String hostname, int port) {
        super(new InetSocketAddress(hostname, port));
        ElytraHostAPI.getLogger().info(String.format("Master server is starting on %s:%d", hostname, port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        userHashMap.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        if (!userHashMap.containsKey(webSocket)) {
            UnauthorizedRequest request = gson.fromJson(message, UnauthorizedRequest.class);
            boolean succeed = request.proceedRequest(user -> {

            });

            if (!succeed) {
                webSocket.send("401 Unauthorized, please contact support");
            }
        }
        else {
            User user = userHashMap.get(webSocket);
            AuthorizedRequest request = gson.fromJson(message, AuthorizedRequest.class);
            boolean succeed = request.proceedRequest(user, webSocket::send);

            if (!succeed) {
                webSocket.send("404 Not found, please contact support");
            }
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        userHashMap.remove(webSocket);
    }

    @Override
    public void onStart() {
        ElytraHostAPI.getLogger().info("Master server was started successfully!");
    }

}
