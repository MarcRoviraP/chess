package utilidades;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class varios {

	public  void mostrarVentana(String rutaFXML, String titulo) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(rutaFXML));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();

			stage.setTitle(titulo);
			stage.setResizable(false);
			stage.setScene(new Scene(root));

			// Spawnear sense bordes
			stage.initStyle(StageStyle.TRANSPARENT);
			// Hacer modal la ventana
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.show();

		} catch (Exception e) {

		}
	}
	
	public static boolean validarEmail(String email) {
		
		final String formatoEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		
		Pattern pattern = Pattern.compile(formatoEmail);
		Matcher matcher = pattern.matcher(email);
		
		return matcher.matches();

	}
}
