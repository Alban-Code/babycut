package io.onelioh.babycut;

import io.onelioh.babycut.config.AppFactory;
import io.onelioh.babycut.core.DefaultProjectContext;
import io.onelioh.babycut.core.ProjectContext;
import io.onelioh.babycut.engine.infra.javacv.JavaCvMediaProber;
import io.onelioh.babycut.engine.prober.MediaProber;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ProjectContext projectContext = new DefaultProjectContext();

        MediaProber mediaProber = new JavaCvMediaProber();

        AppFactory factory = new AppFactory(projectContext, mediaProber);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/app.fxml"));
        loader.setControllerFactory(factory);

        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        stage.setTitle("Babycut");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}