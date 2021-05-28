package ru.elytrium.host.api;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import ru.elytrium.host.api.request.slave.SlaveRequest;

import java.net.InetSocketAddress;

public class SlaveListener extends WebSocketServer {
    private final Gson gson = new Gson();
    private final String masterKey;

    public SlaveListener(String hostname, int port, String masterKey) {
        super(new InetSocketAddress(hostname, port));
        this.masterKey = masterKey;
        ElytraHostAPI.getLogger().info(String.format("Master server is starting on %s:%d", hostname, port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        SlaveRequest request = gson.fromJson(message, SlaveRequest.class);
        request.proceedRequest(masterKey, webSocket::send);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {
        ElytraHostAPI.getLogger().info("Master server was started successfully!");
    }

}
