package net.elytrium.api.manager.master;

import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.backend.AutoExpandInstruction;
import net.elytrium.api.model.backend.BackendInstance;
import net.elytrium.api.manager.shared.utils.TickManager;
import net.elytrium.api.model.backend.AutoExpandBackendInstance;
import net.elytrium.api.model.module.ModuleInstance;
import net.elytrium.api.model.module.RunningModuleInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstanceManager implements TickManager.TickTask {
    private static final Logger logger = LoggerFactory.getLogger(InstanceManager.class);

    private final List<BackendInstance> backends;

    public InstanceManager() {
        backends = new ArrayList<>();

        backends.addAll(ElytriumAPI.getStaticBackends().getAllItems());
        ElytriumAPI.getDatastore().find(AutoExpandBackendInstance.class).forEach(backends::add);

        backends.forEach(BackendInstance::onInit);

        logger.info("Loaded " + backends.size() + " backends");
    }

    public RunningModuleInstance runInstance(ModuleInstance instance) {
        if (instance.getBilling().getAmount() > instance.getBalance().getAmount()) {
            logger.trace("Insufficient funds for ModuleInstance#" + instance.getUuid());
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
            logger.info("All backend instances are busy, buying new one");

            BackendInstance backend = buyBackend(instance.getTariff());
            runningModuleInstance = backend.runModuleInstance(instance);
        }

        instance.getBalance().withdraw(instance.getBilling().getAmount());
        Date nextCheckDate = new Date(new Date().getTime() + instance.getBilling().getBillingType().getPeriod());
        runningModuleInstance.setNextBillingCheckDate(nextCheckDate);

        return runningModuleInstance;
    }

    public void pauseInstance(ModuleInstance instance) {
        RunningModuleInstance runningModuleInstance = ElytriumAPI.getDatastore()
                .find(RunningModuleInstance.class)
                .filter(Filters.eq("moduleInstance.uuid", instance.getUuid()))
                .first();

        if (runningModuleInstance != null) {
            runningModuleInstance.pause();
        }
    }

    public String getLogs(ModuleInstance instance) {
        RunningModuleInstance runningModuleInstance = ElytriumAPI.getDatastore()
                .find(RunningModuleInstance.class)
                .filter(Filters.eq("moduleInstance.uuid", instance.getUuid()))
                .first();

        if (runningModuleInstance != null) {
            return runningModuleInstance.getLogs();
        } else {
            return "";
        }
    }

    public void runCmd(ModuleInstance instance, String cmd) {
        RunningModuleInstance runningModuleInstance = ElytriumAPI.getDatastore()
                .find(RunningModuleInstance.class)
                .filter(Filters.eq("moduleInstance.uuid", instance.getUuid()))
                .first();

        if (runningModuleInstance != null) {
            runningModuleInstance.runCmd(cmd);
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

        ElytriumAPI.getDatastore().find(RunningModuleInstance.class).forEach(runningModuleInstance -> {
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
        ElytriumAPI.getDatastore().find(AutoExpandBackendInstance.class).forEach(AutoExpandBackendInstance::tryDelete);
    }

    private BackendInstance buyBackend(String tariff) {
        AutoExpandInstruction instruction = ElytriumAPI.getAutoExpandInstructions().getRandomItem();
        BackendInstance newInstance = instruction.createInstance(tariff);
        backends.add(newInstance);
        return newInstance;
    }

}
