package net.elytrium.api.model.captcha;

import com.google.common.collect.ImmutableMap;
import net.elytrium.api.model.net.YamlNetException;
import net.elytrium.api.model.net.YamlNetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CaptchaBackend {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaBackend.class);

    public YamlNetRequest validateRequest;
    public String successValue;

    public boolean validate(String captchaResponse) {
        try {
            List<String> response = validateRequest.doRequest(ImmutableMap.of("{captchaResponse}", captchaResponse));
            return response.get(0).equals(successValue);
        } catch (YamlNetException e) {
            logger.error("Error while validating captcha");
            logger.error(e.toString());
            return false;
        }
    }
}
