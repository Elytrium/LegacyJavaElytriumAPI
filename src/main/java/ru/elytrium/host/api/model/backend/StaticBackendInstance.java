package ru.elytrium.host.api.model.backend;

import ru.elytrium.host.api.model.module.ModuleInstance;

import java.util.HashMap;
import java.util.UUID;

public class StaticBackendInstance extends BackendInstance {
    private String hostname;
    private String port;
    private int limitServers;

    private final HashMap<UUID, ModuleInstance> runningInstances = new HashMap<>();

    @Override
    public String runModuleInstance(ModuleInstance moduleInstance) {
        if (runningInstances.size() >= limitServers) {
            return null;
        }

        return String.format("%s:%d", hostname, sendInstanceRunRequest(String.format("%s:%s", hostname, port), moduleInstance));
    }

    @Override
    public void pauseModuleInstance(ModuleInstance moduleInstance) {
        sendInstancePauseRequest(String.format("%s:%s", hostname, port), moduleInstance);
    }
}
