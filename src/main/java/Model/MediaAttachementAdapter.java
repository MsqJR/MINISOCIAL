package Model;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.JsonObject;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.Jsonb;

public class MediaAttachementAdapter implements JsonbAdapter<MediaAttachement, JsonObject> {

    @Override
    public JsonObject adaptToJson(MediaAttachement media) throws Exception {
        Jsonb jsonb = JsonbBuilder.create();
        JsonObject baseJson = jsonb.fromJson(jsonb.toJson(media), JsonObject.class);

        JsonObjectBuilder builder = Json.createObjectBuilder(baseJson);
        if (media instanceof ImageAttachement) {
            return builder.add("media_type", "IMAGE").build();
        } else if (media instanceof LinkAttachement) {
            return builder.add("media_type", "LINK").build();
        }
        return null;
    }

    @Override
    public MediaAttachement adaptFromJson(JsonObject json) throws Exception {
        if (json == null || !json.containsKey("media_type")) {
            return null;
        }

        String mediaType = json.getString("media_type");
        Jsonb jsonb = JsonbBuilder.create();

        if ("IMAGE".equals(mediaType)) {
            ImageAttachement image = new ImageAttachement();
            if (json.containsKey("image_url")) {
                image.setImage_url(json.getString("image_url"));
            }
            return image;
        } else if ("LINK".equals(mediaType)) {
            LinkAttachement link = new LinkAttachement();
            if (json.containsKey("link")) {
                link.setLink(json.getString("link"));
            }
            return link;
        }
        return null;
    }
    //
}