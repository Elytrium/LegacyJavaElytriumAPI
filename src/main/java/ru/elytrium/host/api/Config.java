package ru.elytrium.host.api;

import java.lang.reflect.Field;

public class Config {
    public UsageCase usageCase;

    private String logLevel;
    private String masterKey;
    private String bucketEndpoint;
    private String bucketRegion;
    private String accessKey;
    private String secretKey;
    private String apiHostname;
    private String apiPort;
    private String dbHost;
    private String dbName;
    private String mailFrom;
    private String mailRegSubject;
    private String mailRegBody;
    private String mailSendgridKey;
    private int tickInterval;

    public Config() {
    }

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

    public String getLogLevel() {
        return logLevel;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public String getBucketEndpoint() {
        return bucketEndpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getApiHostname() {
        return apiHostname;
    }

    public String getApiPort() {
        return apiPort;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbName() {
        return dbName;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public String getMailRegSubject() {
        return mailRegSubject;
    }

    public String getMailRegBody() {
        return mailRegBody;
    }

    public String getMailSendgridKey() {
        return mailSendgridKey;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public String getBucketRegion() {
        return bucketRegion;
    }

    public enum UsageCase {
        MASTER,
        SLAVE
    }
}
