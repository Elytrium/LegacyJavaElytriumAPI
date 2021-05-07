package ru.elytrahost.api.model.balance;

import ru.elytrahost.api.model.user.User;

import java.util.Date;

public class YamlTopUpMethod extends TopUpMethod {
    public YamlTopUpRequest createRequest;
    public YamlTopUpRequest checkRequest;
    public YamlTopUpRequest rejectRequest;

    @Override
    public PendingPurchase requestTopUp(User user, int amount) {
        return new PendingPurchase(user, amount, , new Date(new Date().getTime() + 24*60*60*1000), this);
    }

    @Override
    public boolean validatePurchase(PendingPurchase purchase) {
        return false;
    }

    @Override
    public void rejectPurchase(PendingPurchase purchase) {

    }
}
