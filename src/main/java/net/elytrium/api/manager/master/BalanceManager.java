package net.elytrium.api.manager.master;

import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.manager.shared.utils.TickManager;
import net.elytrium.api.model.balance.PendingPurchase;

import java.util.Date;

public class BalanceManager implements TickManager.TickTask {
    @Override
    public void onTick() {
        ElytriumAPI.getDatastore().find(PendingPurchase.class).filter(Filters.lt("invalidationDate", new Date().getTime())).delete();
        ElytriumAPI.getDatastore().find(PendingPurchase.class).forEach(PendingPurchase::proceed);
    }
}