package ru.elytrahost.api.model.balance;

import ru.elytrahost.api.model.user.User;

public abstract class TopUpMethod {
    public abstract PendingPurchase requestTopUp(User user, int amount);
    public abstract boolean validatePurchase(PendingPurchase purchase);
    public abstract void rejectPurchase(PendingPurchase purchase);
}
