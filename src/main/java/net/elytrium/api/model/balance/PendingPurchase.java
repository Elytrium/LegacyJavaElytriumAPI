package net.elytrium.api.model.balance;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.query.experimental.filters.Filters;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.Exclude;

import java.util.Date;

@Entity("pending_purchases")
public class PendingPurchase {
    @Id
    private String topUpId;

    @Reference
    @Exclude
    private Balance balance;

    private int amount;

    private long invalidationDate;

    private String method;

    public PendingPurchase() {}

    public PendingPurchase(Balance balance, int amount, String topUpId, long invalidationDate, TopUpMethod method) {
        this.balance = balance;
        this.amount = amount;
        this.topUpId = topUpId;
        this.invalidationDate = invalidationDate;
        this.method = method.getName();
        update();
    }

    public String getTopUpId() {
        return topUpId;
    }

    public Balance getBalance() {
        return balance;
    }

    public int getAmount() {
        return amount;
    }

    public String getPayString() {
        return getMethod().getPayString(topUpId);
    }

    public long getInvalidationDate() {
        return invalidationDate;
    }

    public TopUpMethod getMethod() {
        return ElytriumAPI.getTopUpMethods().getItem(method);
    }

    public boolean validate() {
        return getMethod().validatePurchase(this);
    }

    public void proceed() {
        if (getInvalidationDate() <= new Date().getTime()) {
            delete();
        }
        if (validate()) {
            balance.topUp(amount);
            delete();
        }
    }

    public void update() {
        ElytriumAPI.getDatastore().save(this);
    }

    public void delete() {
        ElytriumAPI.getDatastore().find(PendingPurchase.class).filter(Filters.eq("topUpId", topUpId)).delete();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof PendingPurchase) {
            PendingPurchase purchase = (PendingPurchase) object;
            return this.topUpId.equals(purchase.topUpId);
        }
        return false;
    }

}
