package selfcheckout.software.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;
import selfcheckout.software.controllers.exceptions.NonexistentPLUCodeException;

/** 
 * The PurchaseManager class holds the current state of the customer's transaction,
 * allowing you to easily interact with it (ex. calculate the current total, the current list of all scanned items, etc)
 * 
 */

public class PurchaseManager {

	// list of items which have been successfully added to the customer's transaction
	private Purchase purchaseList;
	// connection to interact with database
	private final ProductDatabasesWrapper productDatabase;
	private final ArrayList<Purchase> previousPurchase;

	// constructor
	public PurchaseManager(ProductDatabasesWrapper databaseWrapper) {
		this.productDatabase = databaseWrapper;	// an empty transaction list
		this.purchaseList = new Purchase();
		this.previousPurchase = new ArrayList<>();
	}

	/** Adds an item to the customer's list of purchases in the current transaction
	 * and updates the current transaction total
	 * 
	 * @param item The item to be added to the customer's transaction
	 */
	public void addItem(Item item) throws NoSuchItemException {
		// update the list of items which have been purchased
		if (item instanceof BarcodedItem) {
			BarcodedItem barcodeItem = (BarcodedItem) item;
			Barcode barcode = barcodeItem.getBarcode();
			// ensure product exists
			BarcodedProduct product = productDatabase.getProductByBarcode(barcode);
			int numAvailable = productDatabase.getInventoryByProduct(product);
			if (numAvailable == 0) {
				throw new NoSuchItemException("No inventory of that product");
			}
			productDatabase.setInventoryByProduct(product, numAvailable - 1);
			purchaseList.addItem(item);
		} else if (item instanceof PLUCodedItem) {
			PLUCodedItem pluItem = (PLUCodedItem) item;
			PriceLookupCode pluCode = pluItem.getPLUCode();
			// ensure product exists
			PLUCodedProduct product = productDatabase.getProductByPLUCode(pluCode);
			int numAvailable = productDatabase.getInventoryByProduct(product);
			if (numAvailable == 0) {
				throw new NoSuchItemException("No inventory of that product");
			}
			productDatabase.setInventoryByProduct(product, numAvailable - 1);
			purchaseList.addItem(item);
		} else if (item instanceof BagItem) {
			purchaseList.addItem(item);
		} else {
			throw new ControlSoftwareException("Item type not implemented in PurchaseManager");
		}
	}

	/** Calculates and fetches the current total of the transaction, useful
	 * for displaying the updated total for the customer 
	 * 
	 * @return 
	 * 		the current total of the transaction
	 */
	public BigDecimal getTotalPrice() {
		// the current total cost which the customer owes in the transaction
		BigDecimal total = new BigDecimal(0);

		ArrayList<Item> currentPurchases = purchaseList.getCurrentPurchases(); 

		for (Item item : currentPurchases) {
			total = total.add(this.getItemPrice(item));
		}

		return total;
	}
	
	public BigDecimal getItemPrice(Item item) {
		// handle items with barcodes
		if (item instanceof BarcodedItem) {
			BarcodedItem barcodedItem = (BarcodedItem) item;
			Barcode code = barcodedItem.getBarcode();

			BarcodedProduct product;
			// look up the price in the database
			try {
				product = productDatabase.getProductByBarcode(code);
			} catch (NonexistentBarcodeException e) {
				throw new ControlSoftwareException("a purchased BarcodedItem does not exist");
			}
			return product.getPrice().setScale(2, BigDecimal.ROUND_HALF_DOWN);
		} else if (item instanceof PLUCodedItem) {
			PLUCodedItem pluItem = (PLUCodedItem) item;
			PriceLookupCode code = pluItem.getPLUCode();
			PLUCodedProduct pluProduct;
			BigDecimal itemWeight;
			try {
				pluProduct = productDatabase.getProductByPLUCode(code);
			} catch (NonexistentPLUCodeException e) {
				throw new ControlSoftwareException("a purchased PLUCodedItem does not exist");
			}
			itemWeight = BigDecimal.valueOf(pluItem.getWeight());
			return pluProduct.getPrice().multiply(itemWeight).setScale(2, BigDecimal.ROUND_HALF_DOWN);
		} else if (!(item instanceof BagItem)) {
			// if the item is a bag, it is the customer's bag and doesn't
			// cost anything
			throw new ControlSoftwareException("Couldn't find price of item.");
		}
		return new BigDecimal("0.00");
	}
	
	public BigDecimal getTotalPriceOfPurchasedItems(ArrayList<Item> currentPurchases) {
		// the current total cost which the customer owes in the transaction
		BigDecimal total = new BigDecimal(0);

		for (Item item : currentPurchases) {
			total = total.add(this.getItemPrice(item));
		}

		return total;
	}

	public ArrayList<Item> getItems() {
		return this.purchaseList.getCurrentPurchases();
	}

	public ArrayList<Item> getItemsBagged() {
		ArrayList<Item> baggedPurchases = new ArrayList<>();
		
		for (Item item : purchaseList.getCurrentPurchases()) {
			if(purchaseList.isBagged(item)) {
				baggedPurchases.add(item);
			}
		}
		
		return baggedPurchases;
	}

	
	public Item getLastItem() {
		return this.getItems().get(this.getItems().size() - 1);
	}

	/** Retrieves the current list of items in the transaction
	 *
	 * @return 
	 * 		the list of items which have been scanned so far
	 */
	public ArrayList<Product> getProducts() {
		ArrayList<Product> scannedItems = new ArrayList<>();

		ArrayList<Item> currentPurchases = purchaseList.getCurrentPurchases(); 

		for (Item item : currentPurchases) {
			Product possibleProductToShow = this.getProductFromItem(item);
			if (possibleProductToShow != null) {
				scannedItems.add(possibleProductToShow);
			}
		}

		return scannedItems;
	}

	public Product getProductFromItem(Item item) {
		BarcodedProduct product;
		PLUCodedProduct pluProduct;

		if (item instanceof BarcodedItem) {
			BarcodedItem barcodedItem = (BarcodedItem) item;
			Barcode code = barcodedItem.getBarcode();
			// look up the price in the database
			try {
				product = productDatabase.getProductByBarcode(code);
			} catch (NonexistentBarcodeException e) {
				// this should never occur
				throw new ControlSoftwareException("Couldn't retrieve product");
			}
			return product;
		} else if (item instanceof PLUCodedItem) {
			PLUCodedItem pluItem = (PLUCodedItem) item;
			PriceLookupCode code = pluItem.getPLUCode();
			try {
				pluProduct = productDatabase.getProductByPLUCode(code);
			} catch (NonexistentPLUCodeException e) {
				throw new ControlSoftwareException("Couldn't retrieve product");
			}
			return pluProduct;
		} else if (!(item instanceof BagItem)) {
			// if the item is a bag, it is the customer's bag and
			// shouldn't show up in the product list
			throw new ControlSoftwareException("Couldn't retrieve a non-barcoded item.");
		}
		return null;
	}

	/**
	 * Calculate the total weight of the entire Purchase
	 * @return
	 * 		the total weight
	 */
	public double getTotalWeight() {
		double totalWeight = 0;
		for (Item item : purchaseList.getCurrentPurchases()) {
			if(purchaseList.isBagged(item)) {
				totalWeight += item.getWeight();
			}
		}
		return totalWeight;
	}

	public double getTotalBagWeight() {
		double totalBagWeight = 0;
		for (Item item : purchaseList.getCurrentPurchases()) {
			if (item instanceof BagItem) {
				totalBagWeight += item.getWeight();
			}
		}
		return totalBagWeight;
	}

	/**
	 * Remove the item that was last added to the list of scanned items
	 */
	public Item removeLastItem() {
		int lastItemIndex = this.purchaseList.getCurrentPurchases().size() - 1;
		return this.removeItem(lastItemIndex);
	}
	
	/**
	 * Removes an item from the purchase list based on index of the item
	 * @param itemIndex the item to be removed
	 *
	 */
	public Item removeItem(int itemIndex) {
		Item itemRemoved = purchaseList.removeItem(itemIndex);
		if (itemRemoved instanceof PLUCodedItem || itemRemoved instanceof BarcodedItem) {
			Product realProduct = this.getProductFromItem(itemRemoved);
			int inventory = this.productDatabase.getInventoryByProduct(realProduct);
			this.productDatabase.setInventoryByProduct(realProduct, inventory + 1);
		}
		return itemRemoved;
	}
	
	public ArrayList<Purchase> saveCurrentPurchase() {
		this.previousPurchase.add(purchaseList);
		this.purchaseList = new Purchase();

		return previousPurchase;	
	}

	/**
	 * Method to add items to the hashmap keeping track of whether an item was bagged or not
	 *
	 * @param item 	the item which was scanned
	 * @param bool	Boolean to see if the item was bagged (true if bagged, false if bagging skipped)
	 */
	public void addItemTrackBagging(Item item, boolean bool) {
		this.purchaseList.setItemTrackBagging(item, bool);
	}

	public Purchase getLastPurchase() {
		return this.previousPurchase.get(this.previousPurchase.size()-1);
	}

	public String getCustomerOrderSummary() {
		ArrayList<Item> transactionList = getItems();

		StringBuilder outputList = new StringBuilder();
		outputList.append("============================").append(System.lineSeparator())
			.append("Items in Virtual Cart:").append(System.lineSeparator());

		for (int itemNumber = 0; itemNumber < transactionList.size(); itemNumber++) {
			outputList.append(System.lineSeparator()).append(itemNumber + 1).append(": ");
			Item item = transactionList.get(itemNumber);
			if (item instanceof BarcodedItem) {
				BarcodedItem barcodedItem = (BarcodedItem) item;
				BarcodedProduct associatedProduct;
				try {
					associatedProduct = this.productDatabase.getProductByBarcode(barcodedItem.getBarcode());
				} catch (NoSuchItemException e) {
					throw new ControlSoftwareException("Could not find existing BarcodedProduct");
				}
				outputList.append(associatedProduct.getDescription()).append(System.lineSeparator());
				outputList.append("Price: $").append(this.getItemPrice(item));
			} else if (item instanceof PLUCodedItem) {
				PLUCodedItem pluCodedItem = (PLUCodedItem) item;
				PLUCodedProduct associatedProduct;
				try {
					associatedProduct = this.productDatabase.getProductByPLUCode(pluCodedItem.getPLUCode());
				} catch (NoSuchItemException e) {
					throw new ControlSoftwareException("Could not find existing BarcodedProduct");
				}
				outputList.append(associatedProduct.getDescription()).append(System.lineSeparator());
				outputList.append("Price: $").append(this.getItemPrice(item));
			} else {
				outputList.append("Bag").append(System.lineSeparator());
				outputList.append("Price: $").append(this.getItemPrice(item));
			}
			outputList.append(System.lineSeparator());
		}

		outputList.append(System.lineSeparator());
		// print the total of the purchase so far
		BigDecimal total = getTotalPrice();
		outputList.append("Transaction total: $").append(total).append(System.lineSeparator());
		outputList.append("============================").append(System.lineSeparator());
		return outputList.toString();
	}
}
