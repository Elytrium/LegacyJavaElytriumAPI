package net.elytrium.api.request.methods;

import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.captcha.Captcha;
import net.elytrium.api.model.user.LinkedAccount;
import net.elytrium.api.model.user.LinkedAccountType;
import net.elytrium.api.model.user.User;
import net.elytrium.api.request.Response;
import net.elytrium.api.utils.UserUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@ConditionalOnProperty("elytrium.master")
public class AuthMethods {

    @RequestMapping("/auth/login")
    public static Response login(@RequestParam Captcha captcha,
                                 @RequestParam String email,
                                 @RequestParam String password) {
        if (!captcha.validate()) {
            return Response.genBadRequestResponse("Incorrect 'captcha' parameter");
        }

        if (!UserUtils.isValidEmailAddress(email)) {
            return Response.genBadRequestResponse("Incorrect 'email' parameter");
        }

        User user = ElytriumAPI.getDatastore().find(User.class).filter(Filters.and(
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
        LinkedAccountType linkedAccountTypeInst = ElytriumAPI.getLinkedAccountTypes().getItem(linkedAccountType);
        LinkedAccount linkedAccount = linkedAccountTypeInst.toLinkedAccount(token);

        User user = ElytriumAPI.getDatastore().find(User.class).filter(Filters.and(
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

        email = email.toLowerCase(Locale.ROOT).trim();

        if (!UserUtils.isValidEmailAddress(email)) {
            return Response.genBadRequestResponse("Incorrect 'email' parameter");
        }

        long count = ElytriumAPI.getDatastore().find(User.class).filter(Filters.and(
                Filters.and(
                        Filters.eq("email", email),
                        Filters.ne("hash", "")
                )
        )).count();

        if (count != 0) {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }

        User newUser = ElytriumAPI.getDatastore().find(User.class).filter(Filters.and(
                Filters.and(
                        Filters.eq("email", email)
                )
            )).first();

        if (newUser == null) {
            newUser = new User(email);
            newUser.update();
        }

        newUser.sendActivationMail();

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

        if (!UserUtils.isValidEmailAddress(email)) {
            return Response.genBadRequestResponse("Incorrect 'email' parameter");
        }

        User user = ElytriumAPI.getDatastore().find(User.class).filter(Filters.and(
                Filters.eq("email", email),
                Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(UUID))
        )).first();

        if (user == null) {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }

        if (!user.getHash().equals("")) {
            return Response.genBadRequestResponse("Incorrect 'user' parameter");
        }

        user.setPassword(password);
        return Response.genSuccessResponse(user.getToken());
    }

    @RequestMapping("/auth/getLinkedAccountTypes")
    public static Response getLinkedAccountTypes() {
        return Response.genSuccessResponse(
                ElytriumAPI.getGson().toJson(
                        ElytriumAPI.getLinkedAccountTypes().getAllItems()));
    }
}
