package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class RemoveItemController extends MainController {
	
	@FXML
	public Button btnConfirm, btnBack;
	
	@FXML
	public Label lblQuantity, lblErrorMessage, lblUPCErrorMessage, lblOutput, lblItemFull, lblItemQuantity;
	
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
					
					lblQuantity.setVisible(true);
					txtfieldQuantity.setVisible(true);
					
					txtfieldQuantity.requestFocus();
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
					}

				}else if(removalResponse == 0){
					hideNameAndQuantity();
					showError("Item with that UPC not found.");
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
	 * 
	 */
	public void hideNameAndQuantity() {
		lblItemQuantity.setVisible(false);
		lblItemFull.setVisible(false);
		lblQuantity.setVisible(false);
		txtfieldQuantity.setVisible(false);
	}
	
	
	/**
	 * 
	 * @param errorMessage
	 */
	public void showError(String errorMessage) {
		lblErrorMessage.setText(errorMessage);
		lblErrorMessage.setVisible(true);
	}
	
	
	public void keyeventPressed(KeyEvent keyEvent) throws SQLException {
		if(keyEvent.getCode() == KeyCode.ENTER) {
			confirmButtonClicked(null);
		}
	}
	
	/**
	 * 
	 * @return Number of rows changed. Typically returns 1 or 0. 1 if
	 * the update occurred, 0 if no rows changed (item not in db, SQL
	 * error, etc.).
	 * @throws SQLException
	 */
	public int removeQuantity() throws SQLException {
		String removeItem = "UPDATE ITEMS SET ITEMS.ITEM_COUNT = ITEMS.ITEM_COUNT - ? "
				+ "WHERE ITEMS.ITEM_UPC = ?";
		PreparedStatement removeQuantity = conn.prepareStatement(removeItem);
		removeQuantity.setInt(1, Integer.parseInt(txtfieldQuantity.getText()));
		removeQuantity.setInt(2, Integer.parseInt(txtfieldUPC.getText()));
		
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
		searchItems.setInt(1, Integer.parseInt(txtfieldUPC.getText()));
		
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
		grabInfo.setInt(1, Integer.parseInt(txtfieldUPC.getText()));
		return(grabInfo.executeQuery());
	}
}
