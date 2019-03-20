package main;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{

	private static Stage primaryStage = null;
	
	
	public static void main(String[] args){
		Connection conn;
		Scanner scannerUserInput = new Scanner(System.in);
		int userInput;
		
		/*
		 *ALL CONSOLE-BASED PROGRAM CODE 
		 */
//		try {
//			conn = DriverManager.getConnection("jdbc:ucanaccess://C:/CodeProjects/BarcodeTracker/items1.accdb");
//			
//			System.out.println("-----WELCOME TO YOUR INVENTORY TRACKER-----");
//			System.out.println("What would you like to do? Press the"
//					+ " corresponding number and hit enter.\n1) View"
//					+ " Inventory\n2) Add/Update an item\n3) Remove an item"
//					+ "\n4) Quit");
//			userInput = scannerUserInput.nextInt();
//			if(userInput == 1) {
//				printInventory(conn);
//			}else if(userInput == 2) {
//				addItem(conn);
//			}else if(userInput == 3) {
//				removeItem(conn);
//			}else if(userInput == 4) {
//				System.out.println("-----GOOD-BYE-----");
//				scannerUserInput.close();
//				System.exit(0);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		// END CONSOLE-BASED PROGRAM CODE
		
		launch(args);
	}
	
	/**
	 * Launches the GUI for the program
	 * @param primaryStage Stage that launches initially
	 */
	public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
		primaryStage.setTitle("Inventory Tracker");
		primaryStage.setScene(new Scene(root, 800, 500));
		primaryStage.setMinHeight(500);
		primaryStage.setMinWidth(500);
		primaryStage.show();
		Main.primaryStage = primaryStage;
	}
	
	/**
	 * Getter method for returning the current primary stage.
	 * @return The current primaryStage
	 */
	public static Stage getStage() {
		return primaryStage;
	}
	
	/**
	 * Changes the scene of the stage by passing in an FXML file
	 * to change to.
	 * @param fxml Name of the fxml document to load
	 * @throws IOException If fxml is not a valid fxml file
	 */
	public void switchScene(String fxml) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(fxml));
		Scene scene = Main.primaryStage.getScene();
		scene = new Scene(root, 700, 450);
		Main.primaryStage.setScene(scene);
	}
	
	
	/**
	 * CONSOLE-BASED Prints the current stocked inventory
	 * @param conn Connection to the database
	 */
	static void printInventory(Connection conn) {
		System.out.println("\n----------INVENTORY----------");
		
		String query = "Select ITEM_NAME, ITEM_BRAND, ITEM_COUNT, ITEM_UPC "
				+ "FROM ITEMS ";
		try (Statement stmt = conn.createStatement()){
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				String itemName = rs.getString("ITEM_NAME");
				String itemBrand = rs.getString("ITEM_BRAND");
				int itemCount = rs.getInt("ITEM_COUNT");
				
				if(itemCount > 0) {
					System.out.println(itemBrand + " " + itemName + 
							": " + itemCount);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("-----------------------------\n");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		main(null);
	}
	
	
	/*
	 * Attempts to add a new, non-existing item to the inventory.
	 * If the item is found, appropriate action is taken in order to
	 * increase the count.
	 * HOW:
	 * ---ASK user for a UPC
	 * ---CHECK if UPC is already in database
	 * ---FOUND? Update the count
	 * ---NOT FOUND? Ask for more info and add the new item to DB
	 */
	/**
	 * CONSOLE-BASED Attempts to add an item to the database.
	 * @param Conn The connection to the database.
	 */
	static void addItem(Connection Conn) {
		Scanner scannerAddItem = new Scanner(System.in);
		String searchString;
		String updateString;
		String userUPC;

		
		System.out.println("Enter a UPC to add or update: ");
		userUPC = scannerAddItem.next();
		searchString = "SELECT ITEMS.ITEM_COUNT"
				+ " FROM ITEMS"
				+ " WHERE ITEMS.ITEM_UPC = " + userUPC;
		
		// POSSIBLY MOVE THE SEARCH TO SEPARATE FUNCTION
		try(Statement stmt = Conn.createStatement()){
			ResultSet rs = stmt.executeQuery(searchString);
			if(rs.next()) {
				
				// Grabbing the item's count
				String countValue = rs.getString(1);
				System.out.println(countValue);
				
				// Updating the item's count by one
				int itemCount = Integer.parseInt(countValue);
				itemCount++;
				
				// Pushes update to Microsoft Access database
				updateString = "UPDATE ITEMS"
						+ " SET ITEMS.ITEM_COUNT = " + itemCount
						+ " WHERE ITEMS.ITEM_UPC = " + userUPC;
				if(!stmt.execute(updateString)) {
					System.out.println("Item count updated! Item count now at: " + itemCount);
				}
			}else {
				Item itemToAdd = new Item();
				itemToAdd.setItemName("");
				System.out.println("UPC not found. Please enter more information!");
				
				while(itemToAdd.getItemName() == "") {
					itemToAdd = newItemInformation(scannerAddItem, userUPC);
				}
				
				updateString = "INSERT INTO ITEMS "
						+ "(ITEM_NAME, ITEM_BRAND, ITEM_COUNT, ITEM_UPC) VALUES"
						+ " ('" + itemToAdd.getItemName() + "', '" 
						+ itemToAdd.getItemBrand() + "', "
						+ itemToAdd.getItemCount() + ", '" + userUPC + "')";
				if(stmt.execute(updateString)) {
					System.out.println("Item added into database!");
				}else {
					System.out.println("ERROR~!~ ITEM NOT ADDED");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		main(null);	
	}
	
	
	/**
	 * CONSOLE-BASED Prompts user to enter new information for
	 * an item that was not found in the database already.
	 * @param userInput The Scanner object declared in Main, used for
	 * taking user input
	 * @param UPC The UPC String taken from the caller method
	 */
	static Item newItemInformation(Scanner userInput, String UPC) {
		boolean looksGood = false;
		Item userItem = new Item();
		userItem.setItemUPC(UPC);
		
		while (!looksGood) {
			System.out.print("Brand name: ");
			userInput.nextLine();
			userItem.setItemBrand(userInput.nextLine());

			System.out.print("Item name: ");
			userItem.setItemName(userInput.nextLine());
			
			// INCREDIBLY WRONG. ONLY DOES ONE INCORRECT INPUT, ANY MORE AND CRASHES
			// Move to a while loop
			try {
				System.out.print("Initial count: ");
				userItem.setItemCount(userInput.nextInt());
			} catch(Exception e) {
				System.out.println("~~~Only numbers are allowed as inputs.~~~");
				userInput.next();
				System.out.print("Initial count: ");
				userItem.setItemCount(userInput.nextInt());
			}

			
			System.out.print("You are about to add: " + 
					userItem.getItemCount() + " of " + 
					userItem.getItemBrand() + " " + 
					userItem.getItemName() + ". \nDoes this look good? (Y or N): ");
			String uI = userInput.next();
			if(uI.equalsIgnoreCase("n")) {
				System.out.println("Then please reenter the item's information.");
				userItem.setItemName("");
				looksGood = true;
			}else if(uI.equalsIgnoreCase("Q")) {
				System.out.println("~Escaping~");

			}else{
				System.out.println("Great! Added.");
				looksGood = true;
			}
		}
		return userItem;
	}
	
	
	/**
	 * 
	 */
	static void removeItem(Connection conn) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * 
	 */
	static void printItemInformation() {
		
	}


	

	
	


}
