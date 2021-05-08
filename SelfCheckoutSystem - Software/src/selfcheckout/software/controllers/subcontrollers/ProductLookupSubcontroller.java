package selfcheckout.software.controllers.subcontrollers;

import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.exceptions.*;

/*
 * An attendant may lookup a barcoded product by its barcode
 * An attendant may lookup a Price lookup coded product by its price lookup code
 * An attendant may lookup a product by its description
 */
public class ProductLookupSubcontroller {
	
	private final ProductDatabasesWrapper productDatabasesWrapper;
	public static final String SEARCH_STRING_START = "Search by Product Description Results: \n";
	private static final String NO_RESULTS_STRING = "No products found";
	private static final String INVALID_DESCRIPTION_ERROR_MSG = "An invalid description was given";
	//Constructor
	public ProductLookupSubcontroller(ProductDatabasesWrapper productDatabasesWrapper) {
		this.productDatabasesWrapper = productDatabasesWrapper;
		
	}
	
	/**
	 * Attendant searches using the barcode of a BarcodedProduct
	 * 
	 * @param code
	 * 					the search string that is inputed by the attendant
	 * @return String 
	 * 					a listing of the products, their price, and inventory that match the search
	 */
	public String lookupBarcodedProduct(String code) {
		Map<BarcodedProduct,Integer> searchedProducts = new HashMap<>();
		
		 if(code == null) throw new ControlSoftwareException(INVALID_DESCRIPTION_ERROR_MSG);
		//Find and return the product(s) and their inventories which are a partial match of the search parameters 
		for(Barcode productBarcode: productDatabasesWrapper.getAllBarcodes()) {
			String productCode = productBarcode.toString();
			if(productCode.startsWith(code)) {
				BarcodedProduct product;
				try {
					product = productDatabasesWrapper.getProductByBarcode(productBarcode);
				} catch (NonexistentBarcodeException e) {
					// this line is unreachable unless the software is misconfigured
					throw new ControlSoftwareException("Item removed during inventory search");
				}
				int inventory = productDatabasesWrapper.getInventoryByProduct(product);
				searchedProducts.put(product, inventory);
			}
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Barcoded Products Search Results: \n");
		buildBarcodeProductSearchResultString(stringBuilder, searchedProducts);
		
		return stringBuilder.toString();
		
		
	}
	
	/**
	 * This function builds up the string for the search results of Barcoded product lookup.
	 * 
	 * @param stringBuilder
	 * 							the string builder being used to create the search results string
	 * @param searchedProducts
	 * 							a Map containing the Barcoded products and their inventories that match the search parameters
	 * 
	 */
	private void buildBarcodeProductSearchResultString(StringBuilder stringBuilder, Map<BarcodedProduct,Integer> searchedProducts) {
		if (searchedProducts.keySet().isEmpty()) {
			stringBuilder.append(NO_RESULTS_STRING);
			return;
		}
		for(BarcodedProduct product:searchedProducts.keySet()) {
			stringBuilder.append(product.getDescription())
				.append(" with barcode ").append(product.getBarcode())
				.append(" costs $").append(product.getPrice())
			.append(" and has ").append(searchedProducts.get(product)).append(" units in inventory \n");
		}
	}
	
	/**
	 * Attendant searches using the price lookup code of a PLUCodedProduct
	 * 
	 * @param code
	 * 					the search string that is inputed by the attendant
	 * @return String 
	 * 					a listing of the products, their price, and inventory that match the search
	 */
	public String lookupPLUCodedProduct(String code) {
		 Map<PLUCodedProduct,Integer> searchedProducts = new HashMap<>();

		if(code == null) throw new ControlSoftwareException(INVALID_DESCRIPTION_ERROR_MSG);
		//Find and return the product(s) and their inventories which are a partial match of the search parameters 
		for(PriceLookupCode productPLUcode: productDatabasesWrapper.getAllPLUCodes()) {
			String productCode = productPLUcode.toString();
			if(productCode.startsWith(code)) {
				PLUCodedProduct product;
				try {
					product = productDatabasesWrapper.getProductByPLUCode(productPLUcode);
				} catch (NonexistentPLUCodeException e) {
					// this line is unreachable unless the software is misconfigured
					throw new ControlSoftwareException("Item which was just found no longer exists");
				}
				int inventory = productDatabasesWrapper.getInventoryByProduct(product);
				searchedProducts.put(product, inventory);
			}
		}
		
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Price Lookup Coded Products Search Results: \n");
		
		buildPLUCodedProductSearchResultString(stringBuilder, searchedProducts);

		return stringBuilder.toString();
		
	}
	
	/**
	 * This function builds up the string for the search results of PLUCoded product lookup.
	 * 
	 * @param stringBuilder
	 * 							the string builder being used to create the search results string
	 * @param searchedProducts
	 * 							a Map containing the PLUCoded products and their inventories that match the search parameters
	 * 
	 */
	private void buildPLUCodedProductSearchResultString(StringBuilder stringBuilder, Map<PLUCodedProduct,Integer> searchedProducts) {
		if (searchedProducts.keySet().isEmpty()) {
			stringBuilder.append(NO_RESULTS_STRING);
			return;
		}
		for(PLUCodedProduct product:searchedProducts.keySet()) {
			stringBuilder.append(product.getDescription())
				.append(" with PLU code ").append(product.getPLUCode())
				.append(" costs $").append(product.getPrice()).append(" per kilogram and has ")
				.append(searchedProducts.get(product)).append(" units in inventory \n");
		}
	}
	
	/**
	 * Attendant looks up a BarcodedProduct by its description
	 * 
	 * @param description
	 * 						the description of the product that is being searched for
	 * @return Map<BarcodedProduct, Integer>
	 * 					a map of the barcoded product and its inventory
	 * 
	 */
	private Map<BarcodedProduct, Integer> internalLookupBarcodedProductByDescription(String description) {
		if(description == null) throw new ControlSoftwareException(INVALID_DESCRIPTION_ERROR_MSG);

		Map<BarcodedProduct,Integer> searchedProducts = new HashMap<>();
		 
		 //Checks all products in the barcoded product database to see if the match fully or partially with the description
		 for(BarcodedProduct barcodeProduct: productDatabasesWrapper.getAllBarcodedProducts()) {
			if(barcodeProduct.getDescription().toLowerCase().contains(description.toLowerCase())) {
				int inventory = productDatabasesWrapper.getInventoryByProduct(barcodeProduct);
				searchedProducts.put(barcodeProduct, inventory);
			}
		 }
		 
		 return searchedProducts;
		
	}

	public String lookupBarcodedProductByDescription(String description) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(SEARCH_STRING_START);
		Map<BarcodedProduct, Integer> barcodeProductSearchResults = internalLookupBarcodedProductByDescription(description);
		buildBarcodeProductSearchResultString(stringBuilder, barcodeProductSearchResults);
		return stringBuilder.toString();
	}
	
	/**
	 * Attendant looks up a PLUCodedProduct by its description
	 * 
	 * @param description
	 * 						the description of the product that is being searched for
	 * @return Map<BarcodedProduct, Integer>
	 * 						a map of the PLUCoded product and its inventory
	 */
	private Map<PLUCodedProduct, Integer> internalLookupPLUCodedProductByDescription(String description) {
		if(description == null) throw new ControlSoftwareException(INVALID_DESCRIPTION_ERROR_MSG);

		Map<PLUCodedProduct,Integer> searchedProducts = new HashMap<>();
		 
		//Checks all products in the pluProduct database to see if the match fully or partially with the description
		 for(PLUCodedProduct pluProduct:  productDatabasesWrapper.getAllPLUCodedProducts()) {
			if(pluProduct.getDescription().toLowerCase().contains(description.toLowerCase())) {
				int inventory = productDatabasesWrapper.getInventoryByProduct(pluProduct);
				searchedProducts.put(pluProduct, inventory);
			}
		 }
		 
		 return searchedProducts;
		
	}

	public String lookupPLUCodedProductByDescription(String description) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(SEARCH_STRING_START);
		Map<PLUCodedProduct, Integer> pluCodedProductSearchResults = internalLookupPLUCodedProductByDescription(description);
		buildPLUCodedProductSearchResultString(stringBuilder, pluCodedProductSearchResults);
		return stringBuilder.toString();
	}
	
	public String lookupProductByDescription(String description) {
		
		if(description == null) throw new ControlSoftwareException(INVALID_DESCRIPTION_ERROR_MSG);
		 
		Map<BarcodedProduct, Integer> barcodeProductSearchResults;
		Map<PLUCodedProduct, Integer> pluCodedProductSearchResults;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(SEARCH_STRING_START);

		barcodeProductSearchResults = internalLookupBarcodedProductByDescription(description);
		pluCodedProductSearchResults = internalLookupPLUCodedProductByDescription(description);

		if (barcodeProductSearchResults.keySet().isEmpty() && pluCodedProductSearchResults.keySet().isEmpty()) {
			stringBuilder.append(NO_RESULTS_STRING);
		} else {
			if (!barcodeProductSearchResults.keySet().isEmpty()) {
				buildBarcodeProductSearchResultString(stringBuilder, barcodeProductSearchResults);
			}
			if (!pluCodedProductSearchResults.keySet().isEmpty()) {
				buildPLUCodedProductSearchResultString(stringBuilder, pluCodedProductSearchResults);
			}
		}
		return stringBuilder.toString();
		
	}

}
