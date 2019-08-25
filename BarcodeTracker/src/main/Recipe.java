package main;

import java.util.ArrayList;

public class Recipe {

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
	private String notes;
	private ArrayList<String> categoryTags;
	private ArrayList<String> ingredients;
	
	
	public Recipe() {
		recipeName = "";
		cookTime = 0;
		cookTemp = 0;
		numberServings = "";
		notes = "";
		setCategoryTags(new ArrayList<String>());
		ingredients = new ArrayList<String>();
	}
	
	public Recipe(String recipe, int time, int temp, String servings, String notes) {
		recipeName = recipe;
		cookTime = time;
		numberServings = servings;
		this.notes = notes;
	}


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


	public ArrayList<String> getCategoryTags() {
		return categoryTags;
	}


	public void setCategoryTags(ArrayList<String> categoryTags) {
		this.categoryTags = categoryTags;
	}
	
}
