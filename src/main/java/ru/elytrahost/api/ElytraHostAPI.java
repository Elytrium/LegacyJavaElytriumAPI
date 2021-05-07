package ru.elytrahost.api;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.elytrahost.api.db.Database;
import ru.elytrahost.api.db.MySQLDatabase;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;

/**
 * Main class
 *
 * @author hevav
 * @since 0.0.1
 */
public class ElytraHostAPI {
    private static final Logger logger = LogManager.getLogger("ElytraHostAPI");

    public static void main(String[] args) {
        try {
            Yaml yaml = new Yaml(new Constructor(Config.class));
            Config config = yaml.load(new FileReader("config.yml"));
            config.fillConfig(args);

            Configurator.setLevel("ElytraHostAPI", Level.toLevel(config.log_level, Level.WARN));

            Database database = new MySQLDatabase(config.db_host, config.db_name, config.db_user, config.db_password);
            Listener listener = new Listener(config.wss_host, Integer.parseInt(config.wss_port));
        } catch (NoSuchFieldException | IllegalAccessException | SQLException | FileNotFoundException e) {
            logger.fatal(e);
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
