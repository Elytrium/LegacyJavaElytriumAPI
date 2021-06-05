package ru.elytrium.host.api.model.module;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.query.experimental.filters.Filters;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.shared.StorageManager;
import ru.elytrium.host.api.model.balance.Balance;
import ru.elytrium.host.api.model.module.billing.ModuleBilling;
import ru.elytrium.host.api.model.module.params.ModuleConfigFile;
import ru.elytrium.host.api.model.module.params.ModuleMount;
import ru.elytrium.host.api.model.module.params.ModulePlugin;
import ru.elytrium.host.api.model.module.params.ModuleVersion;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.utils.VersionUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Entity("module_instances")
public class ModuleInstance {
    @Id
    private UUID uuid;

    @Reference
    private Balance balance;

    private List<UUID> allowedUsers;

    private String moduleName;

    private String name;

    private ModuleVersion version;

    private ModuleBilling billing;

    private List<ModuleMount> mountsOverrides;

    private List<ModuleConfigFile> configsOverrides;

    private List<ModulePlugin> pluginsOverrides;

    public ModuleInstance() {}

    public ModuleInstance(User mainUser, String moduleName, ModuleVersion version, ModuleBilling billing, String name) {
        this.uuid = UUID.randomUUID();
        this.balance = mainUser.getBalance();
        this.allowedUsers = Collections.singletonList(mainUser.getUuid());
        this.moduleName = moduleName;
        this.name = name;
        this.version = version;
        this.billing = billing;
        this.mountsOverrides = new ArrayList<>();
        this.configsOverrides = new ArrayList<>();
        this.pluginsOverrides = new ArrayList<>();
    }

    public String getContainerId() {
        return getModule().getName() + " " + version.version;
    }


    public List<ModuleMount> getMountAndDownload() {
        List<ModuleMount> finalMounts = mountsOverrides.stream().filter(e -> e.enabled).collect(Collectors.toList());

        List<ModuleConfigFile> finalConfigs = new ArrayList<>(configsOverrides);

        pluginsOverrides.stream()
            .filter(e -> e.enabled)
            .map(e -> e.mounts)
            .forEach(e -> e.stream()
                .filter(q -> q.enabled && VersionUtils.checkVersion(q.versionRange, version.version))
                .forEach(finalMounts::add));

        StorageManager storageManager = ElytraHostAPI.getStorageManager();
        finalMounts.forEach(
            q -> {
                try {
                    String folderPath = q.bucketDir.replace("{instance_id}", uuid.toString());
                    String fullPathName = folderPath + ((q.isFile)? q.filename : q.filename + ".zip");
                    InputStream inputFile = storageManager.getFile("elytrainstance", fullPathName);
                    File folder = new File(folderPath);
                    folder.mkdirs();
                    File outputFile = new File(fullPathName);
                    FileUtils.copyInputStreamToFile(inputFile, outputFile);

                    if (!q.isFile) {
                        ZipFile zipFile = new ZipFile(fullPathName);
                        File extractDirectory = new File(folder, q.filename);
                        extractDirectory.mkdirs();
                        zipFile.extractAll("");
                    }
                } catch (IOException e) {
                    ElytraHostAPI.getLogger().fatal("Error while downloading module files");
                    ElytraHostAPI.getLogger().fatal(e);
                }
            }
        );

        finalConfigs.forEach(
            q -> {
                try {
                    String fullPathName = q.folder + q.fileName;
                    InputStream inputFile = storageManager.getFile("elytrastatic", fullPathName);
                    File folder = new File(q.folder);
                    folder.mkdirs();
                    File outputFile = new File(fullPathName);
                    outputFile.mkdirs();
                    FileUtils.copyInputStreamToFile(inputFile, outputFile);
                    q.deserialize(version, outputFile);
                } catch (IOException e) {
                    ElytraHostAPI.getLogger().fatal("Error while downloading module configs");
                    ElytraHostAPI.getLogger().fatal(e);
                }
            }
        );

        return finalMounts;
    }


    public void saveMount() {
        List<ModuleMount> finalMounts = getModule().getMountDefaults().stream().filter(e -> e.enabled).collect(Collectors.toList());
        List<ModuleConfigFile> finalConfigs = new ArrayList<>(getModule().getConfigFiles());

        getModule().getPluginDefaults().stream()
                .filter(e -> e.enabled)
                .map(e -> e.mounts)
                .forEach(e -> e.stream()
                        .filter(q -> q.enabled && VersionUtils.checkVersion(q.versionRange, version.version))
                        .forEach(finalMounts::add));


        StorageManager storageManager = ElytraHostAPI.getStorageManager();
        finalMounts.forEach(
                q -> {
                    try {
                        String folderPath = q.bucketDir.replace("{instance_id}", uuid.toString());

                        if (!q.isFile) {
                            new File(folderPath + q.filename + ".zip").delete();
                            ZipFile zipFile = new ZipFile(folderPath + q.filename + ".zip");
                            File includeDirectory = new File(folderPath, q.filename);
                            zipFile.addFolder(includeDirectory);
                        }

                        File inputFile = new File(folderPath, (q.isFile)? q.filename : q.filename + ".zip");
                        storageManager.saveFile("elytrainstance", folderPath + q.filename, inputFile);
                    } catch (IOException e) {
                        ElytraHostAPI.getLogger().fatal("Error while uploading module files");
                        ElytraHostAPI.getLogger().fatal(e);
                    }
                }
        );

        finalConfigs.forEach(
                q -> {
                    String fullPathName = q.folder + q.fileName;
                    File outputFile = new File(fullPathName);
                    q.serialize(version, outputFile);
                }
        );
    }

    public Module getModule() {
        return ElytraHostAPI.getModules().getItem(moduleName);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ModuleBilling getBilling() {
        return billing;
    }

    public ModuleVersion getVersion() {
        return version;
    }

    public List<ModuleMount> getMountsOverrides() {
        return mountsOverrides;
    }

    public List<ModuleConfigFile> getConfigsOverrides() {
        return configsOverrides;
    }

    public List<ModulePlugin> getPluginsOverrides() {
        return pluginsOverrides;
    }

    public String getName() {
        return name;
    }

    public Balance getBalance() {
        return balance;
    }

    public List<UUID> getAllowedUsers() {
        return allowedUsers;
    }

    public void remove() {
        ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).delete();
    }

    public void updateMeta(ModuleInstance instance) {
        this.name = instance.getName();
        this.version = instance.getVersion();
        this.billing = instance.getBilling();
        this.mountsOverrides = instance.getMountsOverrides();
        this.configsOverrides = instance.getConfigsOverrides();
        this.pluginsOverrides = instance.getPluginsOverrides();

        update();
    }

    public void update() {
        ElytraHostAPI.getDatastore().save(this);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof ModuleInstance) {
            ModuleInstance instance = (ModuleInstance) object;
            return this.uuid == instance.uuid;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
