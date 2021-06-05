package ru.elytrium.host.api.model.module.billing;

public class ModuleBilling {
    private ModuleBillingType billingType;
    private int amount;

    public ModuleBillingType getBillingType() {
        return billingType;
    }

    public int getAmount() {
        return amount;
    }
}
