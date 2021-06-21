package net.elytrium.api.utils;

import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.user.User;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class UserUtils {
    private static final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
            "[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
            "A-Z]{2,7}$";

    private static final Pattern pat = Pattern.compile(emailRegex);

    public static String genToken(int size) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static User getUser(String token) {
        return ElytriumAPI.getDatastore().find(User.class)
                .filter(Filters.eq("token", token))
                .first();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidEmailAddress(String email) {
        if (email.length() > 64) return false;
        return pat.matcher(email).matches();
    }
}
