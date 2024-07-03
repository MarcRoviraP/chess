package controlador;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import enumerados.Color;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import modelo.DataBase;
import modelo.Jugador;
import modelo.Pieza;
import modelo.Tablero;
import utilidades.Util;

public class ChessController implements Initializable {

	@FXML
	private Label lblJugadorNegro;
	@FXML
	private Label lblJugadorBlanco;
	@FXML
	private Pane masterPane;

	@FXML
	private AnchorPane pane;

	@FXML
	private Button casilla00;
	@FXML
	private Button btnMenu;
	@FXML
	private Button gananNegras;
	@FXML
	private Button empate;
	@FXML
	private Button gananBlancas;

	@FXML
	private Button btnDama;
	@FXML
	private Button btnCaballo;
	@FXML
	private Button btnAlfil;
	@FXML
	private Button btnTorre;

	@FXML
	private Pane paneEleccio;
	private static ChessController chess;
	private static ArrayList<Node> rojos = new ArrayList<Node>();
	private static ArrayList<Node> piezas = new ArrayList<Node>();
	private int game_ID;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(() -> {
			Jugador jugadorBlanco = null;
			Jugador jugadorNegro = null;
			Stage stage = (Stage) btnAlfil.getScene().getWindow();
			String id = stage.getTitle();

			
			this.game_ID = Integer.parseInt(id);
			DataBase db = new DataBase();

			String query = String.format(
					"SELECT p.*,u.* FROM chess.users u join chess.play p on u.user_id = p.user_id where p.game_id = %d;",
					game_ID);

			ResultSet resultSet = db.ejecutarConsulta(query);

			try {

				while (resultSet.next()) {

					String nombre = resultSet.getString("nickname")+" ELO: " + resultSet.getInt("user_puntuation");
					String color = resultSet.getString("user_color");
					Color c = color.equals("Blanco") ? Color.B : Color.N;

					if (c == Color.B) {

						jugadorBlanco = new Jugador(nombre, c);
					} else {

						jugadorNegro = new Jugador(nombre, c);
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.closeConnection();
			}

			Tablero t = new Tablero(jugadorBlanco, jugadorNegro);
			lblJugadorBlanco.setText(jugadorBlanco.getNickname());
			lblJugadorNegro.setText(jugadorNegro.getNickname());

			chess = this;
			paneEleccio.setVisible(false);

			pane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ESCAPE) {
						mostrarVentana("/vista/Menu.fxml", "Menu");
					}
				}
			});

			btnMenu.setOnMouseClicked((event) -> mostrarVentana("/vista/Menu.fxml", "Menu"));

			btnAlfil.setOnMouseClicked((event) -> t.setEleccion("Alfil"));
			btnDama.setOnMouseClicked((event) -> t.setEleccion("Dama"));
			btnCaballo.setOnMouseClicked((event) -> t.setEleccion("Caballo"));
			btnTorre.setOnMouseClicked((event) -> t.setEleccion("Torre"));
			
			gananBlancas.setOnMouseClicked((event) -> determinarEstadoPartida("Blanco"));
			gananNegras.setOnMouseClicked((event) -> determinarEstadoPartida("Negro"));
			empate.setOnMouseClicked((event) -> determinarEstadoPartida("Empate"));

			ArrayList<Pieza> piezas = new ArrayList<>(t.getListaPiezas());
			for (Pieza pieza : piezas) {
				pane.getChildren().add(pieza);
			}
		});
	}

	public static Control enviarControl() {
		return chess.casilla00;
	}

	public static void insertarRojos(double x, double y) {
		if (chess == null) {
			throw new IllegalStateException("ChessController no ha sido inicializado.");
		}

		// Verificar la ruta de la imagen
		String str_img = "/utilidades/img/rojo.png"; // Asegúrate de que la ruta sea correcta
		Image img = new Image(chess.getClass().getResourceAsStream(str_img));
		ImageView imgv = new ImageView(img);

		// Ajustar las coordenadas para que estén dentro del área visible
		imgv.setX(x); // Ajusta estas coordenadas según sea necesario
		imgv.setY(y); // Ajusta estas coordenadas según sea necesario

		// Ajustar el tamaño del ImageView
		imgv.setFitWidth(Util.SIZE);
		imgv.setFitHeight(Util.SIZE);

		// Agregar el ImageView al pane

		imgv.setOpacity(0.2d);
		chess.pane.getChildren().add(imgv);
		rojos.add(imgv);
	}

	public static void borrarRojo() {
		try {
			chess.pane.getChildren().removeAll(rojos);
		} catch (Exception e) {
		}
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

	@FXML
	public static void cerrar() {
		
		Stage stage = (Stage) (enviarControl().getParent().getScene().getWindow());
		// Cerrar la ventana
		stage.close();
	}

	public static void mostrarEleccio() {

		chess.paneEleccio.toFront();
		chess.paneEleccio.setVisible(true);

	}

	public static void amagarEleccio() {

		chess.paneEleccio.setVisible(false);

	}

	public static void actualizarPiezasEnPane(ArrayList<Pieza> nuevasPiezas) {
		if (chess == null) {
			throw new IllegalStateException("ChessController no ha sido inicializado.");
		}

		// Eliminar las piezas actuales del pane
		for (Node node : chess.pane.getChildren()) {
			if (node instanceof Pieza) {
				piezas.add(node);
			}
		}
		chess.pane.getChildren().removeAll(piezas);

		// Agregar las nuevas piezas al pane
		for (Pieza nuevaPieza : nuevasPiezas) {
			chess.pane.getChildren().add(nuevaPieza);
		}
	}
	
	public void determinarEstadoPartida(String estado) {
		
		mostrarVentana("/vista/Acabar.fxml", game_ID + "");
		DataBase db = new DataBase();
		
		String query = String.format("Update chess.game set game_result = '%s' where game_id = %d;", estado,game_ID);		
		db.ejecutar(query);
		
		db.closeConnection();
	}

}
