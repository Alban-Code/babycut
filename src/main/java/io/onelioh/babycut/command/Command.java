package io.onelioh.babycut.command;

/**
 * Interface pour le pattern Command.
 * Permet d'encapsuler une action pour pouvoir l'exécuter, l'annuler et la refaire.
 */
public interface Command {

    /**
     * Exécute la commande.
     */
    void execute();

    /**
     * Annule la commande (revient à l'état précédent).
     */
    void undo();
}
