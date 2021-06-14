package ru.elytrium.host.api;

import java.lang.reflect.Field;

@SuppressWarnings("FieldMayBeFinal")
public class Config {
    private UsageCase usageCase;

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
    private boolean doTimerTasks = true;
    private String registryUrl;
    private String registryUser;
    private String registryPassword;

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

    public UsageCase getUsageCase() {
        return usageCase;
    }

    public boolean isDoTimerTasks() {
        return doTimerTasks;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public String getRegistryUser() {
        return registryUser;
    }

    public String getRegistryPassword() {
        return registryPassword;
    }

    public enum UsageCase {
        MASTER,
        SLAVE
    }
}
