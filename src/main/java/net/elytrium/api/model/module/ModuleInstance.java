package net.elytrium.api.model.module;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.manager.shared.storage.StorageManager;
import net.elytrium.api.model.balance.Balance;
import net.elytrium.api.model.module.billing.ModuleBilling;
import net.elytrium.api.model.module.params.*;
import net.elytrium.api.model.user.User;
import net.elytrium.api.utils.VersionUtils;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Entity("module_instances")
public class ModuleInstance {
    private final static Logger logger = LoggerFactory.getLogger(ModuleInstance.class);

    @Id
    private UUID uuid;

    @Reference
    private Balance balance;

    private List<UUID> allowedUsers;

    private String moduleName;

    private String name;

    @Reference
    private ModuleVersion version;

    @Reference
    private ModuleBilling billing;

    private String tariff;

    @Reference
    private List<ModuleMount> mountsOverrides;

    @Reference
    private List<ModuleConfigFile> configFilesOverrides;

    @Reference
    private List<ModuleConfig> configsOverrides;

    @Reference
    private List<ModulePlugin> pluginsOverrides;

    public ModuleInstance() {}

    public ModuleInstance(String json) {
        ModuleInstance moduleInstance = ElytriumAPI.getGson().fromJson(json, ModuleInstance.class);
        fromAnother(moduleInstance);
    }

    public ModuleInstance(User mainUser, String moduleName, ModuleVersion version, ModuleBilling billing, String tariff, String name) {
        this.uuid = UuidCreator.getTimeOrdered();
        this.balance = mainUser.getBalance();
        this.allowedUsers = Collections.singletonList(mainUser.getUuid());
        this.moduleName = moduleName;
        this.name = name;
        this.version = version;
        this.billing = billing;
        this.tariff = tariff;
        this.mountsOverrides = new ArrayList<>();
        this.configFilesOverrides = new ArrayList<>();
        this.pluginsOverrides = new ArrayList<>();
        update();
    }

    public void fromAnother(ModuleInstance moduleInstance) {
        this.uuid = moduleInstance.getUuid();
        this.balance = moduleInstance.getBalance();
        this.allowedUsers = moduleInstance.getAllowedUsers();
        this.moduleName = moduleInstance.getModuleName();
        this.name = moduleInstance.getName();
        this.version = moduleInstance.getVersion();
        this.billing = moduleInstance.getBilling();
        this.tariff = moduleInstance.getTariff();
        this.mountsOverrides = moduleInstance.getMountsOverrides();
        this.configFilesOverrides = moduleInstance.getConfigFilesOverrides();
        this.pluginsOverrides = moduleInstance.getPluginsOverrides();
    }

    public String getContainerId() {
        return getModule().getName() + " " + version.getVersion();
    }


    public List<ModuleMount> getMountAndDownload() {
        List<ModuleMount> finalMounts = mountsOverrides.stream().filter(e -> e.enabled).collect(Collectors.toList());

        List<ModuleConfigFile> finalConfigs = new ArrayList<>(configFilesOverrides);

        pluginsOverrides.stream()
            .filter(e -> e.enabled)
            .map(e -> e.mounts)
            .forEach(e -> e.stream()
                .filter(q -> q.enabled && VersionUtils.checkVersion(q.versionRange, version.getVersion()))
                .forEach(finalMounts::add));

        StorageManager storageManager = ElytriumAPI.getStorageManager();
        finalMounts.forEach(
            q -> {
                try {
                    String folderPath = q.bucketDir.replace("{instance_id}", uuid.toString());
                    String fullPathName = folderPath + ((q.isFile)? q.filename : q.filename + ".zip");
                    InputStream inputFile = storageManager.getFile(ElytriumAPI.getConfig().getInstanceBucketDir(),
                            fullPathName);
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
                    logger.error("Error while downloading module files");
                    logger.error(e.toString());
                }
            }
        );

        finalConfigs.forEach(
            q -> {
                try {
                    String fullPathName = q.folder + q.filename;
                    InputStream inputFile = storageManager.getFile(ElytriumAPI.getConfig().getStaticBucketDir(), fullPathName);
                    File folder = new File(q.folder);
                    folder.mkdirs();
                    File outputFile = new File(fullPathName);
                    outputFile.mkdirs();
                    FileUtils.copyInputStreamToFile(inputFile, outputFile);
                    q.deserialize(version, outputFile);
                } catch (IOException e) {
                    logger.error("Error while downloading module configs");
                    logger.error(e.toString());
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
                        .filter(q -> q.enabled && VersionUtils.checkVersion(q.versionRange, version.getVersion()))
                        .forEach(finalMounts::add));


        StorageManager storageManager = ElytriumAPI.getStorageManager();
        finalMounts.forEach(
                q -> {
                    try {
                        String folderPath = q.bucketDir.replace("{module_id}", uuid.toString());

                        if (!q.isFile) {
                            new File(folderPath + q.filename + ".zip").delete();
                            ZipFile zipFile = new ZipFile(folderPath + q.filename + ".zip");
                            File includeDirectory = new File(folderPath, q.filename);
                            zipFile.addFolder(includeDirectory);
                        }

                        File inputFile = new File(folderPath, (q.isFile)? q.filename : q.filename + ".zip");
                        storageManager.saveFile(ElytriumAPI.getConfig().getInstanceBucketDir(), folderPath + q.filename, inputFile);
                    } catch (IOException e) {
                        logger.error("Error while uploading module files");
                        logger.error(e.toString());
                    }
                }
        );

        finalConfigs.forEach(
                q -> {
                    String fullPathName = q.folder + q.filename;
                    File outputFile = new File(fullPathName);
                    q.serialize(version, outputFile);
                }
        );
    }

    public Module getModule() {
        return ElytriumAPI.getModules().getItem(moduleName);
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

    public List<ModuleConfig> getConfigsOverrides() {
        return configsOverrides;
    }

    public List<ModuleConfigFile> getConfigFilesOverrides() {
        return configFilesOverrides;
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

    public void delete() {
        ElytriumAPI.getDatastore()
                .find(User.class)
                .filter(Filters.in("_id", getAllowedUsers()))
                .forEach(u -> u.removeInstance(this));
        ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", uuid)).delete();
        ElytriumAPI.getStorageManager().deleteDir(ElytriumAPI.getConfig().getInstanceBucketDir(), uuid.toString());
    }

    public void updateMeta(ModuleInstance instance) {
        if (instance.getName().length() < 64) {
            this.name = instance.getName();
        }

        this.version = getModule().getAvailableVersions().stream()
                .filter(e -> e.equals(instance.getVersion()))
                .findFirst().orElse(this.version);
        this.billing = getModule().getAvailableBillings().stream()
                .filter(e -> e.equals(instance.getBilling()))
                .findFirst().orElse(this.billing);

        this.mountsOverrides = instance.getMountsOverrides()
                .stream()
                .map(this::proceedMountUpdate)
                .collect(Collectors.toList());

        this.configFilesOverrides = instance.getConfigFilesOverrides()
                .stream()
                .map(this::proceedConfigFileUpdate)
                .collect(Collectors.toList());

        this.configsOverrides = instance.getConfigsOverrides()
                .stream()
                .map(w -> proceedConfigUpdate(w, getModule().getConfigDefaults()))
                .collect(Collectors.toList());

        this.pluginsOverrides = instance.getPluginsOverrides()
                .stream()
                .map(this::proceedPluginUpdate)
                .collect(Collectors.toList());

        update();
    }

    public void update() {
        ElytriumAPI.getDatastore().save(this);
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

    private ModuleConfigFile proceedConfigFileUpdate(ModuleConfigFile q) {
        Optional<ModuleConfigFile> configFileOptional = getModule().getConfigFiles().stream().filter(w -> w.filename.equals(q.filename)).findFirst();
        if (configFileOptional.isPresent()) {
            ModuleConfigFile configFile = configFileOptional.get();
            configFile.configs = configFile.configs.stream()
                .map(w -> proceedConfigUpdate(w, q.configs))
                .collect(Collectors.toList());
            return configFile;
        }
        return null;
    }

    private ModuleConfig proceedConfigUpdate(ModuleConfig w, List<ModuleConfig> configs) {
        Optional<ModuleConfig> configOptional = configs.stream().filter(z -> z.name.equals(w.name)).findFirst();
        if (configOptional.isPresent()) {
            ModuleConfig config = configOptional.get();
            config.value = w.value;
            return config;
        }
        return null;
    }

    private ModuleMount proceedMountUpdate(ModuleMount r) {
        Optional<ModuleMount> mountOptional = getModule().getMountDefaults().stream().filter(q -> q.filename.equals(r.filename)).findFirst();
        if (mountOptional.isPresent()) {
            ModuleMount moduleMount = mountOptional.get();
            if (!r.permanent)
                moduleMount.enabled = r.enabled;
            return moduleMount;
        }
        return null;
    }

    private ModulePlugin proceedPluginUpdate(ModulePlugin e) {
        Optional<ModulePlugin> pluginOptional = getModule().getPluginDefaults().stream().filter(q -> q.displayName.equals(e.displayName)).findFirst();
        if (pluginOptional.isPresent()) {
            ModulePlugin modulePlugin = pluginOptional.get();
            modulePlugin.configFiles = modulePlugin.configFiles.stream()
                    .map(this::proceedConfigFileUpdate)
                    .collect(Collectors.toList());
            modulePlugin.mounts = modulePlugin.mounts.stream()
                    .map(this::proceedMountUpdate)
                    .collect(Collectors.toList());
            modulePlugin.enabled = e.enabled;
            return modulePlugin;
        }
        return null;
    }

    public void addAllowedUser(User user) {
        user.addInstance(this);
        allowedUsers.add(user.getUuid());
        update();
    }

    public void removeAllowedUser(User user) {
        user.removeInstance(this);
        allowedUsers.remove(user.getUuid());
        update();
    }

    public String getTariff() {
        return tariff;
    }
}
