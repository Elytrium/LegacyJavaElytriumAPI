package ru.elytrium.host.api.model.user;

import com.google.common.collect.ImmutableMap;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.Exclude;
import ru.elytrium.host.api.model.net.YamlNetException;
import ru.elytrium.host.api.model.net.YamlNetRequest;

import java.util.List;

public class LinkedAccountType {
    public String displayName;
    public String name;
    @Exclude
    public String oauthGenRequest;
    @Exclude
    public YamlNetRequest verifyRequest;

    public LinkedAccount toLinkedAccount(String token) {
        try {
            List<String> response = verifyRequest.doRequest(ImmutableMap.of("{token}", token));
            return new LinkedAccount(response.get(0), response.get(1));
        } catch (YamlNetException e) {
            ElytraHostAPI.getLogger().fatal("Error while verifying LinkedAccount");
            ElytraHostAPI.getLogger().fatal(e);
        }

        return null;
    }
}
