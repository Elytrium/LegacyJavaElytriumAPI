package ru.elytrium.host.api.request.unauthorized;

import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.user.LinkedAccount;
import ru.elytrium.host.api.model.user.LinkedAccountType;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.Request;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.request.unauthorized.user.LoginRequest;
import ru.elytrium.host.api.request.unauthorized.user.LoginViaLinkedAccountRequest;
import ru.elytrium.host.api.request.unauthorized.user.RegisterRequest;
import ru.elytrium.host.api.request.unauthorized.user.SendRegisterLinkRequest;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UnauthorizedRequest {
    private String type;
    private String method;
    private String payload;

    public UnauthorizedRequest(Request request) {
        type = request.getType();
        method = request.getMethod();
        payload = request.getPayload();
    }

    public void proceedRequest(Consumer<Response> reply) {
        try {
            if (type.equals("USER")) {
                Requests.valueOf(method).proceed(payload, reply);
            }
        } catch (IllegalArgumentException e) {
            reply.accept(
                    Response.genBadRequestResponse("Wrong method (неверный метод)")
            );
        }
        reply.accept(
                Response.genBadRequestResponse("Wrong method (неверный метод)")
        );
    }

    private static class UserMethods {
        public static void login(String payload, Consumer<Response> authorize) {
            LoginRequest request = ElytraHostAPI.getGson().fromJson(payload, LoginRequest.class);
            if (!request.captcha.validate()) {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'captcha' parameter"));
                return;
            }

            User user = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                    Filters.eq("email", request.email)
            )).first();

            if (user == null) {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'user' parameter"));
                return;
            }

            if (user.verifyHash(request.password)) {
                authorize.accept(Response.genSuccessResponse(user.getToken()));
            } else {
                authorize.accept(Response.genUnauthorizedResponse("Wrong password (Неверный пароль)"));
            }
        }

        public static void loginViaLinkedAccount(String payload, Consumer<Response> authorize) {
            LoginViaLinkedAccountRequest request = ElytraHostAPI.getGson().fromJson(payload, LoginViaLinkedAccountRequest.class);

            LinkedAccountType linkedAccountType = ElytraHostAPI.getLinkedAccountTypes().getItem(request.linkedAccountType);
            LinkedAccount linkedAccount = linkedAccountType.toLinkedAccount(request.token);

            User user = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                    Filters.eq(String.format("linkedAccounts.%s.id", request.linkedAccountType), linkedAccount.id)
            )).first();

            if (user != null) {
                authorize.accept(Response.genSuccessResponse(user.getToken()));
            } else {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'user' parameter"));
            }
        }

        public static void sendRegisterLink(String payload, Consumer<Response> authorize) {
            SendRegisterLinkRequest request = ElytraHostAPI.getGson().fromJson(payload, SendRegisterLinkRequest.class);
            if (!request.captcha.validate()) {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'captcha' parameter"));
                return;
            }

            long count = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                    Filters.eq("email", request.email)
            )).count();

            if (count != 0) {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'user' parameter"));
                return;
            }

            User newUser = new User(request.email);
            newUser.update();
            authorize.accept(Response.genSuccessResponse());
        }

        public static void register(String payload, Consumer<Response> authorize) {
            RegisterRequest request = ElytraHostAPI.getGson().fromJson(payload, RegisterRequest.class);
            if (!request.captcha.validate()) {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'captcha' parameter"));
                return;
            }

            User user = ElytraHostAPI.getDatastore().find(User.class).filter(Filters.and(
                    Filters.eq("email", request.email),
                    Filters.eq("uuid", request.UUID)
            )).first();

            if (user == null) {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'user' parameter"));
                return;
            }

            if (!user.getToken().equals("")) {
                authorize.accept(Response.genBadRequestResponse("Incorrect 'user' parameter"));
                return;
            }

            user.setPassword(request.password);
            authorize.accept(Response.genSuccessResponse(user.getToken()));
        }
    }

    public enum Requests {
        LOGIN(UserMethods::login),
        LOGIN_VIA_LINKED_ACCOUNT(UserMethods::loginViaLinkedAccount),
        SEND_REGISTER_LINK(UserMethods::sendRegisterLink),
        REGISTER(UserMethods::register);

        private final BiConsumer<String, Consumer<Response>> method;

        Requests(BiConsumer<String, Consumer<Response>> method) {
            this.method = method;
        }

        public void proceed(String payload, Consumer<Response> authorize) {
            method.accept(payload, authorize);
        }
    }
}
