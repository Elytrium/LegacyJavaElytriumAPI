package ru.elytrium.host.api.request;

import ru.elytrium.host.api.model.user.User;

import java.util.function.Consumer;

public abstract class RequestType {
    public abstract boolean proceedRequest(User user, String method, String payload, Consumer<Response> reply);
}
