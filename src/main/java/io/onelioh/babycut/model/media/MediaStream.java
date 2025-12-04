package io.onelioh.babycut.model.media;

/**
 * Représente une piste individuelle d'un fichier multimédia analysé par ffprobe.
 *
 * Une piste peut être de type vidéo ou audio. Elle contient des informations
 * telles que son index, son codec, sa langue éventuelle et sa durée.
 */
public class MediaStream {

    private MediaType type;
    private String codec;
    private int index;
    private String lang;
    private String description;
    private long durationMilliseconds;

    public MediaStream(MediaType type, String codec, int index, String lang, String description, long durationMilliseconds) {
        this.type = type;
        this.codec = codec;
        this.index = index;
        this.lang = lang;
        this.description = description;
        this.durationMilliseconds = durationMilliseconds;
    }

    public MediaType getType() { return type; }
    public String getCodec() { return codec; }
    public int getIndex() { return index; }
    public String getLang() { return lang; }
    public String getDescription() { return description; }
    public long getDurationMilliseconds() { return this.durationMilliseconds; }

    public String label() {
        return "%s #%d (%s)".formatted(type, index, codec);
    }
}
