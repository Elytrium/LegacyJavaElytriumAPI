package ru.elytrium.host.api;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.elytrium.host.api.db.Database;
import ru.elytrium.host.api.db.MongoDatabase;
import ru.elytrium.host.api.manager.shared.ConfigManager;
import ru.elytrium.host.api.model.module.Module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.UnknownHostException;

/**
 * Main class
 *
 * @author hevav
 * @since 0.0.1
 */
public class ElytraHostAPI {
    private static final Logger logger = LogManager.getLogger("ElytraHostAPI");
    private static final ConfigManager<Module> modules = new ConfigManager<>(Module.class, new File("module"));

    public static void main(String[] args) {
        try {
            Yaml yaml = new Yaml(new Constructor(Config.class));
            Config config = yaml.load(new FileReader("config.yml"));
            config.fillConfig(args);

            Configurator.setLevel("ElytraHostAPI", Level.toLevel(config.log_level, Level.WARN));

            Database database = new MongoDatabase(config.db_host);
            switch (config.usageCase) {
                case SLAVE:

                    break;
                case MASTER:
                    new MasterListener(config.wss_host, Integer.parseInt(config.wss_port));
                    break;
            }
        } catch (NoSuchFieldException | IllegalAccessException | UnknownHostException | FileNotFoundException e) {
            logger.fatal(e);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ConfigManager<Module> getModules() {
        return modules;
    }
}
