package main;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeController extends Main{
	
	public int width = 800;
	public int height = 500;
	
	public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
		primaryStage.setTitle("Inventory Tracker");
		primaryStage.setScene(new Scene(root, 800, 500));
		primaryStage.show();
	}
	
	/**
	 * @throws IOException 
	 * 
	 * 
	 */
	public void addButtonClicked(ActionEvent event) throws IOException {
		//primaryStage.setTitle("Add or Increase Item");
		//primaryStage.setScene(new Scene(root, 800, 500));
		//primaryStage.show();
		//setStage("Add or Update Item", root, width, height);
		switchScene("AddItem.fxml");
		getStage().show();
	}

	
	/**
	 * 
	 * 
	 */
	public void deleteButtonClicked(ActionEvent event) {
		
	}
	
	
	/**
	 * 
	 */
	public void exitButtonClicked(ActionEvent event) {
		Platform.exit();
	}
	
	
	/**
	 * 
	 */
	public void viewButtonClicked(ActionEvent event) {
		
	}
}
