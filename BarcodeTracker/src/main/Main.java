package main;

import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{

	private static Stage primaryStage = new Stage();
	
	
	public static void main(String[] args){
//		Connection conn;
//		Scanner scannerUserInput = new Scanner(System.in);
//		int userInput;
//		
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
//				System.exit(0);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		launch(args);
	}
	
	
	public void start(Stage primaryStage) throws Exception{
		Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
		primaryStage.setTitle("Inventory Tracker");
		primaryStage.setScene(new Scene(root, 800, 500));
		primaryStage.show();
	}
	
	public static Stage getStage() {
		return primaryStage;
	}
	
	/**
	 * Primarily used for changing the scene in a stage.
	 * @param fxml Name of the fxml document to load
	 * @throws IOException 
	 */
	public void switchScene(String fxml) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(fxml));
		primaryStage.setScene(new Scene(root));
		
	}
	
	/*
	 * Prints the current stocked inventory
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
				boolean us = stmt.execute(updateString);
				
				
				System.out.println("Item count updated! Item count now at: " + itemCount);
			}else {
//				System.out.print("UPC Not Found. Please enter more information."
//						+ "\nBrand Name: ");
//				scannerAddItem.nextLine();// eats an extra \n
//				String itemBrand = scannerAddItem.nextLine(); 
//				
//				System.out.print("Item Name: ");
//				String itemName = scannerAddItem.nextLine();
//				
//				System.out.print("Initial Item Count: ");
//				int itemCount = scannerAddItem.nextInt();
//				
//				System.out.print("Item to be added: ");
//				System.out.println(itemCount + " of " + itemBrand + 
//						" " + itemName + " with UPC " + userUPC);
////possible				System.out.println("Does this look correct? Y/N");
				Item itemToAdd = new Item();
				itemToAdd.setItemName("");
				
				System.out.println("UPC not found. Please enter more information!");
				while(itemToAdd.getItemName() == "") {
					System.out.println("~~in whileLoop");
					itemToAdd = newItemInformation(scannerAddItem, userUPC);
				}
				
				updateString = "INSERT INTO ITEMS "
						+ "(ITEM_NAME, ITEM_BRAND, ITEM_COUNT, ITEM_UPC) VALUES"
						+ " ('" + itemToAdd.getItemName() + "', '" 
						+ itemToAdd.getItemBrand() + "', "
						+ itemToAdd.getItemCount() + ", '" + userUPC + "')";
				boolean is = stmt.execute(updateString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		main(null);	
	}
	
	
	/*
	 * 
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
	
	
	/*
	 * 
	 */
	static void removeItem(Connection conn) {
		// TODO Auto-generated method stub
		
	}


	/*
	 * Prints item information
	 */
	static void printItemInformation() {
		
	}


	

	
	


}
