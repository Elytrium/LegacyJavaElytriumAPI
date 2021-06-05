package ru.elytrium.host.api.request.authorized.user;

import org.apache.logging.log4j.util.TriConsumer;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.LinkedAccount;
import ru.elytrium.host.api.model.user.LinkedAccountType;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.request.authorized.user.model.ChangePasswordRequest;
import ru.elytrium.host.api.request.authorized.user.model.PushLinkedAccountRequest;

import java.util.function.Consumer;

public class UserMethods extends RequestType {
    @Override
    public boolean proceedRequest(User user, String method, String payload, Consumer<Response> reply) {
        try {
            Requests.valueOf(method).proceed(user, payload, reply);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void getInfo(User user, String payload, Consumer<Response> send) {
        send.accept(
            Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(user))
        );
    }

    public static void pushLinkedAccount(User user, String payload, Consumer<Response> send) {
        PushLinkedAccountRequest request = ElytraHostAPI.getGson().fromJson(payload, PushLinkedAccountRequest.class);
        LinkedAccountType linkedAccountType = ElytraHostAPI.getLinkedAccountTypes().getItem(request.linkedAccountType);
        if (linkedAccountType == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'linkedAccountType' parameter"));
            return;
        }
        LinkedAccount linkedAccount = linkedAccountType.toLinkedAccount(request.token);
        if (linkedAccount == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'linkedAccount' parameter"));
            return;
        }
        user.addLinkedAccounts(request.linkedAccountType, linkedAccount);
        send.accept(Response.genSuccessResponse());
    }

    public static void updatePassword(User user, String payload, Consumer<Response> send) {
        ChangePasswordRequest request = ElytraHostAPI.getGson().fromJson(payload, ChangePasswordRequest.class);
        if (request.currentPassword.length() > 64 || request.newPassword.length() > 64) {
            send.accept(Response.genBadRequestResponse("Incorrect 'password' parameter"));
            return;
        }
        user.setPassword(request.newPassword, request.currentPassword);
        send.accept(Response.genSuccessResponse());
    }

    public enum Requests {
        GET_INFO(UserMethods::getInfo),
        PUSH_LINKED_ACCOUNT(UserMethods::pushLinkedAccount),
        UPDATE_PASSWORD(UserMethods::updatePassword);

        private final TriConsumer<User, String, Consumer<Response>> method;

        Requests(TriConsumer<User, String, Consumer<Response>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<Response> send) {
            method.accept(user, payload, send);
        }
    }
}
