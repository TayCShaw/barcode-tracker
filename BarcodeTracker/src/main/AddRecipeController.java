package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddRecipeController {

	ObservableList<String> servingsList = FXCollections.observableArrayList("Containers", "Cups", "Dozen", "Packages", "Servings", "Slices");
	
	// BEGIN: FXML Declaration
	@FXML
	ChoiceBox<String> choiceboxServings;
	
	@FXML
	ChoiceBox<String> choiceboxTemperature;
	
	@FXML
	TextField txtfieldRecipeName, txtfieldHours, txtfieldMinutes, txtfieldServings, txtfieldTemperature;
	
	@FXML
	TextArea txtareaIngredients, txtareaDirections;
	
	@FXML
	Button btnSaveRecipe, btnClear, btnBack;

	@FXML
	Label lblErrorMessage;
	// END: FXML Declaration
	
	
	
	public void setChoiceBoxServings() {
		choiceboxServings.getItems().addAll(servingsList);
		choiceboxServings.setValue("Servings");
	}
	
	public void setChoiceBoxTemperature() {
		choiceboxTemperature.getItems().addAll("°F", "°C");
		choiceboxTemperature.setValue("°F");
	}
	
	
	/*
	 * Button Methods
	 */
	
	/**
	 * Clears the page's controls so the user can reenter information
	 */
	public void btnClearClicked() {
		txtfieldRecipeName.clear();
		txtfieldHours.clear();
		txtfieldMinutes.clear();
		txtfieldServings.clear();
		txtfieldTemperature.clear();
		txtareaIngredients.clear();
		txtareaDirections.clear();
		lblErrorMessage.setText("");
	}
	
	/**
	 * 
	 */
	public void btnSaveRecipeClicked() {
		if(checkValidity()) {
			// First Step: Create a Recipe item
			Recipe newRec = new Recipe();
			newRec.setRecipeName(txtfieldRecipeName.getText());
			newRec.setCookTime((Integer.parseInt(txtfieldHours.getText()) * 60)
					+ (Integer.parseInt(txtfieldMinutes.getText()))); // Stores the number of minutes required	
			newRec.setCookTemp(Integer.parseInt(txtfieldTemperature.getText()));
			newRec.setNumberServings(txtfieldServings + choiceboxServings.getValue());
			
			//Second Step: Write the item to file
			
		}else {
			//Nothing. Remove later. Nothing is supposed to happen.
		}
	}
	
	
	private boolean checkValidity() {
		// Check for numbers in appropriate boxes
		// Check for boxes having an input
		String errorMessage = "";
		if(txtfieldRecipeName.getLength() == 0 || txtfieldServings.getLength() == 0 || txtfieldTemperature.getLength() == 0 || txtareaIngredients.getLength() == 0 || txtareaDirections.getLength() == 0) {
			// Missing vital recipe information
			errorMessage += "Please enter information into all boxes.";
			lblErrorMessage.setText(errorMessage);
			
			return false;
		}
		
		if(txtfieldHours.getLength() == 0 && txtfieldMinutes.getLength() == 0) {
			// There is no time input at all
			errorMessage += "Please enter in a cooking time.";
			lblErrorMessage.setText(errorMessage);
			
			return false;
		}
		
		try {
			Integer.parseInt(txtfieldHours.getText());
			Integer.parseInt(txtfieldMinutes.getText());
		}catch(NumberFormatException e) {
			lblErrorMessage.setText("Only numbers are allowed in the time fields");
			return false;
		}
		
		try {
			Integer.parseInt(txtfieldTemperature.getText());
		}catch(NumberFormatException e) {
			lblErrorMessage.setText("Only numbers are allowed in the temperature field");
			return false;
		}
		
		return true;

	}

	@FXML
	public void initialize() {
		setChoiceBoxServings();
		setChoiceBoxTemperature();
	}
}
