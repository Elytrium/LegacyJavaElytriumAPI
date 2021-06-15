package ru.elytrium.host.api;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClients;
import com.sendgrid.SendGrid;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.springframework.boot.SpringApplication;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import ru.elytrium.host.api.manager.master.BalanceManager;
import ru.elytrium.host.api.manager.master.InstanceManager;
import ru.elytrium.host.api.manager.shared.config.ConfigManager;
import ru.elytrium.host.api.manager.shared.storage.StorageManager;
import ru.elytrium.host.api.manager.shared.utils.TickManager;
import ru.elytrium.host.api.model.Exclude;
import ru.elytrium.host.api.model.backend.AutoExpandInstruction;
import ru.elytrium.host.api.model.backend.StaticBackendInstance;
import ru.elytrium.host.api.model.balance.TopUpMethod;
import ru.elytrium.host.api.model.captcha.CaptchaBackend;
import ru.elytrium.host.api.model.module.Module;
import ru.elytrium.host.api.model.user.LinkedAccountType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * Main class
 *
 * @author hevav
 * @since 0.0.1
 */
public class ElytraHostAPI {
    private static final ExclusionStrategy strategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            return field.getAnnotation(Exclude.class) != null;
        }
    };

    private static final Logger logger = LogManager.getLogger("ElytraHostAPI");
    private static final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(strategy).create();
    private static String[] args;
    private static ConfigManager<Module> modules;
    private static ConfigManager<LinkedAccountType> linkedAccountTypes;
    private static ConfigManager<TopUpMethod> topUpMethods;
    private static ConfigManager<CaptchaBackend> captchaBackends;
    private static ConfigManager<AutoExpandInstruction> autoExpandInstructions;
    private static ConfigManager<StaticBackendInstance> staticBackends;
    private static Config config;
    private static Datastore datastore;
    private static SendGrid sendGrid;
    private static InstanceManager instanceManager;
    private static TickManager tickManager;
    private static StorageManager storageManager;
    private static Random random;

    public static void main(String[] args) {
        ElytraHostAPI.args = args;
        configLoad();
        listenerLoad();
        console();
    }

    public static void listenerLoad() {
        SpringApplication.run(ElytraHostAPI.class, args);
    }

    public static void configLoad() {
        try {
            Yaml yaml = new Yaml(new Constructor(Config.class));
            yaml.setBeanAccess(BeanAccess.FIELD);
            config = yaml.load(new FileReader("config.yml"));
            config.fillConfig(args);

            Configurator.setLevel("ElytraHostAPI", Level.toLevel(config.getLogLevel(), Level.WARN));
            modules = new ConfigManager<>(Module.class, new File("module"));
            linkedAccountTypes = new ConfigManager<>(LinkedAccountType.class, new File("linkedAccount"));
            topUpMethods = new ConfigManager<>(TopUpMethod.class, new File("topUpMethods"));
            captchaBackends = new ConfigManager<>(CaptchaBackend.class, new File("captchaBackends"));
            autoExpandInstructions = new ConfigManager<>(AutoExpandInstruction.class, new File("autoExpand"));
            staticBackends = new ConfigManager<>(StaticBackendInstance.class, new File("backend"));

            datastore = Morphia.createDatastore(MongoClients.create(config.getDbHost()), config.getDbName());
            datastore.getMapper().mapPackage("ru.elytrium.host.api.model");

            sendGrid = new SendGrid(System.getenv(config.getMailSendgridKey()));
            instanceManager = new InstanceManager();

            if (config.isDoTimerTasks()) {
                tickManager = new TickManager(config.getTickInterval());
                tickManager.unregister("InstanceManager");
                tickManager.unregister("BalanceManager");
                tickManager.register("InstanceManager", instanceManager);
                tickManager.register("BalanceManager", new BalanceManager());
            }

            storageManager = new StorageManager(
                    config.getBucketEndpoint(),
                    config.getBucketRegion(),
                    config.getAccessKey(),
                    config.getSecretKey()
            );
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            logger.fatal(e);
        }
    }

    public static void console() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String cmd = scanner.next();
            String[] cmdSplit = cmd.split(" ");
            switch (cmdSplit[0]) {
                case "stop":
                    System.out.println("Bye!");
                    System.exit(0);
                    break;
                case "reload":
                    System.out.println("Reloading..");
                    configLoad();
                    System.out.println("Reload completed");
                    break;
            }
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ConfigManager<Module> getModules() {
        return modules;
    }

    public static ConfigManager<LinkedAccountType> getLinkedAccountTypes() {
        return linkedAccountTypes;
    }

    public static ConfigManager<TopUpMethod> getTopUpMethods() {
        return topUpMethods;
    }

    public static Config getConfig() {
        return config;
    }

    public static Datastore getDatastore() {
        return datastore;
    }

    public static Gson getGson() {
        return gson;
    }

    public static ConfigManager<CaptchaBackend> getCaptchaBackends() {
        return captchaBackends;
    }

    public static SendGrid getSendGrid() {
        return sendGrid;
    }

    public static TickManager getTickManager() {
        return tickManager;
    }

    public static InstanceManager getInstanceManager() {
        return instanceManager;
    }

    public static StorageManager getStorageManager() {
        return storageManager;
    }

    public static ConfigManager<AutoExpandInstruction> getAutoExpandInstructions() {
        return autoExpandInstructions;
    }

    public static ConfigManager<StaticBackendInstance> getStaticBackends() {
        return staticBackends;
    }

    public static Random getRandom() {
        return random;
    }
}
