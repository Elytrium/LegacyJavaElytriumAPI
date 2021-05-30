package ru.elytrium.host.api.request.authorized.balance;

import org.apache.logging.log4j.util.TriConsumer;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.authorized.user.UserMethods;

import java.util.function.Consumer;

public class BalanceMethods extends RequestType {

    @Override
    public boolean proceedRequest(User user, String method, String payload, Consumer<String> reply) {
        try {
            UserMethods.Requests.valueOf(method).proceed(user, payload, reply);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public enum Requests {
        GEN_TOP_UP_LINK,
        LIST_METHODS,
        LIST_PENDING_PURCHASES;

        private final TriConsumer<User, String, Consumer<String>> method;

        Requests(TriConsumer<User, String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<String> send) {
            method.accept(user, payload, send);
        }
    }
}
