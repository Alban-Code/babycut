package io.onelioh.model;

public class MediaStream {

    private MediaType type;
    private String codec;
    private int index;
    private String lang;
    private String description;

    public MediaStream(MediaType type, String codec, int index, String lang, String description) {
        this.type = type;
        this.codec = codec;
        this.index = index;
        this.lang = lang;
        this.description = description;
    }

    public MediaType getType() { return type; }
    public String getCodec() { return codec; }
    public int getIndex() { return index; }
    public String getLang() { return lang; }
    public String getDescription() { return description; }

    public String label() {
        return "%s #%d (%s)".formatted(type, index, codec);
    }
}
