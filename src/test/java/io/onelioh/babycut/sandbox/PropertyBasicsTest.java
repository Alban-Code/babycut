package io.onelioh.babycut.learning;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

public class PropertyBasicsTest {

    public static void main(String[] args) {
        System.out.println("=== Test des Properties JavaFX ===\n");

        // TODO 1 : Crée une SimpleIntegerProperty
        IntegerProperty counter = new SimpleIntegerProperty(0);
        System.out.println("Valeur initiale : " + counter.get());

        // TODO 2 : Ajoute ton premier listener
        counter.addListener((observable, oldValue, newValue) -> {
            System.out.println("Listener déclenché : " + oldValue + " → " + newValue);
        });

        // TODO 3 : Expérimente avec plusieurs changements
        System.out.println("\n--- Test 1 : Changement simple ---");
        counter.set(5);

        System.out.println("\n--- Test 2 : Même valeur deux fois ---");
        counter.set(10);
        counter.set(10); // Que va-t-il se passer ?

        System.out.println("\n--- Test 3 : Multiple listeners ---");
        ChangeListener<Number> listener2 = (observable, oldValue, newValue) -> {
            System.out.println("Listener 2 déclenché : " + oldValue + " → " + newValue);
        };
        counter.addListener(listener2);
        counter.set(15);

        counter.removeListener(listener2);

        counter.set(67);

        System.out.println("\n=== Fin des tests ===");
    }
}