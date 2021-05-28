package ru.elytrium.host.api;

import java.lang.reflect.Field;

public class Config {
    public UsageCase usageCase;

    public String log_level;
    public String master_key;
    public String bucket_link;
    public String access_key;
    public String secret_link;
    public String wss_host;
    public String wss_port;

    public String db_host;

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

    public enum UsageCase {
        MASTER,
        SLAVE
    }
}
