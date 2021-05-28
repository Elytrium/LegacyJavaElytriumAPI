package ru.elytrium.host.api.request.authorized;

import ru.elytrium.host.api.model.user.User;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AuthorizedRequest {
    private String type;
    private String method;
    private String payload;

    public boolean proceedRequest(User user, Consumer<String> reply) {
        try {
            switch (type) {
                case "USER":
                    USER.valueOf(method).proceed(payload, reply);
                    return true;
                case "BALANCE":
                    BALANCE.valueOf(method).proceed(payload, reply);
                    return true;
                case "INSTANCE":
                    INSTANCE.valueOf(method).proceed(payload, reply);
                    return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    private static class UserMethods {
        public static void login(String payload, Consumer<String> send) {

        }
    }

    public enum USER {
        LOGIN,
        LOGIN_VIA_LINKED_ACCOUNT,
        GET_INFO,
        LIST_LINKED_ACCOUNT,
        REGISTER,
        PUSH_LINKED_ACCOUNT;

        private final BiConsumer<String, Consumer<String>> method;

        USER(BiConsumer<String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(String payload, Consumer<String> send) {
            method.accept(payload, send);
        }
    }

    public enum BALANCE {
        GEN_TOP_UP_LINK,
        LIST_METHODS,
        LIST_PENDING_PURCHASES;

        private final BiConsumer<String, Consumer<String>> method;

        BALANCE(BiConsumer<String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(String payload, Consumer<String> send) {
            method.accept(payload, send);
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

        private final BiConsumer<String, Consumer<String>> method;

        INSTANCE(BiConsumer<String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(String payload, Consumer<String> send) {
            method.accept(payload, send);
        }
    }
}
