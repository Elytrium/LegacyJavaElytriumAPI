package ru.elytrium.host.api.request.authorized;

import com.google.gson.Gson;
import org.apache.logging.log4j.util.TriConsumer;
import ru.elytrium.host.api.model.user.LinkedAccountType;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.authorized.user.PushLinkedAccountRequest;

import java.util.function.Consumer;

public class AuthorizedRequest {
    private String type;
    private String method;
    private String payload;

    public boolean proceedRequest(User user, Consumer<String> reply) {
        try {
            switch (type) {
                case "USER":
                    USER.valueOf(method).proceed(user, payload, reply);
                    return true;
                case "BALANCE":
                    BALANCE.valueOf(method).proceed(user, payload, reply);
                    return true;
                case "INSTANCE":
                    INSTANCE.valueOf(method).proceed(user, payload, reply);
                    return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    private static class UserMethods {
        private static final Gson gson = new Gson();

        public static void getInfo(User user, String payload, Consumer<String> send) {
            send.accept(gson.toJson(user));
        }

        public static void pushLinkedAccount(User user, String payload, Consumer<String> send) {
            PushLinkedAccountRequest request = gson.fromJson(payload, PushLinkedAccountRequest.class);
            LinkedAccountType linkedAccountType = ;
        }
    }

    public enum USER {
        GET_INFO(UserMethods::getInfo),
        PUSH_LINKED_ACCOUNT;

        private final TriConsumer<User, String, Consumer<String>> method;

        USER(TriConsumer<User, String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<String> send) {
            method.accept(user, payload, send);
        }
    }

    public enum BALANCE {
        GEN_TOP_UP_LINK,
        LIST_METHODS,
        LIST_PENDING_PURCHASES;

        private final TriConsumer<User, String, Consumer<String>> method;

        BALANCE(TriConsumer<User, String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<String> send) {
            method.accept(user, payload, send);
        }
    }

    public enum INSTANCE {
        LIST_AVAILABLE,
        CREATE,
        REMOVE,
        RUN,
        PAUSE,
        STOP,
        GET_INFO,
        GET_TEMP_S3_LINK,
        UPDATE;

        private final TriConsumer<User, String, Consumer<String>> method;

        INSTANCE(TriConsumer<User, String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<String> send) {
            method.accept(user, payload, send);
        }
    }
}
