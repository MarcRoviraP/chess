package controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import org.postgresql.util.PSQLException;

import excepciones.TextoIncorrectoException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import modelo.DataBase;
import utilidades.varios;

public class RegisterController implements Initializable {

	@FXML
	private TextField firstName;

	@FXML
	private TextField email;

	@FXML
	private TextField password;

	@FXML
	private TextField confirmPassword;

	@FXML
	private Button registerButton;

	Window window;
	
	private DataBase db = new DataBase();

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		registerButton.setOnMouseClicked((event) -> register());
	}

	@FXML
	private void register() {
		window = registerButton.getScene().getWindow();
		if (textoValido()) {

			String query = String.format(
					"insert into chess.users (email, password, nickname, last_login, register_date, time_game, user_puntuation) values ('%s', '%s', '%s', now(), now(), now(), %d);",
					email.getText(), password.getText(), firstName.getText(), 0);

			db.ejecutar(query);

			System.out.println("Ejecutado");
			cargarLogin();
		}

	}

	private boolean textoValido() {

		window = registerButton.getScene().getWindow();

		try {

			if (firstName.getText().equals("")) {

				firstName.requestFocus();
				throw new TextoIncorrectoException("Texto en blanco",
						"El campo del nombre no puede estar vacío.");
			} else if (firstName.getText().length() < 5 || firstName.getText().length() > 25) {
				firstName.requestFocus();
				throw new TextoIncorrectoException("Caracteres incorrectos",
						"El campo del nombre no puede tener menos de 5 caracteres ni más de 25 caracteres.");

			} else if (email.getText().equals("")) {
				email.requestFocus();
				throw new TextoIncorrectoException("Texto en blanco",
						"El campo del correo electrónico no puede estar vacío.");
			} else if (email.getText().length() < 5 || email.getText().length() > 45) {
				email.requestFocus();
				throw new TextoIncorrectoException("Caracteres incorrectos",
						"El campo del correo electrónico no puede tener menos de 5 caracteres ni más de 45 caracteres.");

			} else if (!varios.validarEmail(email.getText())) {

				email.requestFocus();
				throw new TextoIncorrectoException("Formato email invalido",
						"Introduzca un email correcto. example@sample.ple");
			} else if (password.getText().equals("")) {
				password.requestFocus();
				throw new TextoIncorrectoException("Texto en blanco",
						"El campo de la contraseña no puede estar vacío.");
			} else if (password.getText().length() < 5 || password.getText().length() > 25) {
				password.requestFocus();
				throw new TextoIncorrectoException("Caracteres incorrectos",
						"El campo de la contraseña no puede tener menos de 5 caracteres ni más de 25 caracteres.");
			} else if (confirmPassword.getText().equals("")) {
				confirmPassword.requestFocus();
				throw new TextoIncorrectoException("Texto en blanco",
						"El campo de confirmar contraseña no puede estar vacío.");
			} else if (confirmPassword.getText().length() < 5 || password.getText().length() > 25) {
				confirmPassword.requestFocus();
				throw new TextoIncorrectoException("Caracteres incorrectos",
						"El campo de confirmar contraseña no puede tener menos de 5 caracteres ni más de 25 caracteres.");
			} else if (!password.getText().equals(confirmPassword.getText())) {
				password.requestFocus();
				throw new TextoIncorrectoException("No coinciden",
						"Los campos de contraseña y confirmar contraseña no coinciden.");
			} else if (estaRegistrado()) {
				firstName.requestFocus();
				throw new TextoIncorrectoException("Ya registrado",
						"El nombre de usuario ya está siendo utilizado por otra persona.");
			} else {
				return true;
			}
		} catch (TextoIncorrectoException e) {
		}
		return false;
	}

	private boolean estaRegistrado() {

		String query = "select * from chess.users where nickname = '" + firstName.getText() + "' and password = '"
				+ password.getText() + "';";
		ResultSet resultSet = db.ejecutarConsulta(query);

		try {
			return resultSet.next();
		} catch (SQLException e) {
			return true;
		} finally {
		}
	}

	private boolean clearForm() {
		firstName.clear();
		email.clear();
		password.clear();
		confirmPassword.clear();
		return true;
	}
	
	public void cargarLogin() {

		Stage stage1 = (Stage) (registerButton.getParent().getScene().getWindow());
		// Cerrar la ventana
		stage1.close();
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vista/Login.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();

			stage.setTitle("Login");
			stage.setResizable(false);
			stage.setScene(new Scene(root));

//				// Spawnear sense bordes
				stage.initStyle(StageStyle.TRANSPARENT);
//				// Hacer modal la ventana
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.show();

		} catch (Exception e) {
		}finally {
			db.closeConnection();
		}
	}

}