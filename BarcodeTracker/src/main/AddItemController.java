package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class AddItemController extends MainController {
	@FXML
	public Button btnConfirm, btnClear, btnBack;

	@FXML
	public TextField txtfieldUPC, txtfieldBrand, txtfieldItem, txtfieldQuantity;
	
	@FXML
	public Label lblErrorMessage, lblActionDone, lblBrandName, 
	lblItemName, lblGrabbedBrand, lblGrabbedItem, lblUPCError, 
	lblQuantityError;

//	String connectionString = Main.connectionString;
	
	
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
	 * Clears all textfields, clears the lblErrorMessage field.
	 * Prepares the page as if user first accessed the page. Hides
	 * the textfields and labels for BrandName and ItemName.
	 * @param event Any event that calls the function.
	 */
	public void clearButtonClicked(ActionEvent event) {
		txtfieldUPC.clear();
		txtfieldBrand.clear();
		txtfieldItem.clear();
		txtfieldQuantity.clear();
		
		txtfieldBrand.setVisible(false);
		txtfieldItem.setVisible(false);
		lblBrandName.setVisible(false);
		lblItemName.setVisible(false);
		
		lblGrabbedBrand.setText("");
		lblGrabbedBrand.setVisible(false);
		lblGrabbedItem.setText("");
		lblGrabbedItem.setVisible(false);
		lblErrorMessage.setText("");
		lblErrorMessage.setVisible(false);
		lblActionDone.setText("");
		lblActionDone.setVisible(false);
		lblUPCError.setVisible(false);
		lblQuantityError.setVisible(false);
		
		txtfieldUPC.requestFocus();
	}

	
	/**
	 * Attempts to add the information from the form to the database.
	 * @throws SQLException
	 * @param event Any event that calls the function.
	 */
	public void confirmButtonClicked(ActionEvent event) throws SQLException {
		if(txtfieldUPC.getLength() != 0 && txtfieldQuantity.getLength() != 0) {
			lblQuantityError.setVisible(false);
			lblUPCError.setVisible(false);
			
			
			// 0 if not found, 1 if found
			int itemUpdated = updateItem();
			
			// NO ROW CHANGE, FIELD VISIBLE = ITEM NEEDS TO BE ADDED
			if(itemUpdated == 0 && txtfieldBrand.isVisible()) {
				// 0 if not updated, 1 if updated
				int itemAdded = insertItem();
				
				if(itemAdded == 1) {
					lblActionDone.setText("Added new item! " + txtfieldQuantity.getText() + 
							" of " + txtfieldBrand.getText() + " " + txtfieldItem.getText());
					lblActionDone.setVisible(true);
				}else {
					lblErrorMessage.setText("ERROR. Problem adding new item.");
					lblActionDone.setVisible(false);
				}
				
			// NO ROWS CHANGE, FIELDS VISIBLE = ITEM NOT THERE, ADD TO DB
			}else if(itemUpdated == 0){
				itemNotFound();				
			}else if(itemUpdated == 1) {
				ResultSet itemInformation = grabItemInfo();
				if(itemInformation.next()) {
					lblActionDone.setText("Updated count of " + itemInformation.getString(2)
					+ " " + itemInformation.getString(1) + " to " + itemInformation.getString(3));
					lblActionDone.setVisible(true);
				}
			}	
		}else {
			if(txtfieldUPC.getLength() == 0){
				lblUPCError.setVisible(true);
			}else {
				lblUPCError.setVisible(false);
			}
			
			if(txtfieldQuantity.getLength() == 0) {
				lblQuantityError.setVisible(true);
			}else {
				lblQuantityError.setVisible(false);
			}
		}

	}

	
	/**
	 * Based off of https://stackoverflow.com/questions/36754063/java-fx-scene-builder-how-to-make-event-key-pressed
	 * answer from "sad murshid".
	 * 
	 * @param keyEvent 
	 * @throws SQLException 
	 */
	public void keyeventPressed(KeyEvent keyEvent) throws SQLException {
		if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.TAB) {
			if (!txtfieldUPC.getText().isEmpty()) {
				ResultSet rs = searchItem();
				if (rs.next()) {
					itemFound();
				} else {
					itemNotFound();
				}
			} else {
				lblUPCError.setVisible(true);
			}
		}
	}
	
	public void quantityMouseeventPressed(MouseEvent mouseEvent) throws SQLException {
		if(txtfieldUPC.getLength() == 0) {
			
		}else if(MouseEvent.MOUSE_CLICKED != null && !txtfieldBrand.isVisible()) {
			ResultSet rs = searchItem();
			if(rs.next()) {
				itemFound();
			}else {
				itemNotFound();
			}
		}
	}
	
	
	/**
	 * Updates the GUI to show the appropriate textboxes
	 * and labels in order to enter a new item into the database.
	 */
	public void itemNotFound() {
		// Just in case the user changes the upc initially entered to a valid one
		lblGrabbedBrand.setVisible(false);
		lblGrabbedItem.setVisible(false);
		
		txtfieldBrand.setVisible(true);
		lblBrandName.setVisible(true);
		txtfieldItem.setVisible(true);
		lblItemName.setVisible(true);
		txtfieldBrand.requestFocus();
	}
	
	
	/**
	 * Displays the item's information to the user. Used for
	 * user acknowledgement that the item they are adding is correct.
	 * @throws SQLException
	 */
	public void itemFound() throws SQLException {
		// Just in case the user changes the upc initially entered to a valid one
		txtfieldBrand.setVisible(false);
		lblBrandName.setVisible(false);
		txtfieldItem.setVisible(false);
		lblItemName.setVisible(false);
		
		try {
			ResultSet rs = grabItemInfo();
			if(rs.next()) {
				lblGrabbedBrand.setText(rs.getString("ITEM_BRAND"));
				lblGrabbedBrand.setVisible(true);
				lblGrabbedItem.setText(rs.getString("ITEM_NAME"));
				lblGrabbedItem.setVisible(true);
				txtfieldQuantity.requestFocus();
			}
		}catch(Exception e) {
			System.out.println(e.toString());
		}

	}
	
	
	/**
	 * Attempts to pull information for an item based on the UPC given.
	 * The ResultSet's .next() property will be True if the item was
	 * found successfully, false otherwise.
	 * @return ResultSet of the database query.
	 * @throws SQLException
	 */
	public ResultSet grabItemInfo() throws SQLException {
		Connection conn = Main.conn;
		String grabItemInfo = "SELECT ITEMS.ITEM_NAME, ITEMS.ITEM_BRAND, ITEMS.ITEM_COUNT "
				+ "FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
		PreparedStatement grabInfo = conn.prepareStatement(grabItemInfo);
		grabInfo.setInt(1, Integer.parseInt(txtfieldUPC.getText()));
		return(grabInfo.executeQuery());
	}
	
	/**
	 * Used for returning a ResultSet. If the returned ResultSet .next()
	 * property is True, the item was found successfully in the data.
	 * If False, the item is not found in the data already.
	 * @return ResultSet of the database query.
	 * @throws SQLException 
	 */
	public ResultSet searchItem() throws SQLException {
		Connection conn;
		String searchForItem = "SELECT * FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
		
		conn = DriverManager.getConnection(connectionString);
		PreparedStatement searchItems = conn.prepareStatement(searchForItem);
		searchItems.setInt(1, Integer.parseInt(txtfieldUPC.getText()));
		
		ResultSet rs = searchItems.executeQuery();
		
		return rs;
	}
	
	
	/**
	 * Attempts to update the count of an item based on the UPC. Grabs
	 * the UPC and Quantity from the textfields on the form.
	 * @return 0 if the item was not updated (was not found, could not be updated, db not found, etc.)
	 * 1 if the item was updated successfully
	 * @throws SQLException
	 */
	public int updateItem() throws SQLException {
		Connection conn = Main.conn;
		String updateItemQuantity = "UPDATE ITEMS SET ITEMS.ITEM_COUNT = ITEMS.ITEM_COUNT + ? WHERE ITEMS.ITEM_UPC = ? ";
		PreparedStatement updateQuantity = conn.prepareStatement(updateItemQuantity);
		updateQuantity.setInt(1, Integer.parseInt(txtfieldQuantity.getText()));
		updateQuantity.setInt(2, Integer.parseInt(txtfieldUPC.getText()));
		
		return(updateQuantity.executeUpdate());
	}
	
	
	/**
	 * Attempts to add the item to the database. Grabs the needed information
	 * from the textfields on the form.
	 * @return 0 if the item was not added (SQL error, DB not found, etc.)
	 * 1 if the item was added successfully
	 * @throws SQLException
	 */
	public int insertItem() throws SQLException {
		Connection conn = Main.conn;
		
		String addItem = "INSERT INTO ITEMS (ITEM_NAME, ITEM_BRAND, ITEM_COUNT, ITEM_UPC) "
				+ "VALUES (?, ?, ?, ?)";
		PreparedStatement insertItem = conn.prepareStatement(addItem);
		insertItem.setString(1, txtfieldItem.getText());
		insertItem.setString(2, txtfieldBrand.getText());
		insertItem.setInt(3, Integer.parseInt(txtfieldQuantity.getText()));
		insertItem.setInt(4, Integer.parseInt(txtfieldUPC.getText()));
		
		return(insertItem.executeUpdate());
	}
}
