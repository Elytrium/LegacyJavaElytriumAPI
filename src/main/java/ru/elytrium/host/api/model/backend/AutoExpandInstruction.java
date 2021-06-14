package ru.elytrium.host.api.model.backend;

import com.google.common.collect.ImmutableMap;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.net.YamlNetException;
import ru.elytrium.host.api.model.net.YamlNetRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoExpandInstruction {
    private String name;
    private YamlNetRequest createInstanceRequest;
    private YamlNetRequest checkInstanceRequest;
    private String isRunningRequestValue;
    private long checkRequestInterval;
    private YamlNetRequest suspendInstanceRequest;
    private String apiPort;
    private HashMap<String, String> tariff;
    private int limitServers;
    private int maxAttempts;
    private long ttl;

    public AutoExpandBackendInstance createInstance(String selectedTariff) {
        try {
            List<String> response = createInstanceRequest.doRequest(ImmutableMap.of(
                "{tariff}", tariff.get(selectedTariff)
            ));

            Map<String, String> checkRequestParameters = ImmutableMap.of("{actionId}", response.get(2));
            int loopCounter = 0;
            while (!checkInstanceRequest.doRequest(checkRequestParameters).get(0).equals(isRunningRequestValue)) {
                Thread.sleep(checkRequestInterval);
                if (loopCounter++ > maxAttempts) {
                    ElytraHostAPI.getLogger().fatal("Error while creating BackendInstance");
                    ElytraHostAPI.getLogger().fatal("Not launched within max attempts");
                    return null;
                }
            }

            return new AutoExpandBackendInstance(response.get(0), response.get(1), selectedTariff, this);
        } catch (YamlNetException | InterruptedException e) {
            ElytraHostAPI.getLogger().fatal("Error while creating BackendInstance");
            ElytraHostAPI.getLogger().fatal(e);
        }
        return null;
    }

    public YamlNetRequest getCreateInstanceRequest() {
        return createInstanceRequest;
    }

    public YamlNetRequest getCheckInstanceRequest() {
        return checkInstanceRequest;
    }

    public YamlNetRequest getSuspendInstanceRequest() {
        return suspendInstanceRequest;
    }

    public String getApiPort() {
        return apiPort;
    }

    public HashMap<String, String> getTariff() {
        return tariff;
    }

    public int getLimitServers() {
        return limitServers;
    }

    public long getTtl() {
        return ttl;
    }

    public String getName() {
        return name;
    }
}
