package ru.elytrium.host.api.manager.master;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.module.ModuleInstance;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ContainerManager {
    private final HashMap<String, BackendInstance> backendInstances = new HashMap<>();
    private final AtomicInteger summaryActiveConnections = new AtomicInteger(0);

    public void addClient(String host, int limitServers) {
        DockerClientConfig config = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .withDockerHost(host)
            .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .build();

        DockerHttpClient.Request request = DockerHttpClient.Request.builder()
                .method(DockerHttpClient.Request.Method.GET)
                .path("/_ping")
                .build();

        try (DockerHttpClient.Response response = httpClient.execute(request)) {
            if (response.getStatusCode() != 200) {
                ElytraHostAPI.getLogger().error("Docker host " + host + " status code " + response.getStatusCode());
            }
        }

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        backendInstances.put(host, new BackendInstance(host, dockerClient, limitServers));
    }

    public void removeClient(String host) {
        try {
            backendInstances.get(host).getDockerClient().close();
            backendInstances.remove(host);
        } catch (IOException ignored) {}
    }

    public BackendInstance pickClient() {
        return backendInstances.values()
                                .stream()
                                .filter(a -> a.getRunningServers() < a.getLimitServers())
                                .sorted(Comparator.comparing(BackendInstance::getRunningServers))
                                .reduce((a, b) -> b)
                                .orElse(null);
    }

    public int genPort(BackendInstance instance, int portMultiplier) {
        return 30000 + (10 * instance.getRunningServers()) + portMultiplier;
    }

    public String runInstance(ModuleInstance instance) {
        BackendInstance backendInstance = pickClient();
        if (backendInstance == null) {
            //TODO: AutoExpand
            return "";
        }

        String containerId = backendInstance.getDockerClient()
            .createContainerCmd(instance.getContainerId())
            .withPortBindings(
                new PortBinding(
                    Ports.Binding.bindPort(genPort(backendInstance, 0)),
                    new ExposedPort(instance.getModule().getBindPort(), InternetProtocol.TCP)
                )
            )
            .withBinds(
                instance.getMountAndDownload().stream()
                    .map(e -> new Bind(e.bucketDir, new Volume(e.containerDir)))
                    .collect(Collectors.toList())
            )
            .withName(String.valueOf(instance.getUuid()))
            .exec().getId();

        backendInstance.incrementRunningServers();

        backendInstance.getDockerClient().startContainerCmd(containerId).exec();
        return backendInstance.getHostname() + ":" + genPort(backendInstance, 0);
    }

    public static class BackendInstance {
        private final String hostname;
        private final DockerClient dockerClient;
        private final AtomicInteger runningServers;
        private final int limitServers;

        private BackendInstance(String hostname, DockerClient dockerClient, int limitServers) {
            this.hostname = hostname;
            this.dockerClient = dockerClient;
            this.limitServers = limitServers;
            this.runningServers = new AtomicInteger(0);
        }

        public DockerClient getDockerClient() {
            return dockerClient;
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
