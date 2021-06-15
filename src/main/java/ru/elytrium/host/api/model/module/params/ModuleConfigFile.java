package ru.elytrium.host.api.model.module.params;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.shared.serializer.*;
import ru.elytrium.host.api.model.Exclude;
import ru.elytrium.host.api.utils.VersionUtils;

import java.io.File;
import java.util.List;

@Entity("module_config_file")
public class ModuleConfigFile {

    public String folder;

    public String filename;

    @Exclude
    public FileMethod method;

    @Reference
    public List<ModuleConfig> configs;

    public enum FileMethod {
        YAML,
        JSON,
        PROPERTIES
    }

    public void deserialize(ModuleVersion version, File file) {
        try {
            SerializeProvider provider = getSerializeProvider();

            SerializeManager config = provider.load(file);

            configs.stream()
                    .filter(e -> VersionUtils.checkVersion(e.versionRange, version.getVersion()))
                    .forEach(e -> config.set(e.name, e.value));

            provider.save(config, file);
        } catch (Exception e) {
            ElytraHostAPI.getLogger().fatal("Error while proceeding deserialization of " + filename);
            ElytraHostAPI.getLogger().fatal(e);
        }
    }

    public void serialize(ModuleVersion version, File file) {
        try {
            SerializeProvider provider = getSerializeProvider();

            SerializeManager config = provider.load(file);

            configs.stream()
                    .filter(e -> VersionUtils.checkVersion(e.versionRange, version.getVersion()))
                    .forEach(e -> e.value = config.get(e.name).toString());
        } catch (Exception e) {
            ElytraHostAPI.getLogger().fatal("Error while proceeding serialization of " + filename);
            ElytraHostAPI.getLogger().fatal(e);
        }
    }

    private SerializeProvider getSerializeProvider() throws Exception {
        switch (method) {
            case YAML:
                return new YamlConfiguration();
            case JSON:
                return new JsonConfiguration();
            case PROPERTIES:
                return new PropertiesConfiguration();
            default:
                throw new Exception("Unknown method");
        }
    }
}
