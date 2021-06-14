package ru.elytrium.host.api.model.backend;

import java.util.List;

public class StaticBackendInstance extends BackendInstance {
    private String hostname;
    private String apiPort;
    private int limitServers;
    private List<String> availableTariffs;

    @Override
    public void onInit() {
        onInit(hostname, apiPort, limitServers, availableTariffs);
    }

    @Override
    public void tryDelete() {

    }
}
