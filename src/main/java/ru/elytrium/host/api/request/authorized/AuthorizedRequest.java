package ru.elytrium.host.api.request.authorized;

import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.authorized.balance.BalanceMethods;
import ru.elytrium.host.api.request.authorized.instance.InstanceMethods;
import ru.elytrium.host.api.request.authorized.user.UserMethods;

import java.util.function.Consumer;

public class AuthorizedRequest {
    private String type;
    private String method;
    private String payload;

    public boolean proceedRequest(User user, Consumer<String> reply) {
        try {
            Types.valueOf(type).proceed(user, method, payload, reply);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }


    public enum Types {
        USER(new UserMethods()),
        BALANCE(new BalanceMethods()),
        INSTANCE(new InstanceMethods());

        private final RequestType type;

        Types(RequestType method) {
            this.type = method;
        }

        public void proceed(User user, String method, String payload, Consumer<String> send) {
            type.proceedRequest(user, method, payload, send);
        }
    }
}
