package net.elytrium.api.request.methods;

import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.module.ModuleInstance;
import net.elytrium.api.model.module.RunningModuleInstance;
import net.elytrium.api.model.user.User;
import net.elytrium.api.request.Response;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty("elytrium.master")
public class MetaMethods {
    @RequestMapping("/meta/bots")
    public static Response bots() {
        return Response.genSuccessResponse(String.valueOf(ElytriumAPI.getMeta().getBotFiltered()));
    }

    @RequestMapping("/meta/runningServers")
    public static Response runningServers() {
        return Response.genSuccessResponse(String.valueOf(ElytriumAPI
                .getDatastore()
                .find(RunningModuleInstance.class)
                .count()));
    }

    @RequestMapping("/meta/createdServers")
    public static Response createdServers() {
        return Response.genSuccessResponse(String.valueOf(ElytriumAPI
                .getDatastore()
                .find(ModuleInstance.class)
                .count()));
    }

    @RequestMapping("/meta/users")
    public static Response users() {
        return Response.genSuccessResponse(String.valueOf(ElytriumAPI
                .getDatastore()
                .find(User.class)
                .count()));
    }
}
