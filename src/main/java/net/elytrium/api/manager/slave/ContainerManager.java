package net.elytrium.api.manager.slave;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.module.ModuleInstance;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ContainerManager {
    private static final Logger logger = LoggerFactory.getLogger(ContainerManager.class);

    private final DockerClient dockerClient;
    private final HashMap<UUID, ModuleInstance> runningInstances = new HashMap<>();
    private final HashMap<UUID, StringBuilder> logs = new HashMap<>();

    public ContainerManager() {
        DockerClientConfig config = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .withRegistryUrl(ElytriumAPI.getConfig().getRegistryUrl())
            .withRegistryUsername(ElytriumAPI.getConfig().getRegistryUser())
            .withRegistryPassword(ElytriumAPI.getConfig().getRegistryPassword())
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
                logger.error("Docker: status code " + response.getStatusCode());
                logger.error("Docker: " + IOUtils.toString(response.getBody(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            logger.error("Error while creating ContainerManager");
            logger.error(e.toString());
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

        AttachContainerResultCallback callback = new AttachContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                logs.get(instance.getUuid()).append(item.toString()).append("\n");
                super.onNext(item);
            }

            @Override
            public void onError(Throwable throwable) {
                logs.get(instance.getUuid()).append(throwable.toString()).append("\n");
                super.onError(throwable);
            }
        };

        runningInstances.put(instance.getUuid(), instance);
        logs.put(instance.getUuid(), new StringBuilder());

        dockerClient.startContainerCmd(containerId).exec();
        dockerClient.attachContainerCmd(String.valueOf(instance.getUuid()))
                .withStdOut(true).withStdErr(true).withFollowStream(true).exec(callback);
        return bindPort;
    }

    public void pauseInstance(ModuleInstance instance) {
        dockerClient.stopContainerCmd(String.valueOf(instance.getUuid())).exec();
        runningInstances.remove(instance.getUuid());

        instance.saveMount();
    }

    public String getConsoleStrings(ModuleInstance instance) {
        StringBuilder builder = logs.get(instance.getUuid());
        String logString = builder.toString();
        logs.remove(instance.getUuid());
        logs.put(instance.getUuid(), new StringBuilder());
        return logString;
    }

    @SuppressWarnings("deprecation")
    public void runConsoleCmd(ModuleInstance instance, String cmd) {
        dockerClient.attachContainerCmd(String.valueOf(instance.getUuid()))
                .withStdIn(new ByteArrayInputStream(cmd.getBytes(StandardCharsets.UTF_8)))
                .exec(new AttachContainerResultCallback());
    }

    public List<ModuleInstance> listRunningInstances() {
        return new ArrayList<>(runningInstances.values());
    }

    private Integer getRunningServers() {
        return runningInstances.size();
    }
}
