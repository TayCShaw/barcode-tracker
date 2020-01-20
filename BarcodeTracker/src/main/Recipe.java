package main;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable{

	/*
	 * Everything needed in a recipe:
	 * --Ingredients and amounts
	 * --Cook time and temperature
	 * --Steps
	 * --Notes
	 * --Servings
	 * --Category/Tags (could be an arraylist or single string) (Breakfast, Lunch, Dinner, Desert, Sweets, Snack, Quick, Crocpot, etc.)
	 */
	private String recipeName;
	private int cookTime; // Stored as minutes, can be calculated as hours and mins for presentation to user
	private int cookTemp;
	private String numberServings;
	private String directions;
	private String notes;
	private String mainCategory;
	private ArrayList<String> otherTags;
	private ArrayList<String> ingredients;
	
	
	public Recipe() {
		recipeName = "";
		cookTime = 0;
		cookTemp = 0;
		numberServings = "";
		directions = "";
		notes = "";
		setMainCategory("");
		otherTags = new ArrayList<String>();
		ingredients = new ArrayList<String>();
	}
	
//	public Recipe(String recipe, int time, int temp, int servings, String notes) {
//		recipeName = recipe;
//		cookTime = time;
//		numberServings = servings;
//		this.notes = notes;
//	}


	public String getRecipeName() {
		return recipeName;
	}


	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}


	public int getCookTime() {
		return cookTime;
	}


	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
	}


	public int getCookTemp() {
		return cookTemp;
	}


	public void setCookTemp(int cookTemp) {
		this.cookTemp = cookTemp;
	}


	public String getNumberServings() {
		return numberServings;
	}


	public void setNumberServings(String numberServings) {
		this.numberServings = numberServings;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	public ArrayList<String> getIngredients() {
		return ingredients;
	}


	public void setIngredients(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}


	public ArrayList<String> getOtherTags() {
		return otherTags;
	}


	public void setOtherTags(ArrayList<String> categoryTags) {
		this.otherTags = categoryTags;
	}

	public String getDirections() {
		return directions;
	}

	public void setDirections(String directions) {
		this.directions = directions;
	}

	public String getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(String mainCategory) {
		this.mainCategory = mainCategory;
	}
	
	public String toString() {
		return(recipeName + " |\\| " + cookTime + " at " + cookTemp + " |\\| " +
				numberServings + " servings |\\| " + "directions:\n" + directions + 
				" |\\| " + notes + " |\\| " + mainCategory + " |\\| then theres two arraylists");
	}
	
}
