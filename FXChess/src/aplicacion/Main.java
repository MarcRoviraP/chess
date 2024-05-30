package aplicacion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
//			String fxml = "vista/Inicio.fxml";
			String fxml = "vista/Chess.fxml";
			// Cargar la ventana
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(fxml));
			// Cargar la Scene
			Scene scene = new Scene(root);
			// Asignar propiedades al Stage
			primaryStage.setTitle("Chess");
			primaryStage.setResizable(false);
			// Asignar la scene y mostrar
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
        String url = "jdbc:postgresql://blebcfc2ttf9gnxyvcym-postgresql.services.clever-cloud.com:50013/blebcfc2ttf9gnxyvcym";
		String username = "u8d4gcpzekqpvufboehz";
		String passwd = "fgZD5HEPiV83IBIrf7Dulu9u5je1c0";
		
		
		try {
			
			Class.forName("org.postgresql.Driver");
			
			Connection connection = DriverManager.getConnection(url,username,passwd);
			
			Statement statement = connection.createStatement();
			
			ResultSet resultSet = statement.executeQuery("Select * From chess.piece");
			
			while (resultSet.next()) {
				
				String columnValues = resultSet.getString("position");
				System.out.println("Posicion: " + columnValues);
			}
			
			connection.close();
		} catch (SQLException e) {
			// TODO: handle exception
		}catch (ClassNotFoundException e) {
			// TODO: handle exception
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
