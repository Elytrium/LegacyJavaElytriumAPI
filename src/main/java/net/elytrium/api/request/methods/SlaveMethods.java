package net.elytrium.api.request.methods;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.manager.slave.ContainerManager;
import net.elytrium.api.model.module.ModuleInstance;

import java.util.List;

@RestController
@ConditionalOnProperty("elytrium.slave")
public class SlaveMethods {
    private static final ContainerManager containerManager = new ContainerManager();

    @RequestMapping("/slave/listRunningInstances")
    public static String listRunningInstances() {
        List<ModuleInstance> instances = containerManager.listRunningInstances();
        return ElytriumAPI.getGson().toJson(instances);
    }

    @RequestMapping("/slave/run")
    public static String run(@RequestParam ModuleInstance instance,
                             @RequestParam String masterKey) {
        if (!ElytriumAPI.getConfig().getMasterKey().equals(masterKey)) {
            return "";
        }

        return String.valueOf(containerManager.runInstance(instance));
    }

    @RequestMapping("/slave/logs")
    public static String logs(@RequestParam ModuleInstance instance,
                             @RequestParam String masterKey) {
        if (!ElytriumAPI.getConfig().getMasterKey().equals(masterKey)) {
            return "";
        }

        return String.valueOf(containerManager.getConsoleStrings(instance));
    }

    @RequestMapping("/slave/cmd")
    public static String logs(@RequestParam ModuleInstance instance,
                             @RequestParam String masterKey, @RequestParam String request) {
        if (!ElytriumAPI.getConfig().getMasterKey().equals(masterKey)) {
            return "";
        }

        containerManager.runConsoleCmd(instance, request);
        return "OK";
    }

    @RequestMapping("/slave/pause")
    public static String pause(@RequestParam ModuleInstance instance,
                               @RequestParam String masterKey) {
        if (!ElytriumAPI.getConfig().getMasterKey().equals(masterKey)) {
            return "";
        }

        containerManager.pauseInstance(instance);
        return "OK";
    }
}
