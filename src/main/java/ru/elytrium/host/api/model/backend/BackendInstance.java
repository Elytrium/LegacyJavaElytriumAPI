package ru.elytrium.host.api.model.backend;

import com.google.common.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.request.slave.SlaveRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BackendInstance {
    public abstract String runModuleInstance(ModuleInstance moduleInstance);
    public abstract void pauseModuleInstance(ModuleInstance moduleInstance);
    public abstract List<ModuleInstance> listModuleInstance(ModuleInstance moduleInstance);

    public int sendInstanceRunRequest(String host, ModuleInstance instance) {
        String response = sendInstanceRequest(host, new SlaveRequest(
                ElytraHostAPI.getConfig().getMaster_key(),
                "INSTANCE",
                "RUN",
                ElytraHostAPI.getGson().toJson(instance)
        ));

        return Integer.parseInt(response);
    }

    public void sendInstancePauseRequest(String host, ModuleInstance instance) {
        String response = sendInstanceRequest(host, new SlaveRequest(
                ElytraHostAPI.getConfig().getMaster_key(),
                "INSTANCE",
                "PAUSE",
                ElytraHostAPI.getGson().toJson(instance)
        ));
    }

    public List<ModuleInstance> sendInstanceListRequest(String host) {
        String response = sendInstanceRequest(host, new SlaveRequest(
                ElytraHostAPI.getConfig().getMaster_key(),
                "INSTANCE",
                "LIST_RUNNING_INSTANCES",
                ""
        ));

        return ElytraHostAPI.getGson().fromJson(response, new TypeToken<List<ModuleInstance>>(){}.getType());
    }

    public String sendInstanceRequest(String host, SlaveRequest request) {
        try {
            String data = ElytraHostAPI.getGson().toJson(request);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost("http://" + host + "/api");
            post.setEntity(new StringEntity(data, ContentType.APPLICATION_JSON));
            CloseableHttpResponse httpResponse = httpClient.execute(post);

            return IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
