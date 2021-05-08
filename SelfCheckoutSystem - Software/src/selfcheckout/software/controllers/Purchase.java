package selfcheckout.software.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import org.lsmr.selfcheckout.Item;

/**
 * Holds a record of items which have been successfully scanned by the customer in the current transaction
 * 
 */
public class Purchase {

	// list of items which have been successfully added to the customer's transaction
	private final ArrayList<Item> itemList;
	private final HashMap<Item, Boolean> itemBaggingTracker = new HashMap<>();

	// constructor
	public Purchase() {
		this.itemList = new ArrayList<>();
	}

	/** Adds an item to the customer's list of purchases in the current transaction
	 * and updates the current transaction total
	 * 
	 * @param item The item to be added to the customer's transaction
	 */
	public void addItem(Item item) {
		// update the list of items which have been purchased
		itemList.add(item);
		// assume item will be bagged
		itemBaggingTracker.put(item, true);
	}

	public ArrayList<Item> getCurrentPurchases() {
		return itemList;
	}

	/**
	 * 
	 * @param itemIndex The index of the item to be removed
	 * @return If the item exists in the itemList and was removed
	 */
	public Item removeItem(int itemIndex) {
		Item itemRemoved = itemList.get(itemIndex);
		itemList.remove(itemIndex);
		this.itemBaggingTracker.remove(itemRemoved);
		return itemRemoved;
	}

	public boolean isBagged(Item item) {
		return itemBaggingTracker.get(item);
	}

	public void setItemTrackBagging(Item item, boolean isBagged) {
		this.itemBaggingTracker.put(item, isBagged);
	}
	
}
