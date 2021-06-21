package net.elytrium.api.model.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.github.f4b6a3.uuid.UuidCreator;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import net.elytrium.api.ElytriumAPI;
import net.elytrium.api.model.Exclude;
import net.elytrium.api.model.balance.Balance;
import net.elytrium.api.model.module.ModuleInstance;
import net.elytrium.api.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Entity("users")
public class User {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    @Id
    private UUID uuid;

    private String email;

    @Reference
    private Balance balance;

    @Reference
    private List<ModuleInstance> instances;

    @Reference
    private HashMap<String, LinkedAccount> linkedAccounts;

    @Exclude
    private String hash;

    @Exclude
    private String token;

    public User() {}

    public User(String email) {
        this.uuid = UuidCreator.getTimeOrdered();
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

    public String getHash() {
        return hash;
    }

    public void sendActivationMail() {
        try {
            String subject = ElytriumAPI.getConfig().getMailRegSubject();
            String body = ElytriumAPI.getConfig().getMailRegBody()
                    .replace("{uuid}", uuid.toString())
                    .replace("{email}", email);
            String from = ElytriumAPI.getConfig().getMailFrom();

            MimeMessage message = new MimeMessage(ElytriumAPI.getEmailSession());
            message.setFrom(new InternetAddress(from));
            message.setSubject(subject);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(body,"text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException ae) {
            logger.error("Error while sending verification message");
            logger.error(ae.toString());
        }
    }

    public void update() {
        ElytriumAPI.getDatastore().save(this);
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
            logger.warn("User " + uuid + " has invalid hash format");
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
