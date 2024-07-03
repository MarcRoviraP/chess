package excepciones;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;

public class TextoIncorrectoException extends Exception{
	
	
	
	public TextoIncorrectoException(String titulo, String descripcion) {
		mostrarError(titulo, descripcion);
	}


	public void mostrarError(String titulo,String descripcion) {

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(titulo);
		alert.setHeaderText(descripcion);

		alert.showAndWait();		
	}

}
