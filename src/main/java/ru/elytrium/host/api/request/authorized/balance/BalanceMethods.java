package ru.elytrium.host.api.request.authorized.balance;

import org.apache.logging.log4j.util.TriConsumer;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.balance.PendingPurchase;
import ru.elytrium.host.api.model.balance.TopUpMethod;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.request.authorized.balance.model.GenTopUpLinkRequestModel;

import java.util.function.Consumer;

public class BalanceMethods extends RequestType {

    @Override
    public boolean proceedRequest(User user, String method, String payload, Consumer<Response> reply) {
        try {
            Requests.valueOf(method).proceed(user, payload, reply);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    public static void genTopUpLink(User user, String payload, Consumer<Response> send) {
        GenTopUpLinkRequestModel request = ElytraHostAPI.getGson().fromJson(payload, GenTopUpLinkRequestModel.class);
        if (request.amount < 1) {
            send.accept(
                    Response.genBadRequestResponse("Incorrect 'amount' parameter")
            );
            return;
        }
        TopUpMethod method = ElytraHostAPI.getTopUpMethods().getItem(request.topUpMethod);
        if (method == null) {
            send.accept(
                    Response.genBadRequestResponse("Incorrect 'method' parameter")
            );
            return;
        }
        PendingPurchase pendingPurchase = method.requestTopUp(user, request.amount);
        send.accept(
            Response.genSuccessResponse(method.getPayString(pendingPurchase.getTopUpId()))
        );
    }

    public static void listMethods(User user, String payload, Consumer<Response> send) {
        send.accept(
            Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(ElytraHostAPI.getTopUpMethods().getAllItems()))
        );
    }

    public enum Requests {
        GEN_TOP_UP_LINK(BalanceMethods::genTopUpLink),
        LIST_METHODS(BalanceMethods::listMethods);

        private final TriConsumer<User, String, Consumer<Response>> method;

        Requests(TriConsumer<User, String, Consumer<Response>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<Response> send) {
            method.accept(user, payload, send);
        }
    }
}
