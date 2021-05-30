package ru.elytrium.host.api.request.authorized.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.util.TriConsumer;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.LinkedAccount;
import ru.elytrium.host.api.model.user.LinkedAccountType;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.authorized.user.model.PushLinkedAccountRequest;

import java.util.function.Consumer;

public class UserMethods extends RequestType {
    @Override
    public boolean proceedRequest(User user, String method, String payload, Consumer<String> reply) {
        try {
            Requests.valueOf(method).proceed(user, payload, reply);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void getInfo(User user, String payload, Consumer<String> send) {
        send.accept(ElytraHostAPI.getGson().toJson(user));
    }

    public static void pushLinkedAccount(User user, String payload, Consumer<String> send) {
        PushLinkedAccountRequest request = ElytraHostAPI.getGson().fromJson(payload, PushLinkedAccountRequest.class);
        LinkedAccountType linkedAccountType = ElytraHostAPI.getLinkedAccountTypes().getItem(request.linkedAccountType);
        LinkedAccount linkedAccount = linkedAccountType.toLinkedAccount(request.token);
        user.addLinkedAccounts(request.linkedAccountType, linkedAccount);
    }

    public enum Requests {
        GET_INFO(UserMethods::getInfo),
        PUSH_LINKED_ACCOUNT(UserMethods::pushLinkedAccount);

        private final TriConsumer<User, String, Consumer<String>> method;

        Requests(TriConsumer<User, String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<String> send) {
            method.accept(user, payload, send);
        }
    }
}
