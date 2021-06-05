package ru.elytrium.host.api.model.backend;

import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.model.module.RunningModuleInstance;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StaticBackendInstance extends BackendInstance {
    private String hostname;
    private String port;
    private int limitServers;

    private final HashMap<UUID, ModuleInstance> runningInstances = new HashMap<>();

    @Override
    public RunningModuleInstance runModuleInstance(ModuleInstance moduleInstance) {
        if (runningInstances.size() >= limitServers) {
            return null;
        }

        String apiHost = hostname + ":" + port;
        String port = hostname + ":" + sendInstanceRunRequest(apiHost, moduleInstance);

        return new RunningModuleInstance(apiHost, moduleInstance, port);
    }

    @Override
    public void pauseModuleInstance(ModuleInstance moduleInstance) {
        sendInstancePauseRequest(hostname + ":" + port, moduleInstance);
    }

    @Override
    public List<ModuleInstance> listModuleInstance() {
        return sendInstanceListRequest(hostname + ":" + port);
    }

    @Override
    public int getLimit() {
        return limitServers;
    }
}
