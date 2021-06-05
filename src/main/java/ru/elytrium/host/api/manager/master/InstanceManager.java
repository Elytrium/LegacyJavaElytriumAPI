package ru.elytrium.host.api.manager.master;

import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.shared.ConfigManager;
import ru.elytrium.host.api.manager.shared.TickManager;
import ru.elytrium.host.api.model.backend.AutoExpandBackendInstance;
import ru.elytrium.host.api.model.backend.BackendInstance;
import ru.elytrium.host.api.model.backend.StaticBackendInstance;
import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.model.module.RunningModuleInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstanceManager implements TickManager.TickTask {
    private final List<BackendInstance> backends;

    public InstanceManager() {
        backends = new ArrayList<>();

        ConfigManager<AutoExpandBackendInstance> autoExpandBackends = new ConfigManager<>(AutoExpandBackendInstance.class, new File("backend/autoexpand"));
        ConfigManager<StaticBackendInstance> staticBackends = new ConfigManager<>(StaticBackendInstance.class, new File("backend/static"));

        backends.addAll(autoExpandBackends.getAllItems());
        backends.addAll(staticBackends.getAllItems());
    }

    public RunningModuleInstance runInstance(ModuleInstance instance) {
        RunningModuleInstance runningModuleInstance = null;

        for (BackendInstance backend : backends) {
            runningModuleInstance = backend.runModuleInstance(instance);
            if (runningModuleInstance != null) {
                break;
            }
        }

        return runningModuleInstance;
    }

    public void pauseInstance(ModuleInstance instance) {
        RunningModuleInstance runningModuleInstance = ElytraHostAPI.getDatastore()
                .find(RunningModuleInstance.class)
                .filter(Filters.eq("moduleInstance", instance))
                .first();

        if (runningModuleInstance != null) {
            runningModuleInstance.pause();
        }
    }

    @Override
    public void onTick() {
        List<ModuleInstance> moduleInstances = new ArrayList<>();
        backends.forEach(e -> moduleInstances.addAll(e.listModuleInstance()));

        ElytraHostAPI.getDatastore().find(RunningModuleInstance.class).forEach(runningModuleInstance -> {
            ModuleInstance instance = runningModuleInstance.getModuleInstance();

            if (runningModuleInstance.getNextBillingCheckDate().getTime() <= new Date().getTime()) {
                if (!moduleInstances.contains(instance)) {
                    runningModuleInstance.pause();
                } else if (instance.getBalance().getAmount() < instance.getBilling().getAmount()) {
                    runningModuleInstance.pause();
                } else {
                    instance.getBalance().withdraw(instance.getBilling().getAmount());
                    Date nextCheckDate = new Date(new Date().getTime() + instance.getBilling().getBillingType().getPeriod());
                    runningModuleInstance.setNextBillingCheckDate(nextCheckDate);
                }
            }
        });
    }

}
