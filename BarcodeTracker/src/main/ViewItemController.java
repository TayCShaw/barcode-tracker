package main;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewItemController extends MainController{
	
	@FXML
	Button btnBack;
	
	@FXML
	TableView<Item> tvInventory;
	
	@FXML
	TableColumn<Item, String> colBrand;
	@FXML
	TableColumn<Item, String> colItem;
	@FXML
	TableColumn<Item, String> colUPC;
	@FXML
	TableColumn<Item, Integer> colQuantity;

	/**
	 * Handles when the back button is hit. Calls the backButtonClick()
	 * method from MainController.java.
	 * @param event The trigger that causes the function to fire.
	 * @throws IOException Thrown if the trigger is invalid.
	 */
	public void backButtonClicked(ActionEvent event) throws IOException {
		backButtonClick(event);
	}

	
	/**
	 * Grabs all of the information from all records in the table
	 * to be displayed in the TableView on ViewInventory.fxml
	 * @return ResultSet of all records in the ITEMS table
	 * @throws SQLException If the query or prepareStatement fails.
	 */
	public ResultSet grabItemData() throws SQLException {
		ResultSet rs;
		String search = "Select * from ITEMS";
		PreparedStatement grabData = conn.prepareStatement(search);
		rs = grabData.executeQuery();				
		return rs;
	}

	
	/**
	 * Grabs the information from the records in the table by calling 
	 * another method, then using the resulting ResultSet to add new 
	 * Item objects to the to-be-returned ObservableList.
	 * @return ObservableList of Item objects that will be used to
	 * populate a TableView.
	 * @throws SQLException If grabbing record data, or pulling
	 * information from the ResultSet fails.
	 */
	public ObservableList<Item> fillData() throws SQLException{
		ObservableList<Item> items = FXCollections.observableArrayList();
		
		ResultSet rs = grabItemData();
		while(rs.next()) {
			items.add(new Item(rs.getString("ITEM_BRAND"), 
					rs.getString("ITEM_NAME"),  
					rs.getString("ITEM_UPC"), 
					rs.getInt("ITEM_COUNT")));
		}
				
		return items;
		
	}

	/**
	 * Inputs all data from the Microsoft Access database into the TableView.
	 */
	@FXML
	public void initialize() {
		try {
			colBrand.setCellValueFactory(new PropertyValueFactory<Item, String>("itemBrand"));
			colItem.setCellValueFactory(new PropertyValueFactory<Item, String>("itemName"));
			colQuantity.setCellValueFactory(new PropertyValueFactory<Item, Integer>("itemCount"));
			colUPC.setCellValueFactory(new PropertyValueFactory<Item, String>("itemUPC"));	
		
		}catch(NullPointerException e) {
			System.out.println("Failed in initialize: "); 
			e.printStackTrace();
		}
		
		try {
			tvInventory.setItems(fillData());
		} catch (SQLException e) {
			System.out.println("Failed in fillData: ");
			e.printStackTrace();
		}
		
	}
	
}
