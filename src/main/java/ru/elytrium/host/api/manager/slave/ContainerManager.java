package ru.elytrium.host.api.manager.slave;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.module.ModuleInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContainerManager {
    private final DockerClient dockerClient;
    private final HashMap<UUID, ModuleInstance> runningInstances = new HashMap<>();

    public ContainerManager() {
        DockerClientConfig config = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
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
                ElytraHostAPI.getLogger().error("Docker: status code " + response.getStatusCode());
            }
        }

        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    public int genPort(int portMultiplier) {
        return 30000 + (10 * getRunningServers()) + portMultiplier;
    }

    @SuppressWarnings("deprecation")
    public int runInstance(ModuleInstance instance) {
        int bindPort = genPort(0);

        String containerId = dockerClient
            .createContainerCmd(instance.getContainerId())
            .withPortBindings(
                new PortBinding(
                    Ports.Binding.bindPort(bindPort),
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

        runningInstances.put(instance.getUuid(), instance);

        dockerClient.startContainerCmd(containerId).exec();
        return bindPort;
    }

    public void pauseInstance(ModuleInstance instance) {
        dockerClient.stopContainerCmd(String.valueOf(instance.getUuid())).exec();
        runningInstances.remove(instance.getUuid());

        instance.saveMount();
    }

    public List<ModuleInstance> listRunningInstances() {
        return new ArrayList<>(runningInstances.values());
    }

    private Integer getRunningServers() {
        return runningInstances.size();
    }
}
