package ru.elytrium.host.api.request.authorized.instance;

import dev.morphia.query.experimental.filters.Filters;
import org.apache.logging.log4j.util.TriConsumer;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.module.Module;
import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.model.module.billing.ModuleBilling;
import ru.elytrium.host.api.model.module.params.ModuleVersion;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.RequestType;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.request.authorized.instance.model.CreateInstanceModel;
import ru.elytrium.host.api.request.authorized.instance.model.FileInstanceModel;
import ru.elytrium.host.api.request.authorized.instance.model.UpdateInstanceModel;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

public class InstanceMethods extends RequestType {
    @Override
    public boolean proceedRequest(User user, String method, String payload, Consumer<Response> reply) {
        try {
            Requests.valueOf(method).proceed(user, payload, reply);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void listAvailable(User user, String payload, Consumer<Response> send) {
        send.accept(
            Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(ElytraHostAPI.getModules().getAllItems()))
        );
    }

    public static void create(User user, String payload, Consumer<Response> send) {
        CreateInstanceModel request = ElytraHostAPI.getGson().fromJson(payload, CreateInstanceModel.class);

        if (ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("name", request.name)).count() != 0) {
            send.accept(Response.genBadRequestResponse("Incorrect 'name' parameter"));
            return;
        }

        Module module = ElytraHostAPI.getModules().getItem(request.module);
        Optional<ModuleVersion> version = module.getAvailableVersions().stream().filter(e -> e.version.equals(request.version)).findFirst();
        Optional<ModuleBilling> billing = module.getAvailableBillings().stream().filter(e -> e.getBillingType().name().equals(request.billingPeriod)).findFirst();
        Optional<String> tariff = module.getAvailableTariffs().stream().filter(request.tariff::equals).findFirst();

        if (version.isPresent() && billing.isPresent() && tariff.isPresent()) {
            ModuleInstance instance = new ModuleInstance(user, request.module, version.get(), billing.get(), tariff.get(), request.name);
            user.addInstance(instance);
            send.accept(Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(instance)));
        } else {
            send.accept(Response.genBadRequestResponse("Incorrect module description"));
        }
    }

    public static void remove(User user, String payload, Consumer<Response> send) {
        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", payload)).first();

        if (instance == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'uuid' parameter"));
            return;
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            user.removeInstance(instance);
            instance.delete();

            send.accept(Response.genSuccessResponse());
        } else {
            send.accept(Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid()));
        }
    }

    public static void update(User user, String payload, Consumer<Response> send) {
        UpdateInstanceModel request = ElytraHostAPI.getGson().fromJson(payload, UpdateInstanceModel.class);

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", request.uuid)).first();
        if (instance == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'uuid' parameter"));
            return;
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ModuleInstance newInstance = ElytraHostAPI.getGson().fromJson(request.newInstance, ModuleInstance.class);
            instance.updateMeta(newInstance);

            send.accept(Response.genSuccessResponse());
        } else {
            send.accept(Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid()));
        }
    }

    public static void getInfo(User user, String payload, Consumer<Response> send) {
        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", payload)).first();

        if (instance == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'uuid' parameter"));
            return;
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            send.accept(Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(instance)));
        } else {
            send.accept(Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid()));
        }
    }

    public static void run(User user, String payload, Consumer<Response> send) {
        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", payload)).first();

        if (instance == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'uuid' parameter"));
            return;
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ElytraHostAPI.getInstanceManager().runInstance(instance);

            send.accept(Response.genSuccessResponse());
        } else {
            send.accept(Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid()));
        }
    }

    public static void pause(User user, String payload, Consumer<Response> send) {
        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", payload)).first();

        if (instance == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'uuid' parameter"));
            return;
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ElytraHostAPI.getInstanceManager().pauseInstance(instance);

            send.accept(Response.genSuccessResponse());
        } else {
            send.accept(Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid()));
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static void getDownloadLink(User user, String payload, Consumer<Response> send) {
        FileInstanceModel request = ElytraHostAPI.getGson().fromJson(payload, FileInstanceModel.class);

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", request.uuid)).first();

        if (instance == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'uuid' parameter"));
            return;
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            send.accept(
                    Response.genSuccessResponse(
                            ElytraHostAPI.getStorageManager()
                                    .getDownloadLink("elytrainstance", request.filename,
                                            new Date(new Date().getTime() + 60 * 60 * 1000L)).toString()));
        } else {
            send.accept(Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid()));
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static void getUploadLink(User user, String payload, Consumer<Response> send) {
        FileInstanceModel request = ElytraHostAPI.getGson().fromJson(payload, FileInstanceModel.class);

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", request.uuid)).first();

        if (instance == null) {
            send.accept(Response.genBadRequestResponse("Incorrect 'uuid' parameter"));
            return;
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            send.accept(
                    Response.genSuccessResponse(
                            ElytraHostAPI.getStorageManager()
                                    .getUploadLink("elytrainstance", request.filename,
                                            new Date(new Date().getTime() + 60 * 60 * 1000L)).toString()));
        } else {
            send.accept(Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid()));
        }
    }

    public enum Requests {
        LIST_AVAILABLE(InstanceMethods::listAvailable),
        CREATE(InstanceMethods::create),
        REMOVE(InstanceMethods::remove),
        RUN(InstanceMethods::run),
        PAUSE(InstanceMethods::pause),
        GET_INFO(InstanceMethods::getInfo),
        GET_DOWNLOAD_LINK(InstanceMethods::getDownloadLink),
        GET_UPLOAD_LINK(InstanceMethods::getUploadLink),
        UPDATE(InstanceMethods::update);

        private final TriConsumer<User, String, Consumer<Response>> method;

        Requests(TriConsumer<User, String, Consumer<Response>> method) {
            this.method = method;
        }

        public void proceed(User user, String payload, Consumer<Response> send) {
            method.accept(user, payload, send);
        }
    }
}
