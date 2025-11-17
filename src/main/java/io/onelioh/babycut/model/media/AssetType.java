package io.onelioh.babycut.model.media;

/**
 * Type d'un asset importé dans le projet.
 *
 * VIDEO : fichier vidéo contenant au moins une piste vidéo
 * AUDIO : fichier audio uniquement
 * IMAGE : image fixe (utilisée comme clip dans la timeline)
 */
public enum AssetType {
    VIDEO, AUDIO, IMAGE
}
