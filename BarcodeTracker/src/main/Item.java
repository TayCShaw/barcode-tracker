package main;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Item {

	private SimpleStringProperty itemBrand;
	private SimpleStringProperty itemName;
	private SimpleStringProperty itemUPC;
	private SimpleIntegerProperty itemCount;

	public Item() {
		itemBrand = new SimpleStringProperty("");
		itemName = new SimpleStringProperty("");
		itemUPC = new SimpleStringProperty("");
		itemCount = new SimpleIntegerProperty(0);
	}
	
	public Item(String brand, String name, String upc) {
		itemBrand = new SimpleStringProperty(brand);
		itemName = new SimpleStringProperty(name);
		itemUPC = new SimpleStringProperty(upc);
		itemCount = new SimpleIntegerProperty(0);
	}
	
	public Item(String brand, String name, String upc, int count) {
		itemBrand = new SimpleStringProperty(brand);
		itemName = new SimpleStringProperty(name);
		itemUPC = new SimpleStringProperty(upc);
		itemCount = new SimpleIntegerProperty(count);
	}

	public String getItemBrand() {
		return itemBrand.get();
	}

	public void setItemBrand(String itemBrand) {
		this.itemBrand.set(itemBrand);
	}

	public String getItemName() {
		return itemName.get();
	}

	public void setItemName(String name) {
		this.itemName.set(name);
	}

	public String getItemUPC() {
		return itemUPC.get();
	}

	public void setItemUPC(String itemUPC) {
		this.itemUPC.set(itemUPC);
	}

	public int getItemCount() {
		return itemCount.get();
	}

	public void setItemCount(int itemCount) {
		this.itemCount.set(itemCount);
	}
	
	public String toString() {
		System.out.println("Brand: " + getItemBrand());
		System.out.println("Name: " + getItemName());
		System.out.println("Count: " + getItemCount());
		System.out.println("UPC: " + getItemUPC());		
		return null;		
	}
	
}
