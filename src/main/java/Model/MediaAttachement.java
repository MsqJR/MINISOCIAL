package Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class MediaAttachement
{
    @Id
    @NotNull
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mediaID;
    private String link;
    private String image_url;


}
