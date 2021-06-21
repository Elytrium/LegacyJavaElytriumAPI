package net.elytrium.api.model.backend;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.module.ModuleInstance;
import net.elytrium.api.model.module.RunningModuleInstance;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class BackendInstance {
    private static final Logger logger = LoggerFactory.getLogger(BackendInstance.class);

    private String hostname;
    private String apiPort;
    private int limitServers;
    private List<String> availableTariffs;

    private final HashMap<UUID, ModuleInstance> runningModuleInstances = new HashMap<>();

    public abstract void onInit();
    public abstract void tryDelete();

    public void onInit(String hostname, String apiPort, int limitServers, List<String> availableTariffs) {
        this.hostname = hostname;
        this.apiPort = apiPort;
        this.limitServers = limitServers;
        this.availableTariffs = availableTariffs;

        logger.info("Loading " + hostname);
    }

    public RunningModuleInstance runModuleInstance(ModuleInstance moduleInstance) {
        if (runningModuleInstances.size() >= limitServers || !availableTariffs.contains(moduleInstance.getTariff())) {
            return null;
        }

        return sendInstanceRunRequest(hostname, apiPort, moduleInstance);
    }

    public void pauseModuleInstance(ModuleInstance moduleInstance) {
        sendInstancePauseRequest(hostname + ":" + apiPort, moduleInstance);
    }

    public List<ModuleInstance> listModuleInstance() {
        return sendInstanceListRequest(hostname + ":" + apiPort);
    }

    public int getLimit() {
        return limitServers;
    }

    public int getRunningServers () {
        return listModuleInstance().size();
    }

    @SuppressWarnings("ConstantConditions")
    public static RunningModuleInstance sendInstanceRunRequest(String hostname, String apiPort, ModuleInstance instance) {
        String apiHost = hostname + ":" + apiPort;
        String response = sendInstanceRequest(apiHost, "run", ImmutableList.of(
                new BasicNameValuePair("instance", ElytriumAPI.getGson().toJson(instance)),
                new BasicNameValuePair("masterKey", ElytriumAPI.getConfig().getMasterKey())));

        String moduleInstanceAddress = hostname + ":" + Integer.parseInt(response);
        return new RunningModuleInstance(apiHost, instance, moduleInstanceAddress);
    }

    public static void sendInstancePauseRequest(String apiHost, ModuleInstance instance) {
        sendInstanceRequest(apiHost, "pause", ImmutableList.of(
                new BasicNameValuePair("instance", ElytriumAPI.getGson().toJson(instance)),
                new BasicNameValuePair("masterKey", ElytriumAPI.getConfig().getMasterKey())));
    }

    public static String sendInstanceLogsRequest(String apiHost, ModuleInstance instance) {
        return sendInstanceRequest(apiHost, "logs", ImmutableList.of(
                        new BasicNameValuePair("instance", ElytriumAPI.getGson().toJson(instance)),
                        new BasicNameValuePair("masterKey", ElytriumAPI.getConfig().getMasterKey())));
    }

    public static String sendInstanceCmdRequest(String apiHost, ModuleInstance instance, String cmd) {
        return sendInstanceRequest(apiHost, "cmd", ImmutableList.of(
                        new BasicNameValuePair("instance", ElytriumAPI.getGson().toJson(instance)),
                        new BasicNameValuePair("cmd", cmd),
                        new BasicNameValuePair("masterKey", ElytriumAPI.getConfig().getMasterKey())));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static List<ModuleInstance> sendInstanceListRequest(String apiHost) {
        String response = sendInstanceRequest(apiHost, "listRunningInstances", ImmutableList.of());

        return ElytriumAPI.getGson().fromJson(response, new TypeToken<List<ModuleInstance>>(){}.getType());
    }

    public static String sendInstanceRequest(String apiHost, String method, List<NameValuePair> request) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost("https://" + apiHost + "/slave/" + method);
            post.setEntity(new UrlEncodedFormEntity(request, StandardCharsets.UTF_8));
            CloseableHttpResponse httpResponse = httpClient.execute(post);

            return IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
