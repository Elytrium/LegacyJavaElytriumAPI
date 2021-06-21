package net.elytrium.api.request.methods;

import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.module.Module;
import net.elytrium.api.utils.UserUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.elytrium.api.model.module.ModuleInstance;
import net.elytrium.api.model.module.billing.ModuleBilling;
import net.elytrium.api.model.module.params.ModuleVersion;
import net.elytrium.api.model.user.User;
import net.elytrium.api.request.Response;

import java.util.Date;
import java.util.Optional;

@RestController
@ConditionalOnProperty("elytrium.master")
public class InstanceMethods {

    @RequestMapping("/instance/listAvailable")
    public static Response listAvailable() {
        return Response.genSuccessResponse(ElytriumAPI.getGson().toJson(ElytriumAPI.getModules().getAllItems()));
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

        if (ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("name", name)).count() != 0) {
            return Response.genBadRequestResponse("Incorrect 'name' parameter");
        }

        Module moduleImpl = ElytriumAPI.getModules().getItem(module);
        Optional<ModuleVersion> versionImpl = moduleImpl.getAvailableVersions().stream().filter(e -> e.getVersion().equals(version)).findFirst();
        Optional<ModuleBilling> billing = moduleImpl.getAvailableBillings().stream().filter(e -> e.getBillingType().name().equals(billingPeriod)).findFirst();
        Optional<String> tariffImpl = moduleImpl.getAvailableTariffs().stream().filter(tariff::equals).findFirst();

        if (versionImpl.isPresent() && billing.isPresent() && tariffImpl.isPresent()) {
            ModuleInstance instance = new ModuleInstance(user, module, versionImpl.get(), billing.get(), tariffImpl.get(), name);
            user.addInstance(instance);
            return Response.genSuccessResponse(ElytriumAPI.getGson().toJson(instance));
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

        ModuleInstance instance = ElytriumAPI.getDatastore()
                .find(ModuleInstance.class)
                .filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid)))
                .first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            instance.delete();

            return Response.genSuccessResponse();
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/addAllowedUser/{uuid}")
    public static Response addAllowedUser(@PathVariable String uuid,
                              @RequestParam String user,
                              @RequestParam String token) {
        User owner = UserUtils.getUser(token);
        if (owner == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();
        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(owner.getUuid())) {
            User toAdd = ElytriumAPI.getDatastore()
                    .find(User.class)
                    .filter(Filters.eq("_id", user))
                    .first();

            if (toAdd == null) {
                return Response.genBadRequestResponse("Incorrect 'user' parameter");
            }

            instance.addAllowedUser(toAdd);
            return Response.genSuccessResponse();
        } else {
            return Response.genForbiddenResponse("User#" + owner.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/removeAllowedUser/{uuid}")
    public static Response removeAllowedUser(@PathVariable String uuid,
                              @RequestParam String user,
                              @RequestParam String token) {
        User owner = UserUtils.getUser(token);
        if (owner == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytriumAPI.getDatastore()
                .find(ModuleInstance.class)
                .filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid)))
                .first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(owner.getUuid())) {
            User toAdd = ElytriumAPI.getDatastore()
                    .find(User.class)
                    .filter(Filters.eq("_id", user))
                    .first();

            if (toAdd == null) {
                return Response.genBadRequestResponse("Incorrect 'user' parameter");
            }

            instance.removeAllowedUser(toAdd);
            return Response.genSuccessResponse();
        } else {
            return Response.genForbiddenResponse("User#" + owner.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/logs/{uuid}")
    public static Response logs(@PathVariable String uuid,
                              @RequestParam String token) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();
        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            return Response.genSuccessResponse(ElytriumAPI.getInstanceManager().getLogs(instance));
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }

    @RequestMapping("/instance/cmd/{uuid}")
    public static Response cmd(@PathVariable String uuid,
                              @RequestParam String token,
                               @RequestParam String cmd) {
        User user = UserUtils.getUser(token);
        if (user == null) {
            return Response.genUnauthorizedResponse("Wrong token");
        }

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();
        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ElytriumAPI.getInstanceManager().runCmd(instance, cmd);
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

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();
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

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            return Response.genSuccessResponse(ElytriumAPI.getGson().toJson(instance));
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

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ElytriumAPI.getInstanceManager().runInstance(instance);

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

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            ElytriumAPI.getInstanceManager().pauseInstance(instance);

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

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            return Response.genSuccessResponse(
                    ElytriumAPI.getStorageManager()
                            .getDownloadLink(ElytriumAPI.getConfig().getInstanceBucketDir(), filename,
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

        ModuleInstance instance = ElytriumAPI.getDatastore().find(ModuleInstance.class).filter(Filters.eq("_id", ElytriumAPI.getUuidCodec().decode(uuid))).first();

        if (instance == null) {
            return Response.genBadRequestResponse("Incorrect 'uuid' parameter");
        }

        if (instance.getAllowedUsers().contains(user.getUuid())) {
            return Response.genSuccessResponse(
                    ElytriumAPI.getStorageManager()
                            .getUploadLink(ElytriumAPI.getConfig().getInstanceBucketDir(), filename,
                                    new Date(new Date().getTime() + 60 * 60 * 1000L)).toString());
        } else {
            return Response.genForbiddenResponse("User#" + user.getUuid() + " hasn't access to ModuleInstance#" + instance.getUuid());
        }
    }
}
