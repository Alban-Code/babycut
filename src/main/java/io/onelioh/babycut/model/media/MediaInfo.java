package io.onelioh.babycut.model.media;

import java.util.Collections;
import java.util.List;

/**
 * Représente les métadonnées d'un fichier multimédia obtenues via ffprobe.
 * <p>
 * Un MediaInfo contient :
 * - la durée totale du fichier (en secondes)
 * - la liste des pistes vidéo
 * - la liste des pistes audio
 * <p>
 * Il sert de base pour créer un MediaAsset dans le projet et pour afficher
 * les informations des pistes dans l'interface.
 */
public class MediaInfo {
    private List<MediaStream> videoStreams;
    private List<MediaStream> audioStreams;
    private long durationMilliseconds;

    public MediaInfo() {
        this.videoStreams = List.of();
        this.audioStreams = List.of();
        this.durationMilliseconds = 0;
    }

    public MediaInfo(List<MediaStream> videoStreams, List<MediaStream> audioStreams, long durationMilliseconds) {
        this.videoStreams = videoStreams;
        this.audioStreams = audioStreams;
        this.durationMilliseconds = durationMilliseconds;
    }

    public List<MediaStream> getVideoStreams() {
        return Collections.unmodifiableList(videoStreams);
    }

    public List<MediaStream> getAudioStreams() {
        return Collections.unmodifiableList(audioStreams);
    }

    public long getDurationMilliseconds() {
        return this.durationMilliseconds;
    }


    public List<String> getVideoLabels() {
        return videoStreams.stream().map(MediaStream::label).toList();
    }

    public List<String> getAudioLabels() {
        return audioStreams.stream().map(MediaStream::label).toList();
    }

}
