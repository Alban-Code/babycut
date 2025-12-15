package io.onelioh.babycut.utils;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class JavaFXTestBase {

    /**
     * Flag pour éviter que plusieurs classes qui étendent JavaFXTestBase appellent @BeforeAll chacune
     *
     */
    private static boolean javaFxInitialized = false;

    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
        if (!javaFxInitialized) {
            // Création d'un latch attendant 1 event
            CountDownLatch latch = new CountDownLatch(1);

            Platform.startup(() -> {
                // Code éxécutée une fois que JavaFX est initialisé
                // décrémentation du latch pour notifier que c'est bon
                latch.countDown();
            });

            // Attente du latch pour continuer (donc JavaFX lancé)
            boolean initialized = latch.await(5, TimeUnit.SECONDS);

            if (!initialized) {
                throw new IllegalStateException("Impossible d'initialiser JavaFX Toolkit");
            }

            // marquer l'initialisation de JavaFX
            javaFxInitialized = true;
        }
    }
}
