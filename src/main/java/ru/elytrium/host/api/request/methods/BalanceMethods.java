package ru.elytrium.host.api.request.methods;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.balance.PendingPurchase;
import ru.elytrium.host.api.model.balance.TopUpMethod;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.utils.UserUtils;

@RestController
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

        TopUpMethod method = ElytraHostAPI.getTopUpMethods().getItem(topUpMethod);
        if (method == null) {
            return Response.genBadRequestResponse("Incorrect 'method' parameter");
        }

        PendingPurchase pendingPurchase = method.requestTopUp(user, amount);
        return Response.genSuccessResponse(method.getPayString(pendingPurchase.getTopUpId()));
    }

    @RequestMapping("/balance/listMethods")
    public static Response listMethods() {
        return Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(ElytraHostAPI.getTopUpMethods().getAllItems()));
    }
}
