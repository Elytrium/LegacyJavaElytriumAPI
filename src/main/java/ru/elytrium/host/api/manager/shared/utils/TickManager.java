package ru.elytrium.host.api.manager.shared.utils;

import ru.elytrium.host.api.ElytraHostAPI;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class TickManager extends TimerTask {
    private final HashMap<String, TickTask> map = new HashMap<>();

    public TickManager(int interval) {
        Timer timer = new Timer();
        timer.schedule(this, interval, interval);
    }

    public void register(String taskName, TickTask tickTask) {
        map.put(taskName, tickTask);
        ElytraHostAPI.getLogger().info("TickManager: Loading TickTask " + taskName);
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