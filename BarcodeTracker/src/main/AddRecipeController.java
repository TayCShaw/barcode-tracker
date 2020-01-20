package main;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
	ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
	File recipeFile = new File("C:/ItemTracker/recipes.txt");
	
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
	// Either add a check in here to see if the recipe already exists or create a separate method
	
	/*
	 * 9/10/2019
	 * LEFT OFF AT: Clean up this whole area a little.
	 * Leaving session and creating a new session results in an overwritten recipes.txt
	 * Constant EOF exception 
	 */
	public void btnSaveRecipeClicked() {
		if(checkValidity()) {
			// First Step: Create a Recipe item
			Recipe newRec = new Recipe();
			newRec.setRecipeName(txtfieldRecipeName.getText());
			newRec.setCookTime((Integer.parseInt(txtfieldHours.getText()) * 60)
					+ (Integer.parseInt(txtfieldMinutes.getText()))); // Stores the number of minutes required	
			newRec.setCookTemp(Integer.parseInt(txtfieldTemperature.getText()));
			newRec.setNumberServings(Integer.parseInt(txtfieldServings.getText()) + " " + choiceboxServings.getValue());
			newRec.setDirections(txtareaDirections.getText());
			
			//Second Step: Write the item to file (file name currently set as "recipes.txt". Can be changed in Main.java
			try {
				FileOutputStream f;
				if(!recipeFile.exists()) {
					f = new FileOutputStream(new File("C:/ItemTracker/recipes.txt"));
				}else {
					f = new FileOutputStream(recipeFile);
				}
				recipeList.add(newRec);
				readObjectsFromFile();
				ObjectOutputStream o = new ObjectOutputStream(f);
				o.writeObject(recipeList);
				
				o.close();
				f.close();
			} catch (FileNotFoundException e) { // FOS
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) { // OOS
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}else {
			//Nothing. Remove this else part later. Nothing is supposed to happen.
		}
	}
	
	private void readObjectsFromFile() {
		try {
			FileInputStream fi = new FileInputStream("C:/ItemTracker/recipes.txt");
			ObjectInputStream oi = new ObjectInputStream(fi);
			
			Recipe recipe = new Recipe();
			
			while(recipe != null){
				recipeList = (ArrayList<Recipe>) oi.readObject();
//				recipe = (Recipe) oi.readObject();
//				if(recipe != null) {
//					recipeList.add(recipe);
//					System.out.println("new recipe: " + recipe);
//				}
			}
			

			
			oi.close();
			fi.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int count = 0;
		while(count < recipeList.size()) {
			System.out.println(recipeList.get(count));
			count++;
		}
		
	}
	
	private boolean checkValidity() {
		// Check for numbers in appropriate boxes
		// Check for boxes having an input
		String errorMessage = "";
		if(txtfieldRecipeName.getLength() == 0 || txtfieldServings.getLength() == 0 || 
				txtfieldTemperature.getLength() == 0 || txtareaIngredients.getLength() == 0 || 
				txtareaDirections.getLength() == 0) {
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
		
		// The following 3 try-catch can be combined into one, just did it this way to have specific error messages		
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
		
		try {
			Integer.parseInt(txtfieldServings.getText());
		}catch(NumberFormatException e) {
			lblErrorMessage.setText("Only numbers are allowed in the servings field");
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
