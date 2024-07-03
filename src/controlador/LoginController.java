
package controlador;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import excepciones.TextoIncorrectoException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import modelo.DataBase;

public class LoginController implements Initializable {

	private static int numLogins = 0;
	private static int ultimoID = 0;
	@FXML
	private TextField username;

	@FXML
	private TextField password;

	@FXML
	private Button loginButton;
	
	@FXML 
	private Label contadorUsers;

	Window window;

	private static int game_id;

	private DataBase db = new DataBase();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		contadorUsers.setText(numLogins + "/2");
		loginButton.setOnMouseClicked((event) -> login());
	}

	@FXML
	private void login() {

		int user_id = 0;
		if (textosValidos()) {

			String query = "select user_id from chess.users where nickname = '" + username.getText()
					+ "' and password = '" + password.getText() + "';";
			ResultSet resultSet = db.ejecutarConsulta(query);

			try {

				if (resultSet.next()) {

					user_id = resultSet.getInt("user_id");

					System.out.println(user_id);
					if (numLogins == 0) {

						query = "INSERT INTO chess.game DEFAULT VALUES RETURNING game_id;";

						ResultSet resultSetGame = db.ejecutarConsulta(query);
						resultSetGame.next();
						game_id = resultSetGame.getInt("game_id");
						System.out.println(game_id);
					}

					if (ultimoID != user_id) {

						query = String.format("INSERT INTO chess.play (user_id,game_id) VALUES (%d,%d);", user_id,
								game_id);

						db.ejecutar(query);
						numLogins++;
						contadorUsers.setText(numLogins + "/2");
						username.clear();
						password.clear();
						ultimoID = user_id;

						if (numLogins == 2) {

							numLogins = 0;
							ultimoID = 0;

							cerrarLogin();
							mostrarSeleccionColor(game_id);

						}
					}
				}else {
					username.requestFocus();
					throw new TextoIncorrectoException("El nombre de usuario no existe", "Porfavor inserta un nombre de usario o contraseña validos");
				}
			} catch (SQLException e) {

				e.printStackTrace();
			} catch (TextoIncorrectoException e) {
				// TODO Auto-generated catch block
			}

		}

	}

	private boolean textosValidos() {

		window = loginButton.getScene().getWindow();

		try {

			if (username.getText().equals("")) {

				username.requestFocus();
				throw new TextoIncorrectoException("Texto en blanco", "El nombre de usuario no puede estar en blanco");
			} else if (username.getText().length() < 5 || username.getText().length() > 25) {
				username.requestFocus();
				throw new TextoIncorrectoException("Caracteres incorrectos",
						"El nombre de usuario tiene que tener entre 5 y 25 caracteres");
			} else if (password.getText().equals("")) {
				password.requestFocus();
				throw new TextoIncorrectoException("Contraseña en blanco", "Porfavor introduzca una contraseña");
			} else if (password.getText().length() < 5 || password.getText().length() > 25) {
				password.requestFocus();
				throw new TextoIncorrectoException("Caracteres incorrectos",
						"La contraseña tiene que tener entre 5 y 25 caracteres");
			}
		} catch (TextoIncorrectoException e) {
		}
		return true;
	}

	public void cerrarLogin() {

		db.closeConnection();
		Stage stage1 = (Stage) (loginButton.getParent().getScene().getWindow());
		// Cerrar la ventana
		stage1.close();
	}

	public void mostrarSeleccionColor(int id) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vista/SeleccionColor.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();

			stage.setTitle(id + "");
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

	public void cargarRegistro() {
		cerrarLogin();

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vista/Register.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();

			stage.setTitle("Registro");
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

}