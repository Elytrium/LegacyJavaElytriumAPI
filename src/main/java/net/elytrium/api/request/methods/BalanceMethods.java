package net.elytrium.api.request.methods;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.balance.PendingPurchase;
import net.elytrium.api.model.balance.TopUpMethod;
import net.elytrium.api.model.user.User;
import net.elytrium.api.request.Response;
import net.elytrium.api.utils.UserUtils;

@RestController
@ConditionalOnProperty("elytrium.master")
public class BalanceMethods {
    @RequestMapping("/balance/genTopUpLink")
    public static Response genTopUpLink(@RequestParam int amount,
                                        @RequestParam String topUpMethod,
                                        @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        if (amount < 1) {
            return Response.genBadRequestResponse("Incorrect 'amount' parameter");
        }

        TopUpMethod method = ElytriumAPI.getTopUpMethods().getItem(topUpMethod);
        if (method == null) {
            return Response.genBadRequestResponse("Incorrect 'method' parameter");
        }

        PendingPurchase pendingPurchase = method.requestTopUp(user, amount);
        return Response.genSuccessResponse(method.getPayString(pendingPurchase.getTopUpId()));
    }

    @RequestMapping("/balance/listMethods")
    public static Response listMethods() {
        return Response.genSuccessResponse(ElytriumAPI.getGson().toJson(ElytriumAPI.getTopUpMethods().getAllItems()));
    }
}
