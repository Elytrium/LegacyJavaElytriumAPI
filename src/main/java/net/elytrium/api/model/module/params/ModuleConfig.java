package net.elytrium.api.model.module.params;

import dev.morphia.annotations.Entity;

@Entity("module_config")
public class ModuleConfig {
    public String displayName;
    public String versionRange;
    public String name;
    public String value;
}
