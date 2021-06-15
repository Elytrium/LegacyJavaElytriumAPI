package ru.elytrium.host.api.request.methods;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.LinkedAccount;
import ru.elytrium.host.api.model.user.LinkedAccountType;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.utils.UserUtils;

@RestController
@ConditionalOnProperty("elytrahost.master")
public class UserMethods {

    @RequestMapping("/user/info")
    public static Response info(@RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        return Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(user));
    }

    @RequestMapping("/user/pushLinkedAccount/{linkedAccountType}/{linkedAccountToken}")
    public static Response pushLinkedAccount(@PathVariable String linkedAccountType,
                                             @PathVariable String linkedAccountToken,
                                             @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        LinkedAccountType linkedAccountTypeImpl = ElytraHostAPI.getLinkedAccountTypes().getItem(linkedAccountType);
        if (linkedAccountType == null) {
            return Response.genBadRequestResponse("Incorrect 'linkedAccountType' parameter");
        }
        LinkedAccount linkedAccount = linkedAccountTypeImpl.toLinkedAccount(linkedAccountToken);
        if (linkedAccount == null) {
            return Response.genBadRequestResponse("Incorrect 'linkedAccount' parameter");
        }
        user.addLinkedAccounts(linkedAccountType, linkedAccount);
        return Response.genSuccessResponse();
    }

    @RequestMapping("/user/updatePassword")
    public static Response updatePassword(@RequestParam String currentPassword,
                                          @RequestParam String newPassword,
                                          @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        if (currentPassword.length() > 64 || newPassword.length() > 64) {
            return Response.genBadRequestResponse("Incorrect 'password' parameter");
        }

        user.setPassword(newPassword, currentPassword);
        return Response.genSuccessResponse();
    }
}
