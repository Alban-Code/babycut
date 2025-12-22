package io.onelioh.babycut.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Gestionnaire de commandes pour implémenter Undo/Redo.
 * Maintient deux piles : une pour les commandes annulables (undo), une pour les commandes refaisables (redo).
 */
public class CommandManager {

    private Deque<Command> undoStack = new ArrayDeque<>();
    private Deque<Command> redoStack = new ArrayDeque<>();

    /**
     * Exécute une commande et l'ajoute à l'historique.
     * Vide la pile de redo (un nouvel embranchement efface le futur potentiel).
     */
    public void executeCommand(Command cmd) {
        undoStack.push(cmd);
        cmd.execute();
        redoStack.clear();
    }

    /**
     * Annule la dernière commande exécutée.
     * La commande est déplacée dans la pile de redo.
     */
    public void undo() {
        if (undoStack.isEmpty()) return;

        Command cmd = undoStack.pop();
        cmd.undo();
        redoStack.push(cmd);
    }

    /**
     * Refait la dernière commande annulée.
     * La commande est déplacée dans la pile d'undo.
     */
    public void redo() {
        if (redoStack.isEmpty()) return;

        Command cmd = redoStack.pop();
        cmd.execute();
        undoStack.push(cmd);
    }

    /**
     * @return true si au moins une commande peut être annulée
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * @return true si au moins une commande peut être refaite
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
