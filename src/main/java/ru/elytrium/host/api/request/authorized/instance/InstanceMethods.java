package ru.elytrium.host.api.request.authorized.instance;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.util.TriConsumer;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.authorized.user.UserMethods;

import java.util.function.Consumer;

public class InstanceMethods extends RequestType {
    @Override
    public boolean proceedRequest(User user, String method, String payload, Consumer<String> reply) {
        try {
            UserMethods.Requests.valueOf(method).proceed(user, payload, reply);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void listAvailable(User user, String payload, Consumer<String> send) {
        send.accept(ElytraHostAPI.getGson().toJson(ElytraHostAPI.getModules().getAllItems()));
    }

    public static void create(User user, String payload, Consumer<String> send) {
        send.accept(ElytraHostAPI.getGson().toJson(ElytraHostAPI.getModules().getAllItems()));
    }

    public enum Requests {
        LIST_AVAILABLE(InstanceMethods::listAvailable),
        CREATE,
        REMOVE,
        RUN,
        PAUSE,
        STOP,
        GET_INFO,
        GET_TEMP_S3_LINK,
        UPDATE;

        private final TriConsumer<User, String, Consumer<String>> method;

        Requests(TriConsumer<User, String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<String> send) {
            method.accept(user, payload, send);
        }
    }
}
