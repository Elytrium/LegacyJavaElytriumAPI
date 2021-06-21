package net.elytrium.api.model.balance;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.Exclude;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity("balances")
public class Balance {
    @Id
    private UUID uuid;

    private int amount;

    @Reference
    @Exclude
    private List<PendingPurchase> pendingPurchases;

    public Balance() {
        this.uuid = UuidCreator.getTimeOrdered();
        this.amount = 0;
        this.pendingPurchases = new ArrayList<>();
        update();
    }

    public int getAmount() {
        return amount;
    }

    public void topUp(int amount) {
        this.amount += amount;
        update();
    }

    public void withdraw(int amount) {
        this.amount -= amount;
        update();
    }

    public void update() {
        ElytriumAPI.getDatastore().save(this);
    }

    public List<PendingPurchase> getPendingPurchases() {
        return pendingPurchases;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Balance) {
            Balance balance = (Balance) object;
            return this.uuid == balance.uuid;
        }
        return false;
    }
}
