package net.elytrium.api.model.backend;

import com.google.common.collect.ImmutableMap;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.net.YamlNetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;

@Entity("auto_expand_backend_instances")
public class AutoExpandBackendInstance extends BackendInstance {
    private static final Logger logger = LoggerFactory.getLogger(AutoExpandBackendInstance.class);

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
        ElytriumAPI.getDatastore().save(this);
    }

    public void delete() {
        try {
            getInstruction().getSuspendInstanceRequest().doRequest(ImmutableMap.of("{id}", id));
            ElytriumAPI.getDatastore().find(AutoExpandBackendInstance.class).filter(Filters.eq("_id", id)).delete();
        } catch (YamlNetException e) {
            logger.error("Error while deleting AutoExpandBackendInstance#" + id + "@" + getInstruction().getName());
            logger.error(e.toString());
        }
    }

    public AutoExpandInstruction getInstruction() {
        return ElytriumAPI.getAutoExpandInstructions().getItem(instructionName);
    }
}
