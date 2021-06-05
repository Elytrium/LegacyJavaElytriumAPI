package ru.elytrium.host.api.model.balance;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.Exclude;

import java.util.Date;

@Entity("pending_purchases")
public class PendingPurchase {
    @Id
    private String topUpId;

    @Reference
    @Exclude
    private Balance balance;

    private int amount;

    private Date invalidationDate;

    private String method;

    public PendingPurchase() {}

    public PendingPurchase(Balance balance, int amount, String topUpId, Date invalidationDate, TopUpMethod method) {
        this.balance = balance;
        this.amount = amount;
        this.topUpId = topUpId;
        this.invalidationDate = invalidationDate;
        this.method = method.getName();
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

    public Date getInvalidationDate() {
        return invalidationDate;
    }

    public TopUpMethod getMethod() {
        return ElytraHostAPI.getTopUpMethods().getItem(method);
    }

    public boolean validate() {
        return getMethod().validatePurchase(this);
    }

    public void proceed() {
        if (validate()) {
            balance.topUp(amount);
        }
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

    @Override
    public int hashCode() {
        return topUpId.hashCode();
    }
}
