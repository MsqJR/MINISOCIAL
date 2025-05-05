package Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "media_type", discriminatorType = DiscriminatorType.STRING)
public abstract class MediaAttachement {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mediaID;

    public long getMediaID() {
        return mediaID;
    }

    public void setMediaID(long mediaID) {
        this.mediaID = mediaID;
    }
}
