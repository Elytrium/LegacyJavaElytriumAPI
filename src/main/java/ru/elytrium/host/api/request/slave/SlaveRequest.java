package ru.elytrium.host.api.request.slave;

import com.google.gson.Gson;
import ru.elytrium.host.api.manager.slave.ContainerManager;
import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.model.user.User;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SlaveRequest {
    private String masterKey;
    private String type;
    private String method;
    private String payload;

    public boolean proceedRequest(String masterKey, Consumer<String> reply) {
        if (!masterKey.equals(this.masterKey)) {
            return false;
        }

        try {
            switch (type) {
                case "INSTANCE":
                    INSTANCE.valueOf(method).proceed(payload, reply);
                    return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return false;
    }

    private static class Instance {
        private static final Gson gson = new Gson();
        private static final ContainerManager containerManager = new ContainerManager();

        public static void listRunningInstances(String payload, Consumer<String> send) {
            List<ModuleInstance> instances = containerManager.listRunningInstances();
            send.accept(gson.toJson(instances));
        }

        public static void run(String payload, Consumer<String> send) {
            ModuleInstance instance = gson.fromJson(payload, ModuleInstance.class);
            containerManager.runInstance(instance);
        }

        public static void pause(String payload, Consumer<String> send) {
            ModuleInstance instance = gson.fromJson(payload, ModuleInstance.class);
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
