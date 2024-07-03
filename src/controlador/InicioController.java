package controlador;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utilidades.varios;

public class InicioController implements Initializable {

	@FXML
	private Button btnSalir;

	@FXML
	private Button btnIniciarSesion;

	@FXML
	private Button btnRegistrarse;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		btnIniciarSesion.setOnMouseClicked((event) -> {
			mostrarVentana("/vista/Login.fxml", "Chess");
			cerrar();
			
		});

		btnRegistrarse.setOnMouseClicked((event) -> {
			mostrarVentana("/vista/Register.fxml", "Chess");
			cerrar();
		});

		btnSalir.setOnMouseClicked((event) -> {
			cerrar();
			
		});

	}

	public void mostrarVentana(String rutaFXML, String titulo) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(rutaFXML));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();

			stage.setTitle(titulo);
			stage.setResizable(false);
			stage.setScene(new Scene(root));

//			// Spawnear sense bordes
			stage.initStyle(StageStyle.TRANSPARENT);
//			// Hacer modal la ventana
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.show();

		} catch (Exception e) {

		}
	}
	
	@FXML
	public void cerrar() {
		Stage stage = (Stage) (btnIniciarSesion.getParent().getScene().getWindow());
		// Cerrar la ventana
		stage.close();
	}

}
