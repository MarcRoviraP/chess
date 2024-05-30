package controlador;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import modelo.Pieza;
import modelo.Tablero;

public class ChessController implements Initializable{

	@FXML
	private AnchorPane pane;
	
	@FXML private Button casilla00;
	

	private static ChessController chess;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		chess = this;
		Tablero t = new Tablero();
		
		ArrayList<Pieza> piezas = new ArrayList<Pieza>(t.getListaPiezas());
		
		for (Pieza pieza : piezas) {
			
			pane.getChildren().add( pieza );
		}
		

	}
	
	public static Control enviarControl() {
		return chess.casilla00;
	}

}