package io.onelioh.babycut.model.media;

import io.onelioh.babycut.engine.player.AssetPlayback;

import java.nio.file.Path;

/**
 * Représente un média importé dans le projet.
 * Un MediaAsset correspond à un fichier sur disque: Vidéo, Audio ou Image
 * Il contient un MediaInfo pour les actifs vidéo/audio (ffprobe)
 */
public class MediaAsset {
    private final Path path;
    private final AssetType type;
    private final MediaInfo mediaInfo;

    /**
     * Crée un MediaAsset à partir d'un fichier sur le disque
     *
     * @param path chemin vers le fichier sur le disque
     * @param type type de media (audio, vidéo ou image)
     * @param mediaInfo contient les données vidéo et audio
     */
    public MediaAsset(Path path, AssetType type, MediaInfo mediaInfo) {
        this.path = path;
        this.type = type;
        this.mediaInfo = mediaInfo;
    }

    public Path getPath() {
        return path;
    }

    public AssetType getType() {
        return type;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }
}
