package main;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController extends Main{
	
	@FXML
	public Button btnBack;
	
	
	/**
	 * Exits out of the current window and goes back to the main window.
	 * 
	 * Code based on:
	 * https://stackoverflow.com/questions/25037724/
	 * how-to-close-a-java-window-with-a-button-click-javafx
	 * -project/25038465
	 * @throws IOException 
	 * 
	 */
	@FXML
	public void backButtonClick(ActionEvent event) throws IOException {
		switchScene("Home.fxml");
		getStage().setTitle("Inventory Tracker");
	}
}
