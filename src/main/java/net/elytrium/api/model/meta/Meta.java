package net.elytrium.api.model.meta;

import com.github.f4b6a3.uuid.UuidCreator;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import net.elytrium.api.ElytriumAPI;

import java.util.UUID;

@Entity("meta")
public class Meta {
    @SuppressWarnings("FieldMayBeFinal")
    @Id
    private UUID id = UuidCreator.getTimeOrdered();

    private long botFiltered = 0;

    public long getBotFiltered() {
        return botFiltered;
    }

    public void setBotFiltered(long botFiltered) {
        this.botFiltered = botFiltered;
        update();
    }

    public void incrementBotFiltered(int increment) {
        botFiltered += increment;
        update();
    }

    public void update() {
        ElytriumAPI.getDatastore().save(this);
    }
}
