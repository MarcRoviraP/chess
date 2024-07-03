package controlador;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.DataBase;

public class MenuController implements Initializable {

	@FXML
	private Button btnAcabar;

	@FXML
	private Button btnIrMenu;

	@FXML
	private Button btnReiniciar;
	@FXML
	private Button btnCerrar;

	@FXML
	private AnchorPane pane;
	
	private static MenuController menu;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		menu = this;
		
		pane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent keyEvent) {

				if (keyEvent.getCode() == KeyCode.ESCAPE) {
					cerrar();

				}

			}
		});

		btnCerrar.setOnAction((event) -> cerrar());
		btnAcabar.setOnAction((event) -> {
			ChessController.cerrar();
			cerrar();
			mostrarVentana("/vista/Inicio.fxml", "Inicio");
		});
		btnIrMenu.setOnAction((event) -> {
			ChessController.cerrar();
			cerrar();
			mostrarVentana("/vista/Inicio.fxml", "Inicio");
		});
		btnReiniciar.setOnAction((event) -> {
			ChessController.cerrar();
			reiniciar();
			cerrar();
		});

	}

	@FXML
	public void cerrar() {
		Stage stage = (Stage) (btnAcabar.getParent().getScene().getWindow());
		// Cerrar la ventana
		stage.close();
	}
	public static void cerrarStatico() {
		Stage stage = (Stage) (menu.btnAcabar.getParent().getScene().getWindow());
		// Cerrar la ventana
		stage.close();
	}

	public void mostrarVentana(String rutaFXML, String titulo) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(rutaFXML));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();

			stage.setTitle(titulo);
			stage.setResizable(false);
			stage.setScene(new Scene(root));

//				// Spawnear sense bordes
			stage.initStyle(StageStyle.TRANSPARENT);
//				// Hacer modal la ventana
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.show();

		} catch (Exception e) {

		}
	}

	public void reiniciar() {
		DataBase db = new DataBase();

		String query;
		int game_id = 0;
		try {

			query = "INSERT INTO chess.game DEFAULT VALUES RETURNING game_id;";

			ResultSet resultSetGame = db.ejecutarConsulta(query);
			resultSetGame.next();
			game_id = resultSetGame.getInt("game_id");
			query = "Select user_id From chess.play where game_id = " + (game_id - 1) + ";";

			ResultSet rs = db.ejecutarConsulta(query);

			while (rs.next()) {
				int user_id = rs.getInt("user_id");
				query = String.format("INSERT INTO chess.play (user_id,game_id) VALUES (%d,%d);", user_id, game_id);
				db.ejecutar(query);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			db.closeConnection();
		}

		mostrarVentana("/vista/SeleccionColor.fxml", game_id + "");

	}

	
}
