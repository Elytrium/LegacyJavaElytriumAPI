package ru.elytrium.host.api.model.balance;

import ru.elytrium.host.api.model.user.User;

import java.util.Date;

public class PendingPurchase {
    User user;
    int amount;
    String topUpId;
    Date invalidationDate;
    TopUpMethod method;

    public PendingPurchase(User user, int amount, String topUpId, Date invalidationDate, TopUpMethod method) {
        this.user = user;
        this.amount = amount;
        this.topUpId = topUpId;
        this.invalidationDate = invalidationDate;
        this.method = method;
    }
}
