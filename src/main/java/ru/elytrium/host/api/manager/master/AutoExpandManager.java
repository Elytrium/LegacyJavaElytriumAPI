package ru.elytrium.host.api.manager.master;

import ru.elytrium.host.api.model.module.ModuleInstance;

import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoExpandManager {
    private final HashMap<String, BackendInstance> backendInstances = new HashMap<>();
    private final String masterKey;

    public AutoExpandManager(String masterKey) {
        this.masterKey = masterKey;
    }

    public void addClient(String host, int limitServers) {
        backendInstances.put(host, new BackendInstance(host, masterKey, limitServers));
    }

    public void removeClient(String host) {
        backendInstances.remove(host);
    }

    public BackendInstance pickClient() {
        return backendInstances.values()
                                .stream()
                                .filter(a -> a.getRunningServers() < a.getLimitServers())
                                .sorted(Comparator.comparing(BackendInstance::getRunningServers))
                                .reduce((a, b) -> b)
                                .orElse(null);
    }

    public void runInstance(ModuleInstance instance) {
        BackendInstance backendInstance = pickClient();
        if (backendInstance == null) {
            //TODO: AutoExpand
            return;
        }

    }

    public static class BackendInstance {
        private final String hostname;
        private final AtomicInteger runningServers;
        private final int limitServers;

        private BackendInstance(String hostname, String masterKey, int limitServers) {
            this.hostname = hostname;
            this.limitServers = limitServers;
            this.runningServers = new AtomicInteger(0);
        }

        public Integer getRunningServers() {
            return runningServers.get();
        }

        public Integer incrementRunningServers() {
            return runningServers.incrementAndGet();
        }

        public Integer decrementRunningServers() {
            return runningServers.decrementAndGet();
        }

        public String getHostname() {
            return hostname;
        }

        public int getLimitServers() {
            return limitServers;
        }
    }
}
