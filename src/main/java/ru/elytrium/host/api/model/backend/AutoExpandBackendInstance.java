package ru.elytrium.host.api.model.backend;

import com.google.common.collect.ImmutableMap;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.net.YamlNetException;

import java.util.Collections;
import java.util.Date;

@Entity("auto_expand_backend_instances")
public class AutoExpandBackendInstance extends BackendInstance {
    @Id
    private String id;

    private String hostname;
    private Date nextCheckDate;
    private String tariff;
    private String instructionName;

    public AutoExpandBackendInstance() {}

    public AutoExpandBackendInstance(String id, String hostname, String tariff, AutoExpandInstruction instruction) {
        this.id = id;
        this.hostname = hostname;
        this.tariff = tariff;
        this.nextCheckDate = new Date(new Date().getTime() + getInstruction().getTtl());
        this.instructionName = instruction.getName();

        update();
    }

    @Override
    public void onInit() {
        onInit(hostname, getInstruction().getApiPort(), getInstruction().getLimitServers(), Collections.singletonList(tariff));
    }

    @Override
    public void tryDelete() {
        if (nextCheckDate.getTime() <= new Date().getTime()) {
            if (getRunningServers() > 0) {
                nextCheckDate = new Date(nextCheckDate.getTime() + getInstruction().getTtl());
                update();
            } else {
                delete();
            }
        }
    }

    public void update() {
        ElytraHostAPI.getDatastore().save(this);
    }

    public void delete() {
        try {
            getInstruction().getSuspendInstanceRequest().doRequest(ImmutableMap.of("{id}", id));
            ElytraHostAPI.getDatastore().find(AutoExpandBackendInstance.class).filter(Filters.eq("id", id)).delete();
        } catch (YamlNetException e) {
            ElytraHostAPI.getLogger().fatal("Error while deleting AutoExpandBackendInstance#" + id + "@" + getInstruction().getName());
            ElytraHostAPI.getLogger().fatal(e);
        }
    }

    public AutoExpandInstruction getInstruction() {
        return ElytraHostAPI.getAutoExpandInstructions().getItem(instructionName);
    }
}
