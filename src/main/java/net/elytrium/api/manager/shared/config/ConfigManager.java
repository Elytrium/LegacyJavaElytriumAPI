package net.elytrium.api.manager.shared.config;

import com.google.common.collect.Iterables;
import net.elytrium.api.ElytriumAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager<T> {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private final Map<String, T> items;

    public ConfigManager(Class<T> tClass, File directory) {
        this.items = new HashMap<>();
        File[] moduleFiles = directory.listFiles(File::isFile);

        if (moduleFiles == null) {
            logger.warn(directory + " contains no elements");
            return;
        }

        Yaml yaml = new Yaml(new Constructor(tClass));
        yaml.setBeanAccess(BeanAccess.FIELD);
        try {
            for (File moduleFile : moduleFiles) {
                String itemName = moduleFile.getName().replaceFirst("[.][^.]+$", "");
                items.put(itemName, yaml.load(new FileReader(moduleFile.getAbsoluteFile())));
                logger.info("Loading " + directory + "/" + moduleFile.getName());
            }
        } catch (FileNotFoundException ignored) { }
    }

    public void addItem(String name, T item) {
        items.put(name, item);
    }

    public Set<String> getItemNames() {
        return items.keySet();
    }

    public Collection<T> getAllItems() {
        return items.values();
    }

    public T getItem(String o) {
        return items.get(o);
    }

    public T getRandomItem() {
        int size = items.size();
        int cur = ElytriumAPI.getRandom().nextInt(size - 1);
        return Iterables.get(items.values(), cur);
    }
}
