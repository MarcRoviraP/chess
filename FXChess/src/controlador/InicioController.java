package controlador;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utilidades.varios;

public class InicioController implements Initializable{

    @FXML
    private Button btnCreditos;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Button btnRegistrarse;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		mostrarVentana("/vista/Chess.fxml", "Chess");
		btnIniciarSesion.setOnMouseClicked((event) ->  mostrarVentana("/vista/Chess.fxml", "Chess"));

		
	}
	public  void mostrarVentana(String rutaFXML, String titulo) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(rutaFXML));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();

			stage.setTitle(titulo);
			stage.setResizable(false);
			stage.setScene(new Scene(root));

//			// Spawnear sense bordes
//			stage.initStyle(StageStyle.TRANSPARENT);
//			// Hacer modal la ventana
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.show();

		} catch (Exception e) {

		}
	}

}
