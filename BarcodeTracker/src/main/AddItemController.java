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

public class AddItemController extends MainController {
	@FXML
	public Button btnConfirm, btnClear, btnBack;

	@FXML
	public TextField txtfieldUPC, txtfieldBrand, txtfieldItem, txtfieldQuantity;
	
	@FXML
	public Label lblErrorMessage, lblActionDone, lblBrandName, lblItemName, lblGrabbedBrand, lblGrabbedItem;

	String connectionString = "jdbc:ucanaccess://C:/CodeProjects/BarcodeTracker/items1.accdb";

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
		
		txtfieldUPC.requestFocus();
	}

	/**
	 * Attempts to add the information from the form to the database.
	 * @throws SQLException
	 * @param event Any event that calls the function.
	 */
	public void confirmButtonClicked(ActionEvent event) throws SQLException {
		Connection conn = DriverManager.getConnection(connectionString);
		
		String grabItemInfo = "SELECT ITEMS.ITEM_NAME, ITEMS.ITEM_BRAND, ITEMS.ITEM_COUNT "
				+ "FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
		PreparedStatement grabInfo = conn.prepareStatement(grabItemInfo);
		grabInfo.setInt(1, Integer.parseInt(txtfieldUPC.getText()));
		
		String updateItemQuantity = "UPDATE ITEMS SET ITEMS.ITEM_COUNT = ITEMS.ITEM_COUNT + ? WHERE ITEMS.ITEM_UPC = ? ";
		PreparedStatement updateQuantity = conn.prepareStatement(updateItemQuantity);
		updateQuantity.setInt(1, Integer.parseInt(txtfieldQuantity.getText()));
		updateQuantity.setInt(2, Integer.parseInt(txtfieldUPC.getText()));
		
		String addItem = "INSERT INTO ITEMS (ITEM_NAME, ITEM_BRAND, ITEM_COUNT, ITEM_UPC) "
				+ "VALUES (?, ?, ?, ?)";
		PreparedStatement insertItem = conn.prepareStatement(addItem);
		
		
		int rs = updateQuantity.executeUpdate();
		
		// NO ROWS CHANGE, FIELDS NOT VISIBLE = ITEM NOT UPDATED, ERROR
		if(rs == 0 && !txtfieldBrand.isVisible() && !txtfieldItem.isVisible()) {
			lblErrorMessage.setText("ERROR. ITEM NOT UPDATED.");
			
		// NO ROWS CHANGE, FIELDS VISIBLE = ITEM NOT THERE, ADD TO DB
		}else if(rs == 0 && txtfieldBrand.isVisible()){
			
			// Fills out the PreparedStatement
			insertItem.setString(1, txtfieldItem.getText());
			insertItem.setString(2, txtfieldBrand.getText());
			insertItem.setInt(3, Integer.parseInt(txtfieldQuantity.getText()));
			insertItem.setInt(4, Integer.parseInt(txtfieldUPC.getText()));
			
			int rs2 = insertItem.executeUpdate();
			if(rs2 == 1) {
				lblActionDone.setText("Added new item! " + txtfieldQuantity.getText() + 
						" of " + txtfieldBrand.getText() + " " + txtfieldItem.getText());
				lblActionDone.setVisible(true);
			}
		}else if(rs == 1) {
			ResultSet grabbedItem = grabInfo.executeQuery();
			if(grabbedItem.next()) {
				lblActionDone.setText("Updated count of " + grabbedItem.getString(2)
				+ " " + grabbedItem.getString(1) + " to " + grabbedItem.getString(3));
				lblActionDone.setVisible(true);
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
	public void enterPressed(KeyEvent keyEvent) throws SQLException {
		if (!txtfieldUPC.getText().isEmpty()) {

			if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.TAB) {
				Connection conn;
				String searchForItem = "SELECT * FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
				
				try {
					conn = DriverManager.getConnection(connectionString);
					PreparedStatement searchItems = conn.prepareStatement(searchForItem);
					searchItems.setInt(1, Integer.parseInt(txtfieldUPC.getText()));
					
					ResultSet rs = searchItems.executeQuery();
					
					if(rs.next()) {
						// Just in case the user changes the upc initially entered to a valid one
						txtfieldBrand.setVisible(false);
						lblBrandName.setVisible(false);
						txtfieldItem.setVisible(false);
						lblItemName.setVisible(false);
						
						
						lblGrabbedBrand.setText(rs.getString("ITEM_BRAND"));
						lblGrabbedBrand.setVisible(true);
						lblGrabbedItem.setText(rs.getString("ITEM_NAME"));
						lblGrabbedItem.setVisible(true);
						txtfieldQuantity.requestFocus();
						
					}else {
						// Just in case the user changes the upc initially entered to a valid one
						lblGrabbedBrand.setVisible(false);
						lblGrabbedItem.setVisible(false);
						
						txtfieldBrand.setVisible(true);
						lblBrandName.setVisible(true);
						txtfieldItem.setVisible(true);
						lblItemName.setVisible(true);
						txtfieldBrand.requestFocus();
					}
					
					
					conn.close();

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			
		}
	}
}
