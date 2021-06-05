package ru.elytrium.host.api.request;

import ru.elytrium.host.api.request.authorized.AuthorizedRequest;
import ru.elytrium.host.api.request.unauthorized.UnauthorizedRequest;

import java.util.function.Consumer;

public class Request {
    private String type;
    private String method;
    private String payload;
    private String token;

    public Request(String type, String method, String payload, String token) {
        this.type = type;
        this.method = method;
        this.payload = payload;
        this.token = token;
    }

    public void proceedRequest(Consumer<Response> reply) {
        if (token == null) {
            UnauthorizedRequest request = new UnauthorizedRequest(this);
            request.proceedRequest(reply);
        } else {
            AuthorizedRequest request = new AuthorizedRequest(this);
            request.proceedRequest(token, reply);
        }
    }

    public String getType() {
        return type;
    }

    public String getMethod() {
        return method;
    }

    public String getPayload() {
        return payload;
    }

    public String getToken() {
        return payload;
    }
}
