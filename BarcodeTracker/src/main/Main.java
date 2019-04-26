package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{

	private static Stage primaryStage = null;
	static Connection conn; 
	//static String connectionString = "jdbc:ucanaccess://./items1.accdb";
	static String connectionString = "";
	
	public static void main(String[] args) throws SQLException{
		
		createFile();
		conn = DriverManager.getConnection(connectionString);
		launch(args);
	}
	
	
	/**
	 * This is used when creating an executable JAR. The program will
	 * attempt to create a new directory ("DB") on the host machine
	 * on the "C:/" drive. This will store the data necessary for the
	 * program's uses.
	 * 
	 * Will possibly rework later on.
	 */
	public static void createFile() {
		String dbdir = "C:/DB";
		File f = new File(dbdir);
		if(!f.exists()) {
			f.mkdir();
		}
		String dbName = "items.accdb";
		String dbPath = dbdir + "/" + dbName;
		File f2 = new File(dbPath);
		if(!f2.exists()) {
			InputStream inStream = Main.class.getResourceAsStream("items1.accdb");
			try {
				Files.copy(inStream, f2.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String dbUrl = "jdbc:ucanaccess://" + dbPath;
		connectionString = dbUrl;
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


}
