package ru.elytrium.host.api;

import java.lang.reflect.Field;

public class Config {
    public UsageCase usageCase;

    private String log_level;
    private String master_key;
    private String bucket_link;
    private String access_key;
    private String secret_link;
    private String wss_host;
    private String wss_port;
    private String db_host;

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

    public String getLog_level() {
        return log_level;
    }

    public String getMaster_key() {
        return master_key;
    }

    public String getBucket_link() {
        return bucket_link;
    }

    public String getAccess_key() {
        return access_key;
    }

    public String getSecret_link() {
        return secret_link;
    }

    public String getWss_host() {
        return wss_host;
    }

    public String getWss_port() {
        return wss_port;
    }

    public String getDb_host() {
        return db_host;
    }

    public enum UsageCase {
        MASTER,
        SLAVE
    }
}
