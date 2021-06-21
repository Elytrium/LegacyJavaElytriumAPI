package net.elytrium.api.request.methods;

import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.user.LinkedAccount;
import net.elytrium.api.model.user.User;
import net.elytrium.api.request.Response;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty("elytrium.master")
public class InternalMethods {
    @RequestMapping("/internal/linkAccount")
    public static Response linkAccount(@RequestParam String id,
                                       @RequestParam String displayParam,
                                       @RequestParam String masterKey,
                                       @RequestParam String user,
                                       @RequestParam String type) {
        if (!ElytriumAPI.getConfig().getMasterKey().equals(masterKey)) {
            return Response.genUnauthorizedResponse("Wrong masterKey");
        }

        User userImpl = ElytriumAPI.getDatastore()
                .find(User.class)
                .filter(Filters.eq("_id", user))
                .first();

        if (userImpl == null) {
            return Response.genBadRequestResponse("Incorrect 'userImpl' parameter");
        }

        userImpl.addLinkedAccounts(type, new LinkedAccount(displayParam, id));
        return Response.genSuccessResponse();
    }

    @RequestMapping("/internal/botNotify")
    public static Response botNotify(@RequestParam String masterKey,
                                     @RequestParam int increment) {
        if (!ElytriumAPI.getConfig().getMasterKey().equals(masterKey)) {
            return Response.genUnauthorizedResponse("Wrong masterKey");
        }

        ElytriumAPI.getMeta().incrementBotFiltered(increment);

        return Response.genSuccessResponse();
    }

    @RequestMapping("/internal/setBots")
    public static Response setBots(@RequestParam String masterKey,
                                   @RequestParam int botCount) {
        if (!ElytriumAPI.getConfig().getMasterKey().equals(masterKey)) {
            return Response.genUnauthorizedResponse("Wrong masterKey");
        }

        ElytriumAPI.getMeta().setBotFiltered(botCount);

        return Response.genSuccessResponse();
    }
}
