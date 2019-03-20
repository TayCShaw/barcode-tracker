package main;

public class Item {

	private String itemBrand;
	String itemName;
	private String itemUPC;
	private int itemCount;

	public Item() {
		itemBrand = "";
		itemName = "";
		itemUPC = "";
		itemCount = 0;
	}
	
	public Item(String brand, String name, String upc) {
		itemBrand = brand;
		itemName = name;
		itemUPC = upc;
		itemCount = 0;
	}

	public String getItemBrand() {
		return itemBrand;
	}

	public void setItemBrand(String itemBrand) {
		this.itemBrand = itemBrand;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemUPC() {
		return itemUPC;
	}

	public void setItemUPC(String itemUPC) {
		this.itemUPC = itemUPC;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	
}
