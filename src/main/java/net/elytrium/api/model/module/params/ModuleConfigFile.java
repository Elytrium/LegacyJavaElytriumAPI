package net.elytrium.api.model.module.params;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import net.elytrium.api.manager.shared.serializer.*;
import net.elytrium.api.model.Exclude;
import net.elytrium.api.utils.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

@Entity("module_config_file")
public class ModuleConfigFile {
    private final static Logger logger = LoggerFactory.getLogger(ModuleConfigFile.class);

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
            logger.error("Error while proceeding deserialization of " + filename);
            logger.error(e.toString());
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
            logger.error("Error while proceeding serialization of " + filename);
            logger.error(e.toString());
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
