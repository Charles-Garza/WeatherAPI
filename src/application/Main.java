package application;

import java.io.IOException;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application 
{
	/**
	 * Creates the primary base for which our application can run on.
	 *
	 * @param primaryStage Stage
	 */
	@Override
	public void start(Stage primaryStage)
	{
		try 
		{
			/**
			 * Loads the initial scene/screen
			 */
            Parent root = FXMLLoader.load(getClass().getResource("../weatherapp_layout.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts and launches the application.
	 *
	 * @param args String[]
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
