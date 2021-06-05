package ru.elytrium.host.api.request.authorized;

import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.Request;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.request.authorized.balance.BalanceMethods;
import ru.elytrium.host.api.request.authorized.instance.InstanceMethods;
import ru.elytrium.host.api.request.authorized.user.UserMethods;

import java.util.function.Consumer;

public class AuthorizedRequest {
    private String type;
    private String method;
    private String payload;

    public AuthorizedRequest(Request request) {
        type = request.getType();
        method = request.getMethod();
        payload = request.getPayload();
    }

    public void proceedRequest(String token, Consumer<Response> reply) {
        User user = ElytraHostAPI.getDatastore()
                .find(User.class)
                .filter(Filters.eq("token", token))
                .first();

        if (user == null) {
            reply.accept(
                    Response.genUnauthorizedResponse("Wrong token (Неверный токен)")
            );
        } else {
            proceedRequest(user, reply);
        }
    }

    public void proceedRequest(User user, Consumer<Response> reply) {
        try {
            Types.valueOf(type).proceed(user, method, payload, reply);
        } catch (IllegalArgumentException e) {
            reply.accept(
                    Response.genBadRequestResponse("Wrong method (неверный метод)")
            );
        }
        reply.accept(
                Response.genBadRequestResponse("Wrong method (неверный метод)")
        );
    }


    public enum Types {
        USER(new UserMethods()),
        BALANCE(new BalanceMethods()),
        INSTANCE(new InstanceMethods());

        private final RequestType type;

        Types(RequestType method) {
            this.type = method;
        }

        public void proceed(User user, String method, String payload, Consumer<Response> send) {
            type.proceedRequest(user, method, payload, send);
        }
    }
}
