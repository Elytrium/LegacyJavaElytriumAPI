package ru.elytrium.host.api.model.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import ru.elytrium.host.api.ElytraHostAPI;
import ru.elytrium.host.api.model.Exclude;
import ru.elytrium.host.api.model.balance.Balance;
import ru.elytrium.host.api.model.module.ModuleInstance;
import ru.elytrium.host.api.utils.UserUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Entity("users")
public class User {

    @Id
    private UUID uuid;

    private String email;

    @Reference
    private Balance balance;

    @Reference
    private List<ModuleInstance> instances;

    private HashMap<String, LinkedAccount> linkedAccounts;

    @Exclude
    private String hash;

    @Exclude
    private String token;

    public User() {}

    public User(String email) {
        this.uuid = UUID.randomUUID();
        this.email = email;
        this.balance = new Balance();
        this.instances = new ArrayList<>();
        this.linkedAccounts = new HashMap<>();
        this.hash = "";
        this.token = UserUtils.genToken(64);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    public Balance getBalance() {
        return balance;
    }

    public List<ModuleInstance> getInstances() {
        return instances;
    }

    public HashMap<String, LinkedAccount> getLinkedAccounts() {
        return linkedAccounts;
    }

    public void addLinkedAccounts(String linkedAccountType, LinkedAccount linkedAccount) {
        linkedAccounts.remove(linkedAccountType);
        linkedAccounts.put(linkedAccountType, linkedAccount);
        update();
    }

    public void setPassword(String newPassword, String currentPassword) {
        if (verifyHash(currentPassword)) {
            this.hash = genHash(newPassword);
        }
        update();
    }

    public void setPassword(String newPassword) {
        this.hash = genHash(newPassword);
        update();
    }

    public void addInstance(ModuleInstance instance) {
        this.instances.add(instance);
        update();
    }

    public void removeInstance(ModuleInstance instance) {
        this.instances.removeIf(e -> e.getUuid() == instance.getUuid());
        update();
    }

    public String getToken() {
        return token;
    }

    public void sendActivationMail(String email) {
        try {
            Email from = new Email(ElytraHostAPI.getConfig().getMailFrom());
            Email to = new Email(email);
            String subject = ElytraHostAPI.getConfig().getMailRegSubject();
            String body = ElytraHostAPI.getConfig().getMailRegBody()
                    .replace("{uuid}", uuid.toString())
                    .replace("{email}", email);
            Content content = new Content("text/plain", body);

            Mail mail = new Mail(from, subject, to, content);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            ElytraHostAPI.getSendGrid().api(request);
        } catch (IOException e) {
            ElytraHostAPI.getLogger().warn("Error while sending activation mail");
            ElytraHostAPI.getLogger().warn(e);
        }
    }

    public void update() {
        ElytraHostAPI.getDatastore().save(this);
    }

    public String genHash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public boolean verifyHash(String password) {
        if (hash == null || hash.equals("")) {
            return false;
        }

        BCrypt.Result verifier = BCrypt.verifyer().verify(password.toCharArray(), hash);

        if (!verifier.validFormat) {
            ElytraHostAPI.getLogger().warn("User " + uuid + " has invalid hash format");
            return false;
        }

        return verifier.verified;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof User) {
            User user = (User) object;
            return this.uuid == user.uuid;
        }
        return false;
    }
}
