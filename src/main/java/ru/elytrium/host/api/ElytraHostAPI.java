package ru.elytrium.host.api;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.elytrium.host.api.db.Database;
import ru.elytrium.host.api.db.MongoDatabase;
import ru.elytrium.host.api.manager.shared.ConfigManager;
import ru.elytrium.host.api.model.Exclude;
import ru.elytrium.host.api.model.balance.TopUpMethod;
import ru.elytrium.host.api.model.module.Module;
import ru.elytrium.host.api.model.user.LinkedAccountType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;

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
    private static Config config;
    private static Database database;

    public static void main(String[] args) {
        try {
            ElytraHostAPI.args = args;

            configLoad();
            listenerLoad();
        } catch (NoSuchFieldException | IllegalAccessException | IOException e) {
            logger.fatal(e);
        }
    }

    public static void listenerLoad() throws IOException {
        switch (config.usageCase) {
            case SLAVE:
                new SlaveListener(config.getWss_host(), Integer.parseInt(config.getWss_port()));
                break;
            case MASTER:
                new MasterListener(config.getWss_host(), Integer.parseInt(config.getWss_port()));
                break;
        }
    }

    public static void configLoad() throws IOException, NoSuchFieldException, IllegalAccessException {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        config = yaml.load(new FileReader("config.yml"));
        config.fillConfig(args);

        Configurator.setLevel("ElytraHostAPI", Level.toLevel(config.getLog_level(), Level.WARN));
        modules = new ConfigManager<>(Module.class, new File("module"));
        linkedAccountTypes = new ConfigManager<>(LinkedAccountType.class, new File("linkedAccount"));
        topUpMethods = new ConfigManager<>(TopUpMethod.class, new File("topUpMethods"));

        database = new MongoDatabase(config.getDb_host());
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

    public static Database getDatabase() {
        return database;
    }

    public static Gson getGson() {
        return gson;
    }
}
