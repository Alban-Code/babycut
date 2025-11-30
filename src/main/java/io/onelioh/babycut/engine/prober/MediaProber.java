package io.onelioh.babycut.engine.prober;

import io.onelioh.babycut.model.media.MediaInfo;

import java.io.File;

/**
 * Contrat pour les services capables d'analyser un fichier multimédia
 * et d'en extraire les métadonnées (pistes, durée, etc.).
 */
public interface MediaProber {

    /**
     * Analyse un fichier multimédia.
     * @param file Le fichier à analyser.
     * @return Un objet MediaInfo contenant les métadonnées. Retourne un MediaInfo vide en cas d'erreur.
     */
    MediaInfo analyze(File file);
}
