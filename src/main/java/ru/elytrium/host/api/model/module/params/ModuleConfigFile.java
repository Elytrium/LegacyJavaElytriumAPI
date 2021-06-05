package ru.elytrium.host.api.model.module.params;

import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.shared.SerializeManager;
import ru.elytrium.host.api.manager.shared.serializer.JsonConfiguration;
import ru.elytrium.host.api.manager.shared.serializer.PropertiesConfiguration;
import ru.elytrium.host.api.manager.shared.serializer.SerializeProvider;
import ru.elytrium.host.api.manager.shared.serializer.YamlConfiguration;
import ru.elytrium.host.api.model.Exclude;
import ru.elytrium.host.api.utils.VersionUtils;

import java.io.File;
import java.util.List;

public class ModuleConfigFile {

    public String folder;

    public String filename;

    @Exclude
    public FileMethod method;

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
                    .filter(e -> VersionUtils.checkVersion(e.versionRange, version.version))
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
                    .filter(e -> VersionUtils.checkVersion(e.versionRange, version.version))
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
