package net.elytrium.api.model.captcha;

import net.elytrium.api.ElytriumAPI;

public class Captcha {
    public String captchaRequest;
    public String captchaProvider;

    public Captcha() {}

    public Captcha(String json) {
        Captcha captcha = ElytriumAPI.getGson().fromJson(json, Captcha.class);
        fromAnother(captcha);
    }

    private void fromAnother(Captcha captcha) {
        captchaRequest = captcha.captchaRequest;
        captchaProvider = captcha.captchaProvider;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean validate() {
        return ElytriumAPI.getCaptchaBackends()
                            .getItem(captchaProvider)
                            .validate(captchaRequest);
    }
}
