package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewInventoryController {

	// BEGIN FXML DECLARATION
	
	@FXML
	TableView tableviewRecipes;
	
	@FXML
	TableColumn<Recipe, String> colRecipeName;
	@FXML
	TableColumn<Recipe, String> colServings;
	@FXML
	TableColumn<Recipe, Integer> colTime;
	@FXML
	TableColumn<Recipe, String> colCategory;
	
	
	/*
	 * Left off at what other columns should be added to the TableView.
	 * Figure that out before doing anything else.
	 * 
	 * Change Recipe object code to reflect the GUI. Also, let each Recipe
	 * have one main category and allow for several tags
	 */
	
	
	
	
	
	
	
	
	
	
	// AUG 27 2019: UNTESTED
	private void fillData() {
		ObservableList<Recipe> recipes = FXCollections.observableArrayList();
		
		try {
			FileInputStream fi = new FileInputStream("C:/ItemTracker/recipes.txt");
			ObjectInputStream oi = new ObjectInputStream(fi);
			Recipe recipe = null;
			do {
				recipe = (Recipe) oi.readObject();
				if(recipe != null) {
					recipes.add(recipe);
				}
			}while(recipe != null);
			
			tableviewRecipes.setItems(recipes);
			
			if(recipes.size() == 0) {
				
			}
		} catch (FileNotFoundException e) { // FIS
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) { // OIS
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML
	public void initialize() {
		colRecipeName.setCellValueFactory(new PropertyValueFactory<Recipe, String>("recipeName"));
		colServings.setCellValueFactory(new PropertyValueFactory<Recipe, String>("numberServings"));
		colTime.setCellValueFactory(new PropertyValueFactory<Recipe, Integer>("cookTime"));
		colCategory.setCellValueFactory(new PropertyValueFactory<Recipe, String>("mainCategory"));
		try {
			
		}
	}
	
}
