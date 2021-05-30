package ru.elytrium.host.api.request.slave;

import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.slave.ContainerManager;
import ru.elytrium.host.api.model.module.ModuleInstance;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SlaveRequest {
    private String masterKey;
    private String type;
    private String method;
    private String payload;

    public SlaveRequest() {}

    public SlaveRequest(String masterKey, String type, String method, String payload) {
        this.masterKey = masterKey;
        this.type = type;
        this.method = method;
        this.payload = payload;
    }

    public boolean proceedRequest(String masterKey, Consumer<String> reply) {
        if (!masterKey.equals(this.masterKey)) {
            return false;
        }

        try {
            if ("INSTANCE".equals(type)) {
                INSTANCE.valueOf(method).proceed(payload, reply);
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    private static class Instance {
        private static final ContainerManager containerManager = new ContainerManager();

        public static void listRunningInstances(String payload, Consumer<String> send) {
            List<ModuleInstance> instances = containerManager.listRunningInstances();
            send.accept(ElytraHostAPI.getGson().toJson(instances));
        }

        public static void run(String payload, Consumer<String> send) {
            ModuleInstance instance = ElytraHostAPI.getGson().fromJson(payload, ModuleInstance.class);
            containerManager.runInstance(instance);
        }

        public static void pause(String payload, Consumer<String> send) {
            ModuleInstance instance = ElytraHostAPI.getGson().fromJson(payload, ModuleInstance.class);
            containerManager.pauseInstance(instance);
        }
    }

    public enum INSTANCE {
        LIST_RUNNING_INSTANCES(Instance::listRunningInstances),
        RUN(Instance::run),
        PAUSE(Instance::pause);

        private final BiConsumer<String, Consumer<String>> method;

        INSTANCE(BiConsumer<String, Consumer<String>> method) {
            this.method = method;
        }

        public void proceed(String payload, Consumer<String> send) {
            method.accept(payload, send);
        }
    }
}
