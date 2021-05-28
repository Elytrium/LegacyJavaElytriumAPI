package ru.elytrium.host.api.model.balance;

import ru.elytrium.host.api.model.user.User;

import java.util.Date;

public class YamlTopUpMethod extends TopUpMethod {
    public long ttl;
    public YamlTopUpRequest createRequest;
    public YamlTopUpRequest checkRequest;
    public YamlTopUpRequest rejectRequest;

    @Override
    public PendingPurchase requestTopUp(User user, int amount) throws TopUpException {
        return new PendingPurchase(
                user,
                amount,
                createRequest.doRequest().get(0),
                new Date(new Date().getTime() + ttl),
                this);
    }

    @Override
    public boolean validatePurchase(PendingPurchase purchase) {
        return false;
    }

    @Override
    public void rejectPurchase(PendingPurchase purchase) {

    }
}
