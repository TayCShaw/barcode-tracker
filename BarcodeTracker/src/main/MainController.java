package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {
	
	@FXML
	public Button btnBack;
	
	
	/**
	 * Exits out of the current window and goes back to the main window.
	 * 
	 * Code based on:
	 * https://stackoverflow.com/questions/25037724/
	 * how-to-close-a-java-window-with-a-button-click-javafx
	 * -project/25038465
	 * 
	 */
	@FXML
	public void backButtonClick(ActionEvent event) {
		Stage stage = (Stage) btnBack.getScene().getWindow();
		stage.close();
		
	}
}
