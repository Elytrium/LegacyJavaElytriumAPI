package ru.elytrium.host.api.request.unauthorized.user;

import ru.elytrium.host.api.model.captcha.Captcha;

public class LoginViaLinkedAccountRequest {
    public String linkedAccountType;
    public String token;
    public Captcha captcha;
}
