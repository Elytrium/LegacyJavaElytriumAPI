package ru.elytrahost.api.model.module.params;

import java.util.List;

public class ModuleConfigFile {
    public String fileName;
    public FileMethod method;
    public List<ModuleConfig> configs;

    public enum FileMethod {
        YAML,
        XML,
        TOML,
        PROPERTIES
    }
}
