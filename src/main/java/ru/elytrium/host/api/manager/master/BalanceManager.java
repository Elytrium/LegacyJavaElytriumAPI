package ru.elytrium.host.api.manager.master;

import dev.morphia.query.experimental.filters.Filters;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.manager.shared.utils.TickManager;
import ru.elytrium.host.api.model.balance.PendingPurchase;

import java.util.Date;

public class BalanceManager implements TickManager.TickTask {
    @Override
    public void onTick() {
        ElytraHostAPI.getDatastore().find(PendingPurchase.class).filter(Filters.lt("invalidationDate", new Date().getTime())).delete();
        ElytraHostAPI.getDatastore().find(PendingPurchase.class).forEach(PendingPurchase::proceed);
    }
}