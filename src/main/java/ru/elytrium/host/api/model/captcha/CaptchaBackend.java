package ru.elytrium.host.api.model.captcha;

import com.google.common.collect.ImmutableMap;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.net.YamlNetException;
import ru.elytrium.host.api.model.net.YamlNetRequest;

import java.util.List;

public class CaptchaBackend {
    public YamlNetRequest validateRequest;
    public String successValue;

    public boolean validate(String captchaResponse) {
        try {
            List<String> response = validateRequest.doRequest(ImmutableMap.of("{captchaResponse}", captchaResponse));
            return response.get(0).equals(successValue);
        } catch (YamlNetException e) {
            ElytraHostAPI.getLogger().fatal("Error while validating captcha");
            ElytraHostAPI.getLogger().fatal(e);
            return false;
        }
    }
}
