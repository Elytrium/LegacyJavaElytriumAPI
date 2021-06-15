package ru.elytrium.host.api.request.methods;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.slave.ContainerManager;
import ru.elytrium.host.api.model.module.ModuleInstance;

import java.util.List;

@RestController
public class SlaveRequest {
    private static final ContainerManager containerManager = new ContainerManager();

    @RequestMapping("/slave/listRunningInstances")
    public static String listRunningInstances() {
        List<ModuleInstance> instances = containerManager.listRunningInstances();
        return ElytraHostAPI.getGson().toJson(instances);
    }

    @RequestMapping("/slave/run")
    public static String run(@RequestParam ModuleInstance instance) {
        return String.valueOf(containerManager.runInstance(instance));
    }

    @RequestMapping("/slave/pause")
    public static String pause(@RequestParam ModuleInstance instance) {
        containerManager.pauseInstance(instance);
        return "OK";
    }
}
