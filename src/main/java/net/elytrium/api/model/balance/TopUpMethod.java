package net.elytrium.api.model.balance;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.common.collect.ImmutableMap;
import net.elytrium.api.model.Exclude;
import net.elytrium.api.model.net.YamlNetException;
import net.elytrium.api.model.net.YamlNetRequest;
import net.elytrium.api.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TopUpMethod {
    @Exclude
    private static final Logger logger = LoggerFactory.getLogger(TopUpMethod.class);

    private String name;

    private String displayName;

    @Exclude
    private long ttl;

    @Exclude
    private String payString;

    @Exclude
    private YamlNetRequest createRequest;

    @Exclude
    private YamlNetRequest checkRequest;

    @Exclude
    private String checkRequestSuccessString;

    @Exclude
    private YamlNetRequest rejectRequest;

    public PendingPurchase requestTopUp(User user, int amount) {
        try {
            String topUpId = String.valueOf(UuidCreator.getTimeOrdered());
            createRequest.doRequest(
                ImmutableMap.of(
                    "{topUpId}", topUpId,
                    "{amount}", String.valueOf(amount),
                    "{user_name}", user.getEmail(),
                    "{balance_id}", user.getBalance().getUuid().toString(),
                    "{date}", Instant.now().plusMillis(ttl).toString()
                )
            );
            return new PendingPurchase(
                    user.getBalance(),
                    amount,
                    topUpId,
                    new Date().getTime() + ttl,
                    this);
        } catch (YamlNetException e) {
            logger.error("Error while creating TopUpRequest");
            logger.error(e.toString());
        }
        return null;
    }

    public boolean validatePurchase(PendingPurchase purchase) {
        try {
            List<String> response = checkRequest.doRequest(
                    ImmutableMap.of(
                    "{topUpId}", purchase.getTopUpId()
                )
            );

            return response.get(0).equals(checkRequestSuccessString);
        } catch (YamlNetException e) {
            logger.error("Error while creating ValidatePurchaseRequest");
            logger.error(e.toString());
        }
        return false;
    }

    public void rejectPurchase(PendingPurchase purchase) {
        try {
            rejectRequest.doRequest(
                ImmutableMap.of(
                        "{topUpId}", purchase.getTopUpId()
                )
            );
        } catch (YamlNetException e) {
            logger.error("Error while creating RejectPurchaseRequest");
            logger.error(e.toString());
        }
    }

    public String getName() {
        return name;
    }

    public String getPayString(String topUpId) {
        return payString.replace("{topUpId}", topUpId);
    }
}
