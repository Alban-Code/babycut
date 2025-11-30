package io.onelioh.babycut.config;

import io.onelioh.babycut.core.ProjectContext;
import io.onelioh.babycut.engine.prober.MediaProber;
import io.onelioh.babycut.ui.app.AppController;
import javafx.util.Callback;

public class AppFactory implements Callback<Class<?>, Object> {

    private final ProjectContext projectContext;
    private final MediaProber mediaProber;

    public AppFactory(ProjectContext projectContext, MediaProber mediaProber) {
        this.projectContext = projectContext;
        this.mediaProber = mediaProber;
    }

    @Override
    public Object call(Class<?> param) {
        if (param == AppController.class) {
            return new AppController(this.mediaProber);
        }

        try {
            return param.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de créer le controller: " + param.getName(), e);
        }
    }
}
