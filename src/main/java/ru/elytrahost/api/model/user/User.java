package ru.elytrahost.api.model.user;

import ru.elytrahost.api.model.balance.Balance;
import ru.elytrahost.api.model.module.ModuleInstance;

import java.util.List;
import java.util.UUID;

public class User {
    public UUID uuid;
    public String username;
    public Balance balance;
    public List<ModuleInstance> modules;
    public List<LinkedAccount> linkedAccounts;
}
