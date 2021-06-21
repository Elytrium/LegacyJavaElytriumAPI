package net.elytrium.api.model.module.billing;

import dev.morphia.annotations.Entity;

@Entity("module_billings")
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
