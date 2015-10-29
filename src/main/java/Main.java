import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by minas on 10/10/2015.
 */
public class Main extends Application {

    private static final String VERSION = "v0.1.4";

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("main_menu.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle("Graph Maker " + VERSION);
        primaryStage.setMaximized(true);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

        primaryStage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
