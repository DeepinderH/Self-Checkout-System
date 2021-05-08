package selfcheckout.software.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;
import selfcheckout.software.controllers.exceptions.NonexistentPLUCodeException;

/**
 *  Serves as a database for barcoded items, keeping a record of the name, barcode,
 *  weight, and cost associated with all purchasable items. 
 *
 */

public class ProductDatabasesWrapper {

	/**
	 *  Constructor
	 */
	public ProductDatabasesWrapper() {
	}

	public static void initializeDatabases() {
		BarcodedProduct breadTM = new BarcodedProduct(new Barcode("23578"), "Bread (TM)", new BigDecimal("10.00"));
		BarcodedProduct milkTheDrink = new BarcodedProduct(new Barcode("29861259"), "Milk: The Drink", new BigDecimal("15.00"));
		BarcodedProduct spam = new BarcodedProduct(new Barcode("791666190"), "Spam!", new BigDecimal("20.00"));

		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(breadTM.getBarcode(), breadTM);
		ProductDatabases.INVENTORY.put(breadTM, 100);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(milkTheDrink.getBarcode(), milkTheDrink);
		ProductDatabases.INVENTORY.put(milkTheDrink, 100);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(spam.getBarcode(), spam);
		ProductDatabases.INVENTORY.put(spam, 100);

		PLUCodedProduct apple = new PLUCodedProduct(new PriceLookupCode("4131"), "Fuji Apple", new BigDecimal("3.00"));
		PLUCodedProduct bananas = new PLUCodedProduct(new PriceLookupCode("4011"), "Cavendish Bananas", new BigDecimal("1.00"));
		PLUCodedProduct orange = new PLUCodedProduct(new PriceLookupCode("3107"), "Navel Oranges", new BigDecimal("2.00"));

		ProductDatabases.PLU_PRODUCT_DATABASE.put(apple.getPLUCode(), apple);
		ProductDatabases.INVENTORY.put(apple, 100);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(bananas.getPLUCode(), bananas);
		ProductDatabases.INVENTORY.put(bananas, 100);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(orange.getPLUCode(), orange);	
		ProductDatabases.INVENTORY.put(orange, 100);
	}

	public static void resetDatabases() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
		ProductDatabases.PLU_PRODUCT_DATABASE.clear();
		ProductDatabases.INVENTORY.clear();
	}

	/**
	 * Returns a list of the barcodes of all possible items that could be purchased by the customer
	 */
	public ArrayList<Barcode> getAllBarcodes() {
		return new ArrayList<>(ProductDatabases.BARCODED_PRODUCT_DATABASE.keySet());
	}

	/**
	 * Returns a list of all possible items that could be purchased by the customer
	 */
	public ArrayList<BarcodedProduct> getAllBarcodedProducts() {
		return new ArrayList<>(ProductDatabases.BARCODED_PRODUCT_DATABASE.values());
	}

	/**
	 * Add an item to the list of purchasable items
	 * @param product the BarcodedProduct to be added to the inventory list
	 */
	public void addDatabaseItem(BarcodedProduct product) {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(product.getBarcode(), product);
		ProductDatabases.INVENTORY.put(product, 1);
	}

	public BigDecimal getCostByBarcode(Barcode barcode) throws NonexistentBarcodeException {
		if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
			return product.getPrice();
		} else {
			throw new NonexistentBarcodeException("That barcode does not match a current product.");
		}
	}

	public BarcodedProduct getProductByBarcode(Barcode barcode) throws NonexistentBarcodeException {
		if (ProductDatabases.BARCODED_PRODUCT_DATABASE.containsKey(barcode)) {
			return ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		} else {
			throw new NonexistentBarcodeException("That barcode does not match a current product.");
		}
	}
	
	/**
	 * Returns a list of the PLU codes all possible PLU coded items that could be purchased by the customer
	 */
	public ArrayList<PriceLookupCode> getAllPLUCodes() {
		return new ArrayList<>(ProductDatabases.PLU_PRODUCT_DATABASE.keySet());
	}
	
	/**
	 * Returns a list of all possible PLU coded items that could be purchased by the customer
	 */
	public ArrayList<PLUCodedProduct> getAllPLUCodedProducts() {
		return new ArrayList<>(ProductDatabases.PLU_PRODUCT_DATABASE.values());
	}

	/**
	 * Add an item to the list of purchasable items
	 * @param product the PLUCodedProduct to be added to the inventory list
	 */
	public void addDatabaseItem(PLUCodedProduct product) {
		ProductDatabases.PLU_PRODUCT_DATABASE.put(product.getPLUCode(), product);
	}

	public BigDecimal getCostByPLUCode(PriceLookupCode pluCode) throws NonexistentPLUCodeException {
		if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(pluCode)) {
			PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode);
			return product.getPrice();
		} else {
			throw new NonexistentPLUCodeException("That PLU code does not match a current product.");
		}
	}

	public PLUCodedProduct getProductByPLUCode(PriceLookupCode pluCode) throws NonexistentPLUCodeException {
		if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(pluCode)) {
			return ProductDatabases.PLU_PRODUCT_DATABASE.get(pluCode);
		} else {
			throw new NonexistentPLUCodeException("That PLU code does not match a current product.");
		}
	}
	
	/**
	 * get the inventory of a given product
	 */
	public int getInventoryByProduct(Product product) {
		return ProductDatabases.INVENTORY.get(product);
	}

	public void setInventoryByProduct(Product product, int numItems) {
		ProductDatabases.INVENTORY.put(product, numItems);
	}
}
