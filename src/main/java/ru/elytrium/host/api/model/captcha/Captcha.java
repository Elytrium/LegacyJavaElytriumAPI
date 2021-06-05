package ru.elytrium.host.api.model.captcha;

import ru.elytrium.host.api.ElytraHostAPI;

public class Captcha {
    public String captchaRequest;
    public String captchaProvider;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean validate() {
        return ElytraHostAPI.getCaptchaBackends()
                            .getItem(captchaProvider)
                            .validate(captchaRequest);
    }
}
