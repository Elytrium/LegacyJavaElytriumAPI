package net.elytrium.api;

import com.github.f4b6a3.uuid.codec.StringCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import net.elytrium.api.manager.master.BalanceManager;
import net.elytrium.api.manager.master.InstanceManager;
import net.elytrium.api.manager.shared.config.ConfigManager;
import net.elytrium.api.manager.shared.storage.StorageManager;
import net.elytrium.api.manager.shared.utils.TickManager;
import net.elytrium.api.model.Exclude;
import net.elytrium.api.model.backend.AutoExpandInstruction;
import net.elytrium.api.model.backend.StaticBackendInstance;
import net.elytrium.api.model.balance.TopUpMethod;
import net.elytrium.api.model.captcha.CaptchaBackend;
import net.elytrium.api.model.meta.Meta;
import net.elytrium.api.model.module.Module;
import net.elytrium.api.model.user.LinkedAccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

/**
 * Main class
 *
 * @author hevav
 * @since 0.0.1
 */

@SpringBootApplication
public class ElytriumAPI {
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

    private static final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(strategy).create();
    private static final Logger logger = LoggerFactory.getLogger(ElytriumAPI.class);
    private static final UuidCodec<String> uuidCodec = new StringCodec();
    private static String[] args;
    private static ConfigManager<Module> modules;
    private static ConfigManager<LinkedAccountType> linkedAccountTypes;
    private static ConfigManager<TopUpMethod> topUpMethods;
    private static ConfigManager<CaptchaBackend> captchaBackends;
    private static ConfigManager<AutoExpandInstruction> autoExpandInstructions;
    private static ConfigManager<StaticBackendInstance> staticBackends;
    private static Config config;
    private static Datastore datastore;
    private static InstanceManager instanceManager;
    private static TickManager tickManager;
    private static StorageManager storageManager;
    private static Random random;
    private static Meta meta;
    private static Session emailSession;

    public static void main(String[] args) {
        ElytriumAPI.args = args;
        configLoad();
        listenerLoad();
        afterLoad();
        console();
    }

    public static void listenerLoad() {
        switch (config.getUsageCase()) {
            case MASTER:
                System.setProperty("elytrium.master", "true");
                break;
            case SLAVE:
                System.setProperty("elytrium.slave", "true");
                break;
        }

        System.setProperty("server.address", config.getApiHostname());
        System.setProperty("server.port", config.getApiPort());
        SpringApplication.run(ElytriumAPI.class, args);
    }

    public static void configLoad() {
        try {
            Yaml yaml = new Yaml(new Constructor(Config.class));
            yaml.setBeanAccess(BeanAccess.FIELD);
            config = yaml.load(new FileReader("config.yml"));
            config.fillConfig(args);

            System.setProperty("logging.level.root", config.getLogLevel());
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            logger.error(e.toString());
        }
    }

    public static void afterLoad() {
        modules = new ConfigManager<>(Module.class, new File("module"));
        linkedAccountTypes = new ConfigManager<>(LinkedAccountType.class, new File("linkedAccount"));
        topUpMethods = new ConfigManager<>(TopUpMethod.class, new File("topUpMethods"));
        captchaBackends = new ConfigManager<>(CaptchaBackend.class, new File("captchaBackends"));
        autoExpandInstructions = new ConfigManager<>(AutoExpandInstruction.class, new File("autoExpand"));
        staticBackends = new ConfigManager<>(StaticBackendInstance.class, new File("backend"));

        datastore = Morphia.createDatastore(MongoClients.create(config.getDbHost()), config.getDbName());
        datastore.getMapper().mapPackage("ru.elytrium.host.api.model");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", config.getMailHost());
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.port", 465);
        prop.put("mail.smtp.auth", "true");

        emailSession = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getMailUser(), config.getMailPassword());
            }
        });

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

        random = new Random();

        meta = datastore.find(Meta.class).first();
        if (meta == null) {
            meta = new Meta();
            datastore.save(meta);
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
                    afterLoad();
                    System.out.println("Reload completed");
                    break;
            }
        }
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

    public static Meta getMeta() {
        return meta;
    }

    public static Session getEmailSession() {
        return emailSession;
    }

    public static UuidCodec<String> getUuidCodec() {
        return uuidCodec;
    }
}
