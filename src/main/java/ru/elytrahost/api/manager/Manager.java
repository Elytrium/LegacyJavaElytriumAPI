package ru.elytrahost.api.manager;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import ru.elytrahost.api.ElytraHostAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Manager <T> {
    private final Map<String, T> items;

    public Manager(Class<T> tClass, File directory) {
        this.items = new HashMap<>();
        File[] moduleFiles = directory.listFiles(File::isFile);

        if (moduleFiles == null) {
            ElytraHostAPI.getLogger().warn("Manager: " + directory + " contains no elements");
            return;
        }

        Yaml yaml = new Yaml(new Constructor(tClass));
        try {
            for (File moduleFile : moduleFiles) {
                items.put(moduleFile.getName().replaceFirst("[.][^.]+$", ""), yaml.load(new FileReader(moduleFile.getAbsoluteFile())));
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
}
