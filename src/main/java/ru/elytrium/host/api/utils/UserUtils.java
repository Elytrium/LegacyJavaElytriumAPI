package ru.elytrium.host.api.utils;

import java.security.SecureRandom;
import java.util.Arrays;

public class UserUtils {
    public static String genToken(int size) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return Arrays.toString(bytes);
    }
}
