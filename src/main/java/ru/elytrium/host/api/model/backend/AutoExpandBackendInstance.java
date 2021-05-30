package ru.elytrium.host.api.model.backend;

import ru.elytrium.host.api.model.net.YamlNetRequest;

public class AutoExpandBackendInstance extends BackendInstance {
    private YamlNetRequest createInstanceRequest;
    private YamlNetRequest suspendInstanceRequest;
    private String wsPort;
    private int limitServers;
}
