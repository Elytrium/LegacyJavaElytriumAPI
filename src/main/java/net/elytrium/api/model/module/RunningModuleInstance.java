package net.elytrium.api.model.module;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.backend.BackendInstance;

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
        this.uuid = UuidCreator.getTimeOrdered();
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

    public String getLogs() {
        return BackendInstance.sendInstanceLogsRequest(apiHost, moduleInstance);
    }

    public void runCmd(String cmd) {
        BackendInstance.sendInstanceCmdRequest(apiHost, moduleInstance, cmd);
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
        ElytriumAPI.getDatastore().save(this);
    }

    public void delete() {
        ElytriumAPI.getDatastore().find(RunningModuleInstance.class).filter(Filters.eq("_id", uuid)).delete();
    }

    public Date getNextBillingCheckDate() {
        return nextBillingCheckDate;
    }

    public void setNextBillingCheckDate(Date lastBillingCheckDate) {
        this.nextBillingCheckDate = lastBillingCheckDate;
        update();
    }
}
