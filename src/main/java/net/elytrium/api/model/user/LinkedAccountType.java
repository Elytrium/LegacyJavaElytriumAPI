package net.elytrium.api.model.user;

import com.google.common.collect.ImmutableMap;
import net.elytrium.api.model.Exclude;
import net.elytrium.api.model.net.YamlNetException;
import net.elytrium.api.model.net.YamlNetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LinkedAccountType {
    @Exclude
    private static final Logger logger = LoggerFactory.getLogger(LinkedAccountType.class);

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
            logger.error("Error while verifying LinkedAccount");
            logger.error(e.toString());
        }

        return null;
    }
}
