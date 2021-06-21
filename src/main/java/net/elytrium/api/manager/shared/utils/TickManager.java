package net.elytrium.api.manager.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TickManager extends TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(TickManager.class);

    private final HashMap<String, TickTask> map = new HashMap<>();

    public TickManager(int interval) {
        Timer timer = new Timer();
        timer.schedule(this, interval, interval);
    }

    public void register(String taskName, TickTask tickTask) {
        map.put(taskName, tickTask);
        logger.info("Loading TickTask " + taskName);
    }

    public void unregister(String task) {
        map.remove(task);
    }

    @Override
    public void run() {
        map.values().forEach(TickTask::onTick);
    }

    public interface TickTask {
        void onTick();
    }
}
