package ru.elytrium.host.api.request.methods;

import dev.morphia.query.experimental.filters.Filters;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.module.Module;
import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.model.module.billing.ModuleBilling;
import ru.elytrium.host.api.model.module.params.ModuleVersion;
import ru.elytrium.host.api.model.user.User;
import ru.elytrium.host.api.request.Response;
import ru.elytrium.host.api.utils.UserUtils;

import java.util.Date;
import java.util.Optional;

@RestController
@ConditionalOnProperty("elytrahost.master")
public class InstanceMethods {

    @RequestMapping("/instance/listAvailable")
    public static Response listAvailable() {
        return Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(ElytraHostAPI.getModules().getAllItems()));
    }

    @RequestMapping("/instance/create")
    public static Response create(@RequestParam String name,
                                  @RequestParam String module,
                                  @RequestParam String version,
                                  @RequestParam String billingPeriod,
                                  @RequestParam String tariff,
                                  @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        if (ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("name", name)).count() != 0) {
            return Response.genBadRequestResponse("Incorrect 'name' parameter");
        }

        Module moduleImpl = ElytraHostAPI.getModules().getItem(module);
        Optional<ModuleVersion> versionImpl = moduleImpl.getAvailableVersions().stream().filter(e -> e.getVersion().equals(version)).findFirst();
        Optional<ModuleBilling> billing = moduleImpl.getAvailableBillings().stream().filter(e -> e.getBillingType().name().equals(billingPeriod)).findFirst();
        Optional<String> tariffImpl = moduleImpl.getAvailableTariffs().stream().filter(tariff::equals).findFirst();

        if (versionImpl.isPresent() && billing.isPresent() && tariffImpl.isPresent()) {
            ModuleInstance instance = new ModuleInstance(user, module, versionImpl.get(), billing.get(), tariffImpl.get(), name);
            user.addInstance(instance);
            return Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(instance));
        } else {
            return Response.genBadRequestResponse("Incorrect module description");
        }
    }

    @RequestMapping("/instance/remove/{uuid}")
    public static Response remove(@PathVariable String uuid,
                                  @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            user.removeInstance(instance);
            instance.delete();

            return Response.genSuccessResponse();
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/update/{uuid}")
    public static Response update(@PathVariable String uuid,
                                  @RequestParam ModuleInstance newInstance,
                                  @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).first();
        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            instance.updateMeta(newInstance);
            return Response.genSuccessResponse();
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/info/{uuid}")
    public static Response info(@PathVariable String uuid,
                                @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            return Response.genSuccessResponse(ElytraHostAPI.getGson().toJson(instance));
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/run/{uuid}")
    public static Response run(@PathVariable String uuid,
                               @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ElytraHostAPI.getInstanceManager().runInstance(instance);

            return Response.genSuccessResponse();
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/pause/{uuid}")
    public static Response pause(@PathVariable String uuid,
                                 @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ElytraHostAPI.getInstanceManager().pauseInstance(instance);

            return Response.genSuccessResponse();
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @RequestMapping("/instance/getDownloadLink/{uuid}")
    public static Response getDownloadLink(@PathVariable String uuid,
                                           @RequestParam String filename,
                                           @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            return Response.genSuccessResponse(
                    ElytraHostAPI.getStorageManager()
                            .getDownloadLink("elytrainstance", filename,
                                    new Date(new Date().getTime() + 60 * 60 * 1000L)).toString());
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @RequestMapping("/instance/getUploadLink/{uuid}")
    public static Response getUploadLink(@PathVariable String uuid,
                                         @RequestParam String filename,
                                         @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytraHostAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("uuid", uuid)).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            return Response.genSuccessResponse(
                    ElytraHostAPI.getStorageManager()
                            .getUploadLink("elytrainstance", filename,
                                    new Date(new Date().getTime() + 60 * 60 * 1000L)).toString());
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }
}
