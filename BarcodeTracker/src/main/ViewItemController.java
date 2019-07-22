package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ViewItemController extends MainController{
	
	// BEGIN: Declare all necessary FXML objects below
	
	@FXML
	Button btnRemove, btnModify, btnSaveUpdate, btnSaveAdd, btnReset, btnSearch2;
	
	@FXML
	MenuButton menuBtnSearch, menuBtnAdd;
	
	@FXML
	Label lblUPC, lblBrand, lblItem, lblQuantity;
	
	@FXML
	TextField txtfieldUPC, txtfieldBrand, txtfieldItem, txtfieldQuantity;
	
	@FXML
	TextArea txtareaStatus;
	
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
	
	// END FXML declaration

	
	// Used for detecting what item is selected in the TableView
	Item selectedItem = null;
	
	
	
	/*
	 * ----------Button Handling----------
	 */	
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
	 * Handles editing the item when an item is right-clicked in the
	 * TableView.
	 * @param item Item object whose parameters are to be displayed
	 * in the textboxes for editing.
	 */
	public void editButtonMenu(Item item) {
		hideSearchButtons();
		hideAllFields();
		showAllFields();
		btnReset.setVisible(true);
		btnSaveUpdate.setVisible(true);
		txtfieldUPC.setText(item.getItemUPC());
		txtfieldBrand.setText(item.getItemBrand());
		txtfieldItem.setText(item.getItemName());
		txtfieldQuantity.setText(Integer.toString(item.getItemCount()));
	}
	
	/**
	 * Handles editing the item when an item is selected from the
	 * TableView and then the "Edit Item" button is clicked above.
	 * @param event The event that occurs when the button is clicked.
	 */
	public void editButtonClicked(ActionEvent event) {
		if(selectedItem != null) {
			editButtonMenu(selectedItem);
		}

	}
	
	/** 
	 * Saves the changed information to the database.
	 * Updates the database record based upon the retrieved item UPC.
	 * Important to note that the item's UPC cannot be changed. The
	 * only way to change an item's UPC is to remove and reenter the
	 * item into the database.
	 */
	public void saveButtonUpdateClicked(ActionEvent event) {
		try {
			String update = "UPDATE ITEMS SET ITEMS.ITEM_BRAND = ?, "
					+ "ITEMS.ITEM_NAME = ?, ITEMS.ITEM_COUNT = ? "
					+ "WHERE ITEMS.ITEM_UPC = ?";
			PreparedStatement updateItem = conn.prepareStatement(update);
			updateItem.setString(1, txtfieldBrand.getText());
			updateItem.setString(2, txtfieldItem.getText());
			updateItem.setString(3, txtfieldQuantity.getText());
			updateItem.setString(4, txtfieldUPC.getText());
			
			int result = updateItem.executeUpdate();
			
			if(result != 0) {
				//Show "Save Successful" message
				txtareaStatus.setText("Save successful. Item information updated.");
				fillData(conn.prepareStatement("SELECT * FROM ITEMS"));
				hideAllFields();
			}else {
				//Show "Save Failed" message
				txtareaStatus.setText("Save failed. Item information not updated. \n\nDEBUG: Result is " + result);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			printGenericError("saveButtonUpdateClicked()");
		}
		
	}
	
	/**
	 * Handles saving a new item to the database
	 * @param event Event that causes the method to activate
	 * @throws NumberFormatException 
	 * @throws SQLException 
	 */
	public void saveButtonAddClicked(ActionEvent event) throws NumberFormatException, SQLException{
		Connection conn = Main.conn;
		
		int result = 0;
		
		txtareaStatus.setText("yo we in here"); // TEST LINE
		
		ResultSet rs = searchItem(txtfieldUPC.getText());
		if(rs.next()) {
			txtareaStatus.setText("yo we in here again"); // TEST LINE
			String updateItemQuantity = "UPDATE ITEMS SET ITEMS.ITEM_COUNT = ITEMS.ITEM_COUNT + ? WHERE ITEMS.ITEM_UPC = ? ";
			PreparedStatement updateQuantity;
			try {
				updateQuantity = conn.prepareStatement(updateItemQuantity);
				updateQuantity.setString(2, txtfieldUPC.getText());
				updateQuantity.setInt(1, Integer.parseInt(txtfieldQuantity.getText()));
				
				result = updateQuantity.executeUpdate();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else {
			
			String addItem = "INSERT INTO ITEMS (ITEM_NAME, ITEM_BRAND, ITEM_COUNT, ITEM_UPC) "
					+ "VALUES (?, ?, ?, ?)";
			
			PreparedStatement insertItem;
			try {
				insertItem = conn.prepareStatement(addItem);
				insertItem.setString(1, txtfieldItem.getText());
				insertItem.setString(2, txtfieldBrand.getText());
				insertItem.setString(4, txtfieldUPC.getText());
				try {
					int quantity = Integer.parseInt(txtfieldQuantity.getText());
					if(quantity >= 0) {
						insertItem.setInt(3, Integer.parseInt(txtfieldQuantity.getText()));
					}
					
					result = insertItem.executeUpdate();
					
					
				}catch(NumberFormatException e) {

					e.printStackTrace();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}	
		
		if(result != 0) {
			//Show "Save Successful" message
			txtareaStatus.setText("Save successful. Item information updated.");
			fillData(conn.prepareStatement("SELECT * FROM ITEMS"));
			hideAllFields();
		}else {
			//Show "Save Failed" message
			txtareaStatus.setText("Save failed. Item information not updated. \n\nDEBUG: Result is " + result);
		}
	}
	
	/**
	 * Handles removing an item from the database completely.
	 *
	 * @param event The event that prompted this method.
	 */
	public void removeClicked(ActionEvent event) {
		Alert removalAlert = new Alert(AlertType.CONFIRMATION,
				"Fully remove " + selectedItem.getItemBrand()
				+ " " + selectedItem.getItemName() + 
				" from database?", ButtonType.YES, 
				ButtonType.NO, ButtonType.CANCEL);
		removalAlert.showAndWait();

		if (removalAlert.getResult() == ButtonType.YES) {
			try {
				ResultSet rs = searchItem(selectedItem.getItemUPC());

				if (rs != null) {
					String removal = "DELETE * FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
					Connection conn = DriverManager.getConnection(connectionString);
					PreparedStatement removeItem = conn.prepareStatement(removal);
					removeItem.setString(1, selectedItem.getItemUPC());
					int result = removeItem.executeUpdate();

					if (result != 0) {
						txtareaStatus.setText("Successfully removed from the database.\n" + selectedItem.getItemBrand()
								+ " " + selectedItem.getItemName());
						fillData(conn.prepareStatement("SELECT * FROM ITEMS"));

					} else {
						txtareaStatus.setText("Problem on removal. \n" + selectedItem.getItemBrand() + " "
								+ selectedItem.getItemName() + " not removed.");
					}
				} else {
					txtareaStatus.setText("ERROR: Item was not found in the database.");
				}
			} catch (SQLException e) {
				txtareaStatus.setText(e.toString());
			}
		}
	}
	
	/**
	 * Handles events performed when the reset button is clicked after a search
	 * @param event The event that prompted this method.
	 */
	public void resetButtonClicked(ActionEvent event) {
		txtfieldUPC.setText("");
		txtfieldBrand.setText("");
		txtfieldItem.setText("");
		txtfieldQuantity.setText("");
		if(txtfieldUPC.isVisible()) txtfieldUPC.setEditable(false);
		hideAllFields();
		hideSearchButtons();
		hideSaveButtons();
		
		try {
			fillData(conn.prepareStatement("SELECT * FROM ITEMS"));
		} catch (SQLException e) {
			printGenericError("resetButtonClicked() fillData()");
			e.printStackTrace();
		}
	}
	
	/**
	 * Handles the different search methods based on what is active on the page
	 * @param event
	 */
	public void searchButtonClicked(ActionEvent event) {
		if(txtfieldUPC.isVisible() && (txtfieldUPC.getLength() > 0)) {
			upcSearch(event);
		}else if(txtfieldBrand.isVisible() && (txtfieldBrand.getLength() > 0)) {
			brandSearch(event);
		}else if(txtfieldItem.isVisible() && (txtfieldItem.getLength() > 0)) {
			itemSearch(event);
		}
		
	}
	
	
	public void addButtonClicked(ActionEvent event) {
		if(!txtfieldUPC.isVisible() || !txtfieldBrand.isVisible() || !txtfieldItem.isVisible() || !txtfieldQuantity.isVisible()) {
			showAllFields();
			btnReset.setVisible(true);
			txtfieldUPC.setEditable(true);
			btnSaveAdd.setVisible(true);
		}		
	}
	
	public void addBulkClicked(ActionEvent event) {
		
	}
	
	
	
	/*
	 * ----------Search Handling----------
	 */
	/**
	 * Searches the database based on the given UPC. If an item is found, that item then appears
	 * in the TableView as the only item until the user resets the search.
	 * @param event The event that prompted this method.
	 */
	public void upcSearch(ActionEvent event) {
		//If any other field is visible, something was in progress. So clear it and set up
		//for the search. This applies to every search type.
		if(txtfieldUPC.isVisible() && !txtfieldQuantity.isVisible()) {
			String search = "SELECT * FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
			
			try {
				PreparedStatement upcSearch = conn.prepareStatement(search);
				upcSearch.setString(1, txtfieldUPC.getText());
				
				fillData(upcSearch);
				
			} catch (SQLException e) {
				printGenericError("setSearchButton() byUPC");
				e.printStackTrace();
			}
		}else {
			hideAllFields();		
			showField(txtfieldUPC, lblUPC);
			txtfieldUPC.setEditable(true);
			showSearchButtons();
		}
	}
	
	/**
	 * Searches the database based on the given brand name. For any items found that contain the keyword specified
	 * in their brand name, those items appear as the only items in the TableView until the user resets the search.
	 * @param event The event that prompted this method.
	 */
	public void brandSearch(ActionEvent event) {
		//For larger databases, this is a slow search method (would use full text search).
		//For this program's purpose, it will be entirely fine. Will not be many items at all.
		if (txtfieldBrand.isVisible() && !txtfieldQuantity.isVisible()) {

			String search = "SELECT * FROM ITEMS WHERE ITEMS.ITEM_BRAND LIKE ?";

			try {
				PreparedStatement brandSearch = conn.prepareStatement(search);
				brandSearch.setString(1, "%" + txtfieldBrand.getText() + "%"); //Wildcards
				
				fillData(brandSearch);
				
			} catch (SQLException e) {
				printGenericError("setSearchButton() byBrand");
				e.printStackTrace();
			}
		} else {
			hideAllFields();
			showField(txtfieldBrand, lblBrand);
			showSearchButtons();
		}
	}
	
	/**
	 * Searches the database based on a given item name. For any items found that contain the keyword specified in
	 * their item name, they will appear as the only items in the TableView until the user resets the search.
	 * @param event The event that prompted this method.
	 */
	public void itemSearch(ActionEvent event) {
		String search = "SELECT * FROM ITEMS WHERE ITEMS.ITEM_NAME LIKE ?";
		
		if (txtfieldItem.isVisible() && !txtfieldQuantity.isVisible()) {

			try {
				PreparedStatement itemSearch = conn.prepareStatement(search);
				itemSearch.setString(1, "%" + txtfieldItem.getText() + "%"); //Wildcards
				
				fillData(itemSearch);
				
			} catch (SQLException e) {
				printGenericError("setSearchButton() byItem");
				e.printStackTrace();
			}
		}else {
			hideAllFields();
			showField(txtfieldItem, lblItem);
			showSearchButtons();
		}
	}
	
	
	
	/*
	 * ----------Handling Page Objects----------
	 */
	/**
	 * Generic method that takes in a textfield and its label to only
	 * display those things on screen
	 * @param txtField Textfield to display. Use the specific FX:ID
	 * @param lbl Associated label to display. Use the specific FX:ID
	 */
 	public void showField(TextField txtField, Label lbl) {
		txtField.setVisible(true);
		lbl.setVisible(true);
	}
	
	/**
	 * Generic method that hides all textboxes and their labels. Can
	 * be used in order to reset the scene.
	 */
	public void hideAllFields() {
		txtfieldUPC.setVisible(false);
		lblUPC.setVisible(false);
		txtfieldUPC.setEditable(false);
		
		txtfieldBrand.setVisible(false);
		lblBrand.setVisible(false);
		
		txtfieldItem.setVisible(false);
		lblItem.setVisible(false);
		
		txtfieldQuantity.setVisible(false);
		lblQuantity.setVisible(false);
	}
	
	/**
	 * Generic method that shows all textboxes and their labels.
	 * Design purpose is to be used when an item is being edited,
	 * all fields appear and can be edited accordingly before being
	 * saved.
	 */
	public void showAllFields() {
		txtfieldUPC.setVisible(true);
		lblUPC.setVisible(true);
		
		txtfieldBrand.setVisible(true);
		lblBrand.setVisible(true);
		
		txtfieldItem.setVisible(true);
		lblItem.setVisible(true);
		
		txtfieldQuantity.setVisible(true);
		lblQuantity.setVisible(true);
	}
	
	/**
	 * Handles showing the Reset and Search buttons when a search is active
	 */
	public void showSearchButtons() {
		btnReset.setVisible(true);
		btnSearch2.setVisible(true);
	}
	
	/**
	 * Handles hiding the Reset and Search buttons when closing a search
	 */
	public void hideSearchButtons() {
		btnReset.setVisible(false);
		btnSearch2.setVisible(false);		
	}
	
	
	public void showSaveButton(Button btnName) {
		btnName.setVisible(true);
	}
	
	/**
	 * Handles hiding the save buttons
	 */
	public void hideSaveButtons() {
		btnSaveUpdate.setVisible(false);
		btnSaveAdd.setVisible(false);
	}
	
	/**
	 * Handles all interactions with the TableView. Handles
	 * right-clicking on table rows.
	 * Resources: https://coderanch.com/t/688206/java/Data-Object-Single-Row-TableView
	 * Resources: https://stackoverflow.com/questions/21009377/context-menu-on-a-row-of-tableview
	 * Resources: https://stackoverflow.com/questions/8309981/how-to-create-and-show-common-dialog-error-warning-confirmation-in-javafx-2
	 */
	public void tableClick() {
		ContextMenu cMenu = new ContextMenu();
		MenuItem editItem = new MenuItem("Edit");
		MenuItem removeItem = new MenuItem("Remove");
		cMenu.getItems().add(editItem);
		cMenu.getItems().add(removeItem);
		
		
		tvInventory.setOnMouseClicked((MouseEvent mEvent) -> {
			txtareaStatus.clear();
			cMenu.hide();
			int index = tvInventory.getSelectionModel().getSelectedIndex();
			Item item = tvInventory.getItems().get(index);
			
			selectedItem = item;
			
			if(mEvent.getButton().equals(MouseButton.SECONDARY)) {
				cMenu.show(tvInventory, mEvent.getScreenX(), mEvent.getScreenY());
				item.toString();

				editItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						editButtonMenu(item);
					
					}
				});
				
				removeItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						removeClicked(event);
					}
				});
			}
		});
	}

	
	 
	/*
	 * ----------Helper Methods----------
	 */
	/**
	 * Searches the database to see if the item currently exists.
	 * @param UPC
	 * @return ResultSet of items found (or not found)
	 */
	public ResultSet searchItem(String UPC) {
		Connection conn;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(connectionString);
			String search = "SELECT * FROM ITEMS WHERE ITEMS.ITEM_UPC = ?";
			PreparedStatement searchItem = conn.prepareStatement(search);
			searchItem.setString(1, UPC);
			rs = searchItem.executeQuery();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			printGenericError("searchItem(String UPC){}");
		}
		return rs;		
	}
		
	/**
	 * Prints a generic error based on the function that failed.
	 * This is only used for debugging purposes and will most likely
	 * be removed in a "final" version.
	 * @param fcn
	 */
	public void printGenericError(String fcn) {
		txtareaStatus.setText("Error during the function \"" + fcn + "\". Run the program in Eclipse and check the console, or read the error log.");
	}
	
	
	
	/*
	 * ----------Creating the Page----------
	 */
	/**
	 * Grabs item data based upon the passed in PreparedStatement
	 * @param ps PreparedStatement used to search for the data.
	 * @return ResultSet of the data found. Will never be null.
	 */
	public ResultSet grabItemData(PreparedStatement ps) {
		ResultSet rs = null;
		
		try {
			rs = ps.executeQuery();
		}catch(SQLException e) {
			printGenericError("grabItemData(PreparedStatement)");
			e.printStackTrace();
		}
		
		return rs;
		
	}
	
	
	/**
	 * Grabs item information based on the passed in prepared statement.
	 * Sets the found data into the TableView on the page.
	 */
	public void fillData(PreparedStatement ps) {
		ObservableList<Item> items = FXCollections.observableArrayList();
		
		ResultSet rs = grabItemData(ps);
		try {
			while(rs.next()) {
				items.add(new Item(rs.getString("ITEM_BRAND"), 
							rs.getString("ITEM_NAME"),  
							rs.getString("ITEM_UPC"), 
							rs.getInt("ITEM_COUNT")));
			}
		} catch (SQLException e) {
			printGenericError("fillData()");
			e.printStackTrace();
		}				
		tvInventory.setItems(items);
		
		/*
		 * Two reasons:
		 * 1) Let the user know that there are no items in the table on purpose.
		 * 2) Clear that message after a successful search was completed.
		 */
		if(items.size() == 0) {
			txtareaStatus.setText("No items were found.");
		}else {
			txtareaStatus.setText("");
		}
		
	}
	
	// Most likely come back and add code for accepting a boolean input if the user wants an explicit search
	public void setSearchButton() {
		MenuItem byUPC = new MenuItem("By UPC");
		MenuItem byBrand = new MenuItem("By Brand");
		MenuItem byItem = new MenuItem("By Item");
		menuBtnSearch.getItems().addAll(byUPC, byBrand, byItem);

		byUPC.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				upcSearch(event);
			}
		});
		
		byBrand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				brandSearch(event);
			}
		});
		
		byItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				itemSearch(event);
			}
		});
	}
	
	public void setAddButton() {
		MenuItem singleAdd = new MenuItem("Add Item");
		MenuItem bulkAdd = new MenuItem("Bulk Add");
		menuBtnAdd.getItems().addAll(singleAdd, bulkAdd);
		
		singleAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addButtonClicked(event);
			}
		});
		
		bulkAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addBulkClicked(event);
			}
		});
		
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
			printGenericError("initialize()");
			e.printStackTrace();
		}

		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM ITEMS");
			fillData(ps);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		setSearchButton();
		setAddButton();
		
	}
	
}


