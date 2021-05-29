package ru.elytrium.host.api.model.balance;

import com.google.common.collect.ImmutableMap;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.net.YamlNetException;
import ru.elytrium.host.api.model.net.YamlNetRequest;
import ru.elytrium.host.api.model.user.User;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TopUpMethod {
    public long ttl;
    public YamlNetRequest createRequest;
    public YamlNetRequest checkRequest;
    public String checkRequestSuccessString;
    public YamlNetRequest rejectRequest;

    public PendingPurchase requestTopUp(User user, int amount) {
        try {
            return new PendingPurchase(
                    user,
                    amount,
                    createRequest.doRequest(new HashMap<>()).get(0),
                    new Date(new Date().getTime() + ttl),
                    this);
        } catch (YamlNetException e) {
            ElytraHostAPI.getLogger().fatal("Error while creating TopUpRequest");
            ElytraHostAPI.getLogger().fatal(e);
        }
        return null;
    }

    public boolean validatePurchase(PendingPurchase purchase) {
        try {
            List<String> response = checkRequest.doRequest(
                    ImmutableMap.of(
                    "purchaseId", purchase.topUpId
                )
            );

            return response.get(0).equals(checkRequestSuccessString);
        } catch (YamlNetException e) {
            ElytraHostAPI.getLogger().fatal("Error while creating ValidatePurchaseRequest");
            ElytraHostAPI.getLogger().fatal(e);
        }
        return false;
    }

    public void rejectPurchase(PendingPurchase purchase) {
        try {
            rejectRequest.doRequest(
                ImmutableMap.of(
                        "purchaseId", purchase.topUpId
                )
            );
        } catch (YamlNetException e) {
            ElytraHostAPI.getLogger().fatal("Error while creating RejectPurchaseRequest");
            ElytraHostAPI.getLogger().fatal(e);
        }
    }
}
