package ru.elytrahost.api;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import ru.elytrahost.api.model.user.User;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class Listener extends WebSocketServer {
    private final HashMap<WebSocket, User> userHashMap = new HashMap<>();

    public Listener(String hostname, int port) {
        super(new InetSocketAddress(hostname, port));
        ElytraHostAPI.getLogger().info(String.format("Server is starting on %s:%d", hostname, port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        userHashMap.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (!userHashMap.containsKey(webSocket)) {

        }
        else {
            userHashMap.get();
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {
        ElytraHostAPI.getLogger().info("Server has started successfully!");
    }
}
