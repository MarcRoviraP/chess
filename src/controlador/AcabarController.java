package controlador;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.DataBase;

public class AcabarController implements Initializable {

	@FXML
	private Label label;

	@FXML
	private Button reiniciar;

	@FXML
	private Button salir;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		reiniciar.setOnMouseClicked((event) -> {

			reiniciar();
		});
		salir.setOnMouseClicked((event) -> {
			mostrarVentana("/vista/Inicio.fxml", "");
			cerrar();
		});
		Platform.runLater(() -> {

			Stage stage = (Stage) reiniciar.getScene().getWindow();
			String id = stage.getTitle();

			asignarGanador(id);
		});

	}

	public void asignarGanador(String id) {

		DataBase db = new DataBase();
		String result = "";
		String query = "SELECT game_result FROM chess.game where game_id = '+" + id + "';";
		ResultSet rs = db.ejecutarConsulta(query);
		int valor = 0;
		try {
			if (rs.next()) {
				result = rs.getString("game_result");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (!result.equals("Empate")) {

			query = "SELECT p.*,u.* FROM chess.play p join chess.users u on u.user_id = p.user_id WHERE p.game_id = '" + id + "'";
			rs = db.ejecutarConsulta(query);
			try {
				while (rs.next()) {

					valor = rs.getString("user_color").equals(result) ?  10 : -10;
					valor = rs.getInt("user_puntuation") + valor < 0 ? 0 : rs.getInt("user_puntuation") + valor;
					query = String.format("Update chess.users set user_puntuation = '%d' where user_id = %d",valor,rs.getInt("user_id"));
					db.ejecutar(query);
					
					if (rs.getString("user_color").equals(result)) {
						label.setText("Felicidades " +rs.getString("nickname") + " has ganado la guerra ");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			
			label.setText("Nadie a ha ganado la guerra");
		}

		db.closeConnection();
	}

	public void mostrarVentana(String rutaFXML, String titulo) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(rutaFXML));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();
			stage.setTitle(titulo);
			stage.setResizable(false);
			stage.setScene(new Scene(root));

			// Spawnear sin bordes
			stage.initStyle(StageStyle.TRANSPARENT);
			// Hacer modal la ventana
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

		cerrar();

		mostrarVentana("/vista/SeleccionColor.fxml", game_id + "");

	}

	@FXML
	public void cerrar() {
		ChessController.cerrar();

		try {

			MenuController.cerrarStatico();
		} catch (NullPointerException e) {
		}
		Stage stage = (Stage) (reiniciar.getParent().getScene().getWindow());
		// Cerrar la ventana
		stage.close();
	}
}
