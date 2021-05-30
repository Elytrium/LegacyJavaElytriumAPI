package ru.elytrium.host.api.model.user;

import ru.elytrium.host.api.model.balance.Balance;
import ru.elytrium.host.api.model.module.ModuleInstance;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class User {
    private UUID uuid;
    private String username;
    private Balance balance;
    private List<ModuleInstance> modules;
    private HashMap<String, LinkedAccount> linkedAccounts;

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public Balance getBalance() {
        return balance;
    }

    public List<ModuleInstance> getModules() {
        return modules;
    }

    public HashMap<String, LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void addLinkedAccounts(String linkedAccountType, LinkedAccount linkedAccount) {
        linkedAccounts.remove(linkedAccountType);
        linkedAccounts.put(linkedAccountType, linkedAccount);
    }
}
