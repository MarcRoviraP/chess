package controlador;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import modelo.DataBase;

public class SeleccionColorController implements Initializable {

	

	@FXML
	private Pane girarMoneda;
	@FXML
	private Pane pulsarJugar;
	
	@FXML
	private Button boton;

	@FXML
	private Button cambiar;

	@FXML
	private Button jugar;

	@FXML
	private ImageView imageGIF;

	@FXML
	private Label jugador1;

	@FXML
	private Label jugador2;
	@FXML
	private Label texto;

	@FXML
	private ImageView monedaJugador1;

	@FXML
	private ImageView monedaJugador2;

	@FXML
	private AnchorPane pane;

	private ArrayList<Integer> ids;

	private boolean valor;
	private int game_id;

	private DataBase db = new DataBase();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		pulsarJugar.setVisible(false);
		valor = true;
		ids = new ArrayList<Integer>();
		Platform.runLater(() -> {
			Stage stage = (Stage) jugador1.getScene().getWindow();
			String id = stage.getTitle();
			insertarUsuarios(Integer.parseInt(id));

			boton.setOnMouseClicked((event) -> 
			{cargarGIF();
			girarMoneda.setVisible(false);
			texto.setVisible(false);
			});
			cambiar.setOnMouseClicked((event) -> cambiarCarasMoneda());
			jugar.setOnMouseClicked((event) -> mostrarVentana(game_id + ""));

		});
	}

	private void cargarGIF() {

		ArrayList<String> opciones = new ArrayList<String>();

		opciones.add("cara");
		opciones.add("cruz");

		Collections.shuffle(opciones);

		try {
			imageGIF.setVisible(true);
			Image gif = new Image(this.getClass().getResourceAsStream("/gif/" + opciones.get(0) + ".gif"));
			imageGIF.setImage(gif);
			imageGIF.setFitWidth(500);
			imageGIF.setFitHeight(500);
			imageGIF.setFitWidth(500);
			imageGIF.setFitHeight(500);

			// Crear una Timeline para ocultar la imagen después de un cierto tiempo
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
				Image img = new Image(this.getClass().getResourceAsStream("/utilidades/img/" + opciones.get(0)+".png"));

				imageGIF.setImage(img);
				imageGIF.setFitWidth(300);
				imageGIF.setFitHeight(300);
				pulsarJugar.setVisible(true);
				girarMoneda.setVisible(true);
			}));
			timeline.play(); // Iniciar la animación de la Timeline
		} catch (Exception e) {
			// Imprimir detalles del error en caso de excepción
			e.printStackTrace();
		}

		actualizarColor(opciones);
	}

	public void cambiarCarasMoneda() {

		String rutaCruz = "/utilidades/img/cruz.png";
		String rutaCara = "/utilidades/img/cara.png";

		if (valor) {

			monedaJugador1.setImage(new Image(getClass().getResourceAsStream(rutaCruz)));
			monedaJugador2.setImage(new Image(getClass().getResourceAsStream(rutaCara)));
		} else {
			monedaJugador1.setImage(new Image(getClass().getResourceAsStream(rutaCara)));
			monedaJugador2.setImage(new Image(getClass().getResourceAsStream(rutaCruz)));

		}

		valor = !valor;
	}

	public void actualizarColor(ArrayList<String> caraMoneda) {

		String color;
		if (caraMoneda.get(0).equals("cara")) {

			if (valor) {

				color = "Blanco";
			} else {

				color = "Negro";
			}
		} else {
			if (valor) {

				color = "Negro";
			} else {

				color = "Blanco";
			}
		}

		for (Integer id : ids) {
			String query = String.format("Update chess.play set user_color = '%s' where user_id = %d and game_id = %d;",
					color, id, game_id);
			color = color.equals("Blanco") ? "Negro" : "Blanco";

			db.ejecutar(query);
		}

		System.out.println(color);

	}

	public void insertarUsuarios(int game_ID) {

		System.out.println(game_ID);
		this.game_id = game_ID;
		ArrayList<String> nombresJugadores = new ArrayList<String>();

		String query = "select user_id from chess.play where game_id = " + game_ID + ";";
		ResultSet resultSet = db.ejecutarConsulta(query);

		try {
			while (resultSet.next()) {
				ids.add(resultSet.getInt("user_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (Integer id : ids) {

			query = "select nickname from chess.Users where user_id = " + id + ";";
			resultSet = db.ejecutarConsulta(query);
			try {
				resultSet.next();
				nombresJugadores.add(resultSet.getString("nickname"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		jugador1.setText(nombresJugadores.get(0));
		jugador2.setText(nombresJugadores.get(1));

	}

	public void mostrarVentana(String titulo) {

		cerrar();
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vista/Chess.fxml"));
			Parent root = (Parent) fxmlLoader.load();

			Stage stage = new Stage();
			stage.setTitle(titulo);
			stage.setResizable(false);
			stage.setScene(new Scene(root));

//			stage.setFullScreen(true);
			// Spawnear sin bordes
			stage.initStyle(StageStyle.TRANSPARENT);
			// Hacer modal la ventana
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.show();
		} catch (Exception e) {
			// Manejar excepción si es necesario
		}

		db.closeConnection();
	}

	public void cerrar() {
		Stage stage1 = (Stage) (boton.getParent().getScene().getWindow());
		// Cerrar la ventana
		stage1.close();
	}

}
