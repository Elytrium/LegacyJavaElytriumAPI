package net.elytrium.api.model.module.params;

import dev.morphia.annotations.Entity;

@Entity("module_mount")
public class ModuleMount {
    public String displayName;
    public String containerDir;
    public String bucketDir;
    public String filename;
    public boolean isFile;
    public boolean enabled;
    public boolean permanent;
    public String versionRange;
}
