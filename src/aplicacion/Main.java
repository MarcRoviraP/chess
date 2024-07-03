package aplicacion;

import java.io.InputStream;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Registrar la fuente personalizada
            InputStream fontStream = getClass().getResourceAsStream("/tipografias/Enchanted Land.otf");
            if (fontStream != null) {
                Font.loadFont(fontStream, 12);
            } else {
                System.err.println("No se pudo cargar la fuente.");
            }

            // Cargar la ventana
            String fxml = "/vista/Inicio.fxml";
//            String fxml = "/vista/Login.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            // Cargar la Scene
            Scene scene = new Scene(root);
            // Asignar propiedades al Stage
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setTitle("Chess");
            primaryStage.setResizable(false);
            // Asignar la scene y mostrar
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
