package ru.elytrium.host.api.model.backend;

import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.model.module.RunningModuleInstance;
import ru.elytrium.host.api.model.net.YamlNetRequest;

import java.util.HashMap;
import java.util.List;

public class AutoExpandBackendInstance extends BackendInstance {
    private YamlNetRequest createInstanceRequest;
    private YamlNetRequest suspendInstanceRequest;
    private String apiPort;
    private HashMap<String, String> tariff;
    private int limitServers;

    @Override
    public RunningModuleInstance runModuleInstance(ModuleInstance moduleInstance) {
        return null;
    }

    @Override
    public void pauseModuleInstance(ModuleInstance moduleInstance) {

    }

    @Override
    public List<ModuleInstance> listModuleInstance() {
        return null;
    }

    @Override
    public int getLimit() {
        return Integer.MAX_VALUE;
    }
}
