package net.elytrium.api.request.methods;

import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.user.LinkedAccount;
import net.elytrium.api.model.user.LinkedAccountType;
import net.elytrium.api.model.user.User;
import net.elytrium.api.request.Response;
import net.elytrium.api.utils.UserUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty("elytrium.master")
public class UserMethods {

    @RequestMapping("/user/info")
    public static Response info(@RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        return Response.genSuccessResponse(ElytriumAPI.getGson().toJson(user));
    }

    @RequestMapping("/user/pushLinkedAccount/{linkedAccountType}/{linkedAccountToken}")
    public static Response pushLinkedAccount(@PathVariable String linkedAccountType,
                                             @PathVariable String linkedAccountToken,
                                             @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        LinkedAccountType linkedAccountTypeImpl = ElytriumAPI.getLinkedAccountTypes().getItem(linkedAccountType);
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
