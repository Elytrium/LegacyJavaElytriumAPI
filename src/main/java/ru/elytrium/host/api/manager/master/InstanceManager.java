package ru.elytrium.host.api.manager.master;

import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.shared.utils.TickManager;
import ru.elytrium.host.api.model.backend.AutoExpandBackendInstance;
import ru.elytrium.host.api.model.backend.AutoExpandInstruction;
import ru.elytrium.host.api.model.backend.BackendInstance;
import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.model.module.RunningModuleInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstanceManager implements TickManager.TickTask {
    private final List<BackendInstance> backends;

    public InstanceManager() {
        backends = new ArrayList<>();

        backends.addAll(ElytraHostAPI.getStaticBackends().getAllItems());
        ElytraHostAPI.getDatastore().find(AutoExpandBackendInstance.class).forEach(backends::add);

        backends.forEach(BackendInstance::onInit);

        ElytraHostAPI.getLogger().info("InstanceManager: Loaded " + backends.size() + " backends");
    }

    public RunningModuleInstance runInstance(ModuleInstance instance) {
        if (instance.getBilling().getAmount() > instance.getBalance().getAmount()) {
            ElytraHostAPI.getLogger().trace("InstanceManager: Insufficient funds for ModuleInstance#" + instance.getUuid());
            return null;
        }

        RunningModuleInstance runningModuleInstance = null;

        for (BackendInstance backend : backends) {
            runningModuleInstance = backend.runModuleInstance(instance);
            if (runningModuleInstance != null) {
                break;
            }
        }

        if (runningModuleInstance == null) {
            ElytraHostAPI.getLogger().info("InstanceManager: All backend instances are busy, buying new one");

            BackendInstance backend = buyBackend(instance.getTariff());
            runningModuleInstance = backend.runModuleInstance(instance);
        }

        instance.getBalance().withdraw(instance.getBilling().getAmount());
        Date nextCheckDate = new Date(new Date().getTime() + instance.getBilling().getBillingType().getPeriod());
        runningModuleInstance.setNextBillingCheckDate(nextCheckDate);

        return runningModuleInstance;
    }

    public void pauseInstance(ModuleInstance instance) {
        RunningModuleInstance runningModuleInstance = ElytraHostAPI.getDatastore()
                .find(RunningModuleInstance.class)
                .filter(Filters.eq("moduleInstance.uuid", instance.getUuid()))
                .first();

        if (runningModuleInstance != null) {
            runningModuleInstance.pause();
        }
    }

    @Override
    public void onTick() {
        doBillingTasks();
        doAutoExpandExpirationTasks();
    }

    private void doBillingTasks() {
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

    private void doAutoExpandExpirationTasks() {
        ElytraHostAPI.getDatastore().find(AutoExpandBackendInstance.class).forEach(AutoExpandBackendInstance::tryDelete);
    }

    private BackendInstance buyBackend(String tariff) {
        AutoExpandInstruction instruction = ElytraHostAPI.getAutoExpandInstructions().getRandomItem();
        BackendInstance newInstance = instruction.createInstance(tariff);
        backends.add(newInstance);
        return newInstance;
    }

}
