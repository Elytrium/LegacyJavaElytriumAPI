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

    @Override
    public boolean equals(Object o) {
        if (o instanceof ModuleBilling) {
            return ((ModuleBilling) o).billingType.equals(billingType);
        }
        return false;
    }
}
