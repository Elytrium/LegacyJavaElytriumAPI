package ru.elytrium.host.api.utils;

import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.User;

import java.security.SecureRandom;
import java.util.Arrays;

public class UserUtils {
    public static String genToken(int size) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return Arrays.toString(bytes);
    }

    public static User getUser(String token) {
        return ElytraHostAPI.getDatastore().find(User.class)
                .filter(Filters.eq("token", token))
                .first();
    }
}
