package ru.elytrium.host.api.model.user;

import dev.morphia.annotations.Entity;

@Entity("linked_account")
public class LinkedAccount {
    public String displayParam;
    public String id;

    public LinkedAccount(String displayParam, String id) {
        this.displayParam = displayParam;
        this.id = id;
    }
}
