package ru.elytrium.host.api.model.module;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.backend.BackendInstance;

import java.util.Date;
import java.util.UUID;

@Entity("running_module_instances")
public class RunningModuleInstance {

    private UUID uuid;

    private String backendHost;

    private String port;

    private Date nextBillingCheckDate;

    @Reference
    private ModuleInstance moduleInstance;

    public RunningModuleInstance() {}

    public RunningModuleInstance(String backendHost, ModuleInstance moduleInstance, String port) {
        this.uuid = UUID.randomUUID();
        this.backendHost = backendHost;
        this.moduleInstance = moduleInstance;
        this.port = port;
        this.nextBillingCheckDate = new Date();
    }

    public String getBackendHost() {
        return backendHost;
    }

    public ModuleInstance getModuleInstance() {
        return moduleInstance;
    }

    public String getPort() {
        return port;
    }

    public void pause() {
        BackendInstance.sendInstancePauseRequest(backendHost, moduleInstance);
        ElytraHostAPI.getDatastore().find(RunningModuleInstance.class).filter(Filters.eq("uuid", uuid)).delete();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof RunningModuleInstance) {
            RunningModuleInstance runningModuleInstance = (RunningModuleInstance) object;
            return this.uuid == runningModuleInstance.uuid;
        }
        return false;
    }

    public void update() {
        ElytraHostAPI.getDatastore().save(this);
    }

    public Date getNextBillingCheckDate() {
        return nextBillingCheckDate;
    }

    public void setNextBillingCheckDate(Date lastBillingCheckDate) {
        this.nextBillingCheckDate = lastBillingCheckDate;
        update();
    }
}
