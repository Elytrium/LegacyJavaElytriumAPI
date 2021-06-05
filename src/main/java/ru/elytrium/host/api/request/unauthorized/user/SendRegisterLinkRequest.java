package ru.elytrium.host.api.request.unauthorized.user;

import ru.elytrium.host.api.model.captcha.Captcha;

public class SendRegisterLinkRequest {
    public String email;
    public Captcha captcha;
}
