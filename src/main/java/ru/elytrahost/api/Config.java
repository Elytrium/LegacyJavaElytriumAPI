package ru.elytrahost.api;

import java.lang.reflect.Field;

public class Config {
    public String log_level;
    public String wss_host;
    public String wss_port;
    public String db_host;
    public String db_user;
    public String db_password;
    public String db_name;

    public void fillConfig(String[] args) throws NoSuchFieldException, IllegalAccessException {
        for (Field field : Config.class.getFields()) {
            String fieldName = field.getName();
            if (System.getenv(fieldName) != null) field.set(this, System.getenv(fieldName));
        }

        for (String arg : args) {
            String[] argArray = arg.split("=");
            Config.class.getField(argArray[0]).set(this, argArray[1]);
        }
    }
}
