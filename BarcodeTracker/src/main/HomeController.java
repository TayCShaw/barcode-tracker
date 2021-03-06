package main;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;

public class HomeController extends MainController{
	
	/**
	 * Switches the scene to the AddItem.fxml scene so the user can
	 * add a new item to the database.
	 * @throws IOException IOException Based on switching the scene. Thrown if
	 * the name of the file being switched to does not exist.
	 */
	public void addButtonClicked(ActionEvent event) throws IOException {
		switchScene("AddItem.fxml");
		getStage().setTitle("Add Item");
	}

	
	/**
	 * Accesses the delete item page/function to decrease a count of an item.
	 * Currently only supports decreasing an item by a specific count,
	 * NOT fully removing an item from a database.
	 * 
	 * @throws IOException Based on switching the scene. Thrown if
	 * the name of the file being switched to does not exist.
	 * 
	 */
	public void deleteButtonClicked(ActionEvent event) throws IOException {
		switchScene("RemoveItem.fxml");
		getStage().setTitle("Delete Item");
//		getStage().show();
	}
	
	
	/**
	 * Closes the program.
	 */
	public void exitButtonClicked(ActionEvent event) {
		Platform.exit();
	}
	
	
	/**
	 * Switches the scene to the ViewInventory.fxml scene so the user 
	 * can view the inventory they have so far.
	 * @throws IOException If the ViewInventory.fxml page is missing
	 * 
	 */
	public void viewButtonClicked(ActionEvent event) throws IOException {
		switchScene("ViewInventory.fxml");
		getStage().setTitle("Inventory");
	}
}
