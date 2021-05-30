package ru.elytrium.host.api.request.unauthorized;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.unauthorized.user.LoginRequest;
import ru.elytrium.host.api.request.unauthorized.user.LoginViaLinkedAccountRequest;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UnauthorizedRequest {
    private String type;
    private String method;
    private String payload;

    public boolean proceedRequest(Consumer<User> authorize) {
        try {
            USER.valueOf(method).proceed(payload, authorize);
        } catch (IllegalArgumentException exception) {
            return false;
        }
        return false;
    }

    private static class UserMethods {
        public static void login(String payload, Consumer<User> authorize) {
            LoginRequest request = ElytraHostAPI.getGson().fromJson(payload, LoginRequest.class);
        }

        public static void loginViaLinkedAccount(String payload, Consumer<User> authorize) {
            LoginViaLinkedAccountRequest request = ElytraHostAPI.getGson().fromJson(payload, LoginViaLinkedAccountRequest.class);
        }

        public static void register(String payload, Consumer<User> authorize) {
            LoginRequest request = ElytraHostAPI.getGson().fromJson(payload, LoginRequest.class);
        }
    }

    public enum USER {
        LOGIN(UserMethods::login),
        LOGIN_VIA_LINKED_ACCOUNT(UserMethods::loginViaLinkedAccount),
        REGISTER(UserMethods::register);

        private final BiConsumer<String, Consumer<User>> method;

        USER(BiConsumer<String, Consumer<User>> method) {
            this.method = method;
        }

        public void proceed(String payload, Consumer<User> authorize) {
            method.accept(payload, authorize);
        }
    }
}
