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

public class RemoveItemController extends MainController {
	
	@FXML
	public Button btnConfirm, btnBack, btnFullyRemove;
	
	@FXML
	public Label lblQuantity, lblErrorMessage, lblUPCErrorMessage, lblOutput, lblItemFull, lblItemQuantity, lblCurrentQuantity;
	
	@FXML 
	public TextField txtfieldUPC, txtfieldQuantity;
	
	Connection conn = Main.conn;
	
	
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
	 * Handles when the Confirm button is clicked on the page. Starts
	 * searching for items and makes calls to the correct methods to
	 * handle searching and removing items.
	 * @param event
	 * @throws SQLException
	 */
	public void confirmButtonClicked(ActionEvent event) throws SQLException {
		
		/* This handles if the UPC is entered and then the user hits 
		 * the confirm button. This is one of three options to show
		 * the "Quantity" textbox. Otherwise, other methods handle if the
		 * txtfieldQuantity is visible based on if the user presses
		 * "ENTER" or "TAB" while in the UPC box.
		 */
		if((txtfieldUPC.getLength() != 0 && !txtfieldQuantity.isVisible()) || 
				(txtfieldUPC.getLength() != 0 && txtfieldQuantity.getLength() == 0)) {
			lblUPCErrorMessage.setVisible(false);
			lblErrorMessage.setVisible(false);
			
			try {
				ResultSet rs = searchItem();
				if(rs.next()) {
					String itemBrand = rs.getString("ITEM_BRAND");
					String itemName = rs.getString("ITEM_NAME");
					lblItemFull.setText(itemBrand + " " + itemName);
					lblItemFull.setVisible(true);
					
					lblItemQuantity.setText(rs.getString("ITEM_COUNT"));
					lblItemQuantity.setVisible(true);
					lblCurrentQuantity.setVisible(true);
					
					lblQuantity.setVisible(true);
					txtfieldQuantity.setVisible(true);
					
					txtfieldQuantity.requestFocus();
					btnFullyRemove.setVisible(true);
				}else {
					hideNameAndQuantity();
					showError("Item with that UPC not found.");
					
				}
			}catch(Exception e) {
				System.out.println(e.toString());
			}
			
		// UPC is given AND the quantity field is visible, so try to update count
		}else if(txtfieldUPC.getLength() != 0 && txtfieldQuantity.isVisible()){
			if(txtfieldQuantity.getLength() != 0) {
				// 0 if not updated, 1 if updated
				int removalResponse = removeQuantity();
				
				if(removalResponse == 1) {
					ResultSet itemInfo = grabItemInfo();
					if(itemInfo.next()) {
						lblOutput.setText("Updated count of " + 
						itemInfo.getString("ITEM_BRAND") + " " + 
						itemInfo.getString("ITEM_NAME") + " to " +
						itemInfo.getString("ITEM_COUNT"));
						lblOutput.setVisible(true);
						lblItemQuantity.setText(itemInfo.getString("ITEM_COUNT"));
					}

				}else if(removalResponse == 0){
					hideNameAndQuantity();
				}else if(removalResponse > 1) {
					lblOutput.setText("Multiple items with that UPC found, both were updated.");
					lblOutput.setVisible(true);
				}
			}else {
				// ASSUME THAT PERSON ENTERED A UPC, FIELDS APPEARED
				// THEN THE UPC IS CHANGED. OR, THEY HIT CONFIRM AGAIN
				// AND THE QUANTITY WAS EMPTY
			}

			
		}else if(txtfieldUPC.getLength() == 0) {
			lblUPCErrorMessage.setVisible(true);
		}
	}
	
	
	/**
	 * Hides certain labels and textfields when called. Usually when
	 * the page is cleared.
	 */
	public void hideNameAndQuantity() {
		lblItemQuantity.setVisible(false);
		lblItemFull.setVisible(false);
		lblQuantity.setVisible(false);
		txtfieldQuantity.setVisible(false);
		lblCurrentQuantity.setVisible(false);
		lblOutput.setVisible(false);
	}
	
	
	/**
	 * Called to display an error message in the error label.
	 * @param errorMessage The error message to display in
	 * the error label.
	 */
	public void showError(String errorMessage) {
		lblErrorMessage.setText(errorMessage);
		lblErrorMessage.setVisible(true);
	}
	
	
	/**
	 * Only handles if the ENTER button is pressed. This is used for
	 * accessibility reasons, such as pressing ENTER on the UPC
	 * text box to start the page events.
	 * @param keyEvent Occurs when a key is pressed. This method only
	 * checks to see if "ENTER" is pressed.
	 * @throws SQLException
	 */
	public void keyeventPressed(KeyEvent keyEvent) throws SQLException {
		if(keyEvent.getCode() == KeyCode.ENTER) {
			confirmButtonClicked(null);
		}
	}
	
	
	/**
	 * Handles the removal of a specific quantity for an item's
	 * record. Handles errors for invalid numbers such as negatives
	 * or Integer MAX_VALUE or MIN_VALUE.
	 * @return Number of rows changed. Typically returns 1 or 0. 1 if
	 * the update occurred, 0 if no rows changed (item not in db, SQL
	 * error, etc.).
	 * @throws SQLException
	 */
	public int removeQuantity() throws SQLException {
		// Check to see if number is valid
		int quantityToRemove;
		try {
			quantityToRemove = Integer.parseInt(txtfieldQuantity.getText());
			if(quantityToRemove < 0) {
				showError("Error: Negative number");
				return(0);
			}
		}catch(NumberFormatException e) {
			System.out.println("NumberFormatException: ");
			e.printStackTrace();
			return(0);
		}
	
		// Check to see if difference is >= 0
		String checkCount = "Select ITEM_COUNT from ITEMS where "
				+ "ITEMS.ITEM_UPC = ?";
		PreparedStatement countCheck = conn.prepareStatement(checkCount);
		countCheck.setString(1, txtfieldUPC.getText());
		
		ResultSet rs = countCheck.executeQuery();
		if(!rs.next()) {
			System.out.println("Error: No item found in removeQuantity()");
		}
		int currentQuantity = Integer.parseInt(rs.getString("ITEM_COUNT"));
		
		if(currentQuantity <= 0 || (currentQuantity - quantityToRemove) < 0) {
			showError("Error: Possible zero or negative quantity.");
			return(0);
		}
		
		// Proceed with removal
		String removeItem = "UPDATE ITEMS SET ITEMS.ITEM_COUNT = ITEMS.ITEM_COUNT - ? "
				+ "WHERE ITEMS.ITEM_UPC = ?";
		PreparedStatement removeQuantity = conn.prepareStatement(removeItem);
		removeQuantity.setInt(1, Integer.parseInt(txtfieldQuantity.getText()));
		removeQuantity.setString(2, txtfieldUPC.getText());
		
		return(removeQuantity.executeUpdate());
	}
	
	
	/**
	 * Used for returning a ResultSet. If the returned ResultSet .next()
	 * property is True, the item was found successfully in the data.
	 * If False, the item is not found in the data already.
	 * @return ResultSet of the database query.
	 * @throws SQLException 
	 */
	public ResultSet searchItem() throws SQLException {
		String searchForItem = "SELECT * FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
		
		conn = DriverManager.getConnection(connectionString);
		PreparedStatement searchItems = conn.prepareStatement(searchForItem);
		searchItems.setString(1, txtfieldUPC.getText());
		
		ResultSet rs = searchItems.executeQuery();
		
		return rs;
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
		grabInfo.setString(1, txtfieldUPC.getText());
		return(grabInfo.executeQuery());
	}

	
	/**
	 * Used to fully remove an entry from the database instead of
	 * simply removing a quantity of items
	 * @throws SQLException 
	 */
	public void fullyRemoveItem() {
		String deleteEntry = "Delete * from ITEMS where ITEM_UPC = ?";
		
		try {
			PreparedStatement entryDeletion = conn.prepareStatement(deleteEntry);
			entryDeletion.setString(1, txtfieldUPC.getText());
			if(entryDeletion.executeUpdate() == 0) {
				lblErrorMessage.setText("Error: Error fully removing.");
				lblErrorMessage.setVisible(true);
			}else {
				lblOutput.setText("Item fully removed from database.");
				lblOutput.setVisible(true);
			}
		}catch(SQLException e) {
			System.out.println("Error: SQLException located in fullyRemoveItem().");
			e.printStackTrace();
		}

	}
}
