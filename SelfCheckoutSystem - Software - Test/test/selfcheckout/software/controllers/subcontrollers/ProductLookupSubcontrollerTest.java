package selfcheckout.software.controllers.subcontrollers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.exceptions.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class ProductLookupSubcontrollerTest {
	public ProductDatabasesWrapper databaseWrapper;
	public ProductLookupSubcontroller lookupSubcontroller;
	
	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		databaseWrapper = new ProductDatabasesWrapper();
		lookupSubcontroller = new ProductLookupSubcontroller(databaseWrapper);
	}
	
	@Test
	public void testMatchingBarcodedProductLookup() {
		String results = lookupSubcontroller.lookupBarcodedProduct("23578");
		assertTrue(results.contains("Bread (TM) with barcode 23578 costs $10.00 and has 100 units in inventory \n"));
	}
	
	@Test
	public void testPartialMatchingBarcodedProductLookup() {
		String results = lookupSubcontroller.lookupBarcodedProduct("2");
		assertTrue(results.contains("Bread (TM) with barcode 23578 costs $10.00 and has 100 units in inventory \n"));
		assertTrue(results.contains("Milk: The Drink with barcode 29861259 costs $15.00 and has 100 units in inventory \n"));
	}
	
	@Test
	public void testInvalidBarcodedProductLookup() {
		String results;
		results = lookupSubcontroller.lookupBarcodedProduct("@a8*");
		assertEquals("Barcoded Products Search Results: \nNo products found", results);
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testNullBarcodeProductLookup() {
		lookupSubcontroller.lookupBarcodedProduct(null);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testNullBarcodeProductDescription() {
		lookupSubcontroller.lookupBarcodedProductByDescription(null);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testNullPLUProductDescription() {
		lookupSubcontroller.lookupPLUCodedProductByDescription(null);
	}
	
	@Test
	public void testMatchingPLUCodedProductLookup() {
		String results = lookupSubcontroller.lookupPLUCodedProduct("4011");
		assertTrue(results.contains("Cavendish Bananas with PLU code 4011 costs $1.00 per kilogram and has 100 units in inventory \n"));
	}

	@Test
	public void testPartialMatchingPLUCodedProductLookup() {
		String results = lookupSubcontroller.lookupPLUCodedProduct("4");
		assertTrue(results.contains("Fuji Apple with PLU code 4131 costs $3.00 per kilogram and has 100 units in inventory \n"));
		assertTrue(results.contains("Cavendish Bananas with PLU code 4011 costs $1.00 per kilogram and has 100 units in inventory \n"));
	}
	
	@Test
	public void testFullValidPLUCodedProductLookup() {
		String results;
		results = lookupSubcontroller.lookupPLUCodedProduct("413156");
		assertEquals("Price Lookup Coded Products Search Results: \nNo products found", results);
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testNullPLUCodedProductLookup() {
		lookupSubcontroller.lookupPLUCodedProduct(null);
	}
	
	@Test
	public void testMatchingDescriptionProductLookup() {
		String results = lookupSubcontroller.lookupProductByDescription("Cavendish Bananas");
		assertTrue(results.contains("Cavendish Bananas with PLU code 4011 costs $1.00 per kilogram and has 100 units in inventory \n"));
	}
	
	@Test
	public void testPartialMatchingDescriptionProductLookup() {
		String results = lookupSubcontroller.lookupProductByDescription("a");
		assertTrue(results.contains("Bread (TM) with barcode 23578 costs $10.00 and has 100 units in inventory \n"));
		assertTrue(results.contains("Spam! with barcode 791666190 costs $20.00 and has 100 units in inventory \n"));
		assertTrue(results.contains("Fuji Apple with PLU code 4131 costs $3.00 per kilogram and has 100 units in inventory \n"));
		assertTrue(results.contains("Cavendish Bananas with PLU code 4011 costs $1.00 per kilogram and has 100 units in inventory \n"));
		assertTrue(results.contains("Navel Oranges with PLU code 3107 costs $2.00 per kilogram and has 100 units in inventory \n"));
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testNullDescriptionProductLookup() {
		lookupSubcontroller.lookupProductByDescription(null);
	}

	@Test
	public void testNoResultsProductLookup() {
		String results = lookupSubcontroller.lookupProductByDescription("0F%sL7>");
		assertEquals(results, "Search by Product Description Results: \nNo products found");
	}


	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}

}
