package net.elytrium.api.model.module.billing;

public enum ModuleBillingType {
    MONTHLY(30L * 24L * 60L * 60L * 1000L),
    DAILY(24L * 60L * 60L * 1000L),
    HOURLY(60L * 60L * 1000L);

    private final long period;

    public long getPeriod() {
        return period;
    }

    ModuleBillingType(long period) {
        this.period = period;
    }
}
