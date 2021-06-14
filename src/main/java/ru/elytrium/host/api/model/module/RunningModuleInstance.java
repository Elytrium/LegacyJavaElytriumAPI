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

    private String apiHost;

    private String moduleInstanceAddress;

    private Date nextBillingCheckDate;

    @Reference
    private ModuleInstance moduleInstance;

    public RunningModuleInstance() {}

    public RunningModuleInstance(String apiHost, ModuleInstance moduleInstance, String moduleInstanceAddress) {
        this.uuid = UUID.randomUUID();
        this.apiHost = apiHost;
        this.moduleInstance = moduleInstance;
        this.moduleInstanceAddress = moduleInstanceAddress;
        this.nextBillingCheckDate = new Date();
        update();
    }

    public String getApiHost() {
        return apiHost;
    }

    public ModuleInstance getModuleInstance() {
        return moduleInstance;
    }

    public String getModuleInstanceAddress() {
        return moduleInstanceAddress;
    }

    public void pause() {
        BackendInstance.sendInstancePauseRequest(apiHost, moduleInstance);
        delete();
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

    public void delete() {
        ElytraHostAPI.getDatastore().find(RunningModuleInstance.class).filter(Filters.eq("uuid", uuid)).delete();
    }

    public Date getNextBillingCheckDate() {
        return nextBillingCheckDate;
    }

    public void setNextBillingCheckDate(Date lastBillingCheckDate) {
        this.nextBillingCheckDate = lastBillingCheckDate;
        update();
    }
}
