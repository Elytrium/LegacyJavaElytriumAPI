package ru.elytrium.host.api.request.unauthorized.user;

import ru.elytrium.host.api.model.captcha.Captcha;

public class RegisterRequest {
    public String UUID;
    public String email;
    public String password;
    public Captcha captcha;
}
