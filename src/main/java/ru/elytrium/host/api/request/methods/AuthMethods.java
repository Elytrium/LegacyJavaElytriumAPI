package ru.elytrium.host.api.request.methods;

import dev.morphia.query.experimental.filters.Filters;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.captcha.Captcha;
import ru.elytrium.host.api.model.user.LinkedAccount;
import ru.elytrium.host.api.model.user.LinkedAccountType;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.Response;

@RestController
public class AuthMethods {

    @RequestMapping("/auth/login")
    public static Response login(@RequestParam Captcha captcha,
                                @RequestParam String email,
                                @RequestParam String password) {
        if (!captcha.validate()) {
            return Response.genBadRequestResponse("Incorrect 'captcha' parameter");
        }

        User user = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                Filters.eq("email", email)
        )).first();

        if (user == null) {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }

        if (user.verifyHash(password)) {
            return Response.genSuccessResponse(user.getToken());
        } else {
            return Response.genUnauthorizedResponse("Wrong password (Неверный пароль)");
        }
    }

    @RequestMapping("/auth/loginViaLinkedAccount")
    public static Response loginViaLinkedAccount(@RequestParam String linkedAccountType,
                                             @RequestParam String token) {
        LinkedAccountType linkedAccountTypeInst = ElytraHostAPI.getLinkedAccountTypes().getItem(linkedAccountType);
        LinkedAccount linkedAccount = linkedAccountTypeInst.toLinkedAccount(token);

        User user = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                Filters.eq(String.format("linkedAccounts.%s.id", linkedAccountType), linkedAccount.id)
        )).first();

        if (user != null) {
            return Response.genSuccessResponse(user.getToken());
        } else {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }
    }

    @RequestMapping("/auth/sendRegisterLink")
    public static Response sendRegisterLink(@RequestParam Captcha captcha,
                                        @RequestParam String email) {
        if (!captcha.validate()) {
            return Response.genBadRequestResponse("Incorrect 'captcha' parameter");
        }

        long count = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                Filters.eq("email", email)
        )).count();

        if (count != 0) {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }

        User newUser = new User(email);
        newUser.update();
        return Response.genSuccessResponse();
    }

    @RequestMapping("/auth/register")
    public static Response register(@RequestParam Captcha captcha,
                                    @RequestParam String email,
                                    @RequestParam String password,
                                    @RequestParam String UUID) {
        if (!captcha.validate()) {
            return Response.genBadRequestResponse("Incorrect 'captcha' parameter");
        }

        User user = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                Filters.eq("email", email),
                Filters.eq("uuid", UUID)
        )).first();

        if (user == null) {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }

        if (!user.getToken().equals("")) {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }

        user.setPassword(password);
        return Response.genSuccessResponse(user.getToken());
    }
}
