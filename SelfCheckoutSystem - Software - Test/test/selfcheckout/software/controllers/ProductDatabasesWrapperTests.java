package selfcheckout.software.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.external.ProductDatabases;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;
import selfcheckout.software.controllers.exceptions.NonexistentPLUCodeException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ProductDatabasesWrapperTests {

	private ProductDatabasesWrapper dbWrapper;

	@Before
	public void setup() {
		ProductDatabasesWrapper.initializeDatabases();
		this.dbWrapper = new ProductDatabasesWrapper();
	}

	@After
	public void teardown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test
	public void testGetAllBarcodesNone() {
		ProductDatabasesWrapper.resetDatabases();
		assertEquals(this.dbWrapper.getAllBarcodes().size(), 0);
	}	

	@Test
	public void testGetAllBarcodesSingle() {
		ProductDatabasesWrapper.resetDatabases();

		Barcode barcode = new Barcode("739252945192046282");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		BarcodedProduct newProduct = new BarcodedProduct(barcode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);

		assertEquals(this.dbWrapper.getAllBarcodes().size(), 1);
		assertEquals(this.dbWrapper.getAllBarcodes().get(0), barcode);
	}

	@Test
	public void testGetAllBarcodesMultiple() {
		int originalSize = ProductDatabases.BARCODED_PRODUCT_DATABASE.size();

		Barcode barcode = new Barcode("739252945192046282");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		BarcodedProduct newProduct = new BarcodedProduct(barcode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);

		assertEquals(this.dbWrapper.getAllBarcodes().size(), originalSize + 1);
	}

	@Test
	public void testGetAllBarcodedProductsNone() {
		ProductDatabasesWrapper.resetDatabases();
		assertEquals(this.dbWrapper.getAllBarcodedProducts().size(), 0);
	}

	@Test
	public void testGetAllBarcodedProductsSingle() {
		ProductDatabasesWrapper.resetDatabases();

		Barcode barcode = new Barcode("739252945192046282");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		BarcodedProduct newProduct = new BarcodedProduct(barcode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);

		assertEquals(this.dbWrapper.getAllBarcodedProducts().size(), 1);
		assertEquals(this.dbWrapper.getAllBarcodedProducts().get(0), newProduct);
	}

	@Test
	public void testGetAllBarcodedProductsMultiple() {
		int originalSize = ProductDatabases.BARCODED_PRODUCT_DATABASE.size();

		Barcode barcode = new Barcode("739252945192046282");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		BarcodedProduct newProduct = new BarcodedProduct(barcode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);

		assertEquals(this.dbWrapper.getAllBarcodedProducts().size(), originalSize + 1);
	}

	@Test
	public void testGetCostByBarcode() {
		Barcode barcode = new Barcode("739252945192046282");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		BarcodedProduct newProduct = new BarcodedProduct(barcode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);
		try {
			BigDecimal cost = this.dbWrapper.getCostByBarcode(barcode);
			assertEquals(cost, price);
		} catch (NonexistentBarcodeException e) {
			fail("Product should exist");
		}
	}

	@Test (expected = NonexistentBarcodeException.class)
	public void testGetCostByBarcodeInvalid() throws NonexistentBarcodeException {
		Barcode barcode = new Barcode("739252945192046282");
		this.dbWrapper.getCostByBarcode(barcode);
	}
	
	@Test
	public void testGetProductByBarcode() {
		Barcode barcode = new Barcode("739252945192046282");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		BarcodedProduct newProduct = new BarcodedProduct(barcode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);
		
		try {
			BarcodedProduct product = this.dbWrapper.getProductByBarcode(barcode);
			assertEquals(product, newProduct);
		} catch (NonexistentBarcodeException e) {
			fail("Product should exist");
		}
	}

	@Test (expected = NonexistentBarcodeException.class)
	public void testGetProductByBarcodeInvalid() throws NonexistentBarcodeException {
		Barcode barcode = new Barcode("739252945192046282");
		this.dbWrapper.getProductByBarcode(barcode);
	}

	@Test
	public void testGetAllPLUCodedProductsNone() {
		ProductDatabasesWrapper.resetDatabases();
		assertEquals(this.dbWrapper.getAllPLUCodedProducts().size(), 0);
	}

	@Test
	public void testGetAllPLUCodedProductsSingle() {
		ProductDatabasesWrapper.resetDatabases();

		PriceLookupCode pluCode = new PriceLookupCode("25437");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		PLUCodedProduct newProduct = new PLUCodedProduct(pluCode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);

		assertEquals(this.dbWrapper.getAllPLUCodedProducts().size(), 1);
		assertEquals(this.dbWrapper.getAllPLUCodedProducts().get(0), newProduct);
	}

	@Test
	public void testGetAllPLUCodedProductsMultiple() {
		int originalSize = ProductDatabases.PLU_PRODUCT_DATABASE.size();

		PriceLookupCode pluCode = new PriceLookupCode("25437");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		PLUCodedProduct newProduct = new PLUCodedProduct(pluCode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);

		assertEquals(this.dbWrapper.getAllPLUCodedProducts().size(), originalSize + 1);
	}

	@Test
	public void testGetCostByPLUCode() {
		PriceLookupCode pluCode = new PriceLookupCode("25437");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		PLUCodedProduct newProduct = new PLUCodedProduct(pluCode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);
		try {
			BigDecimal cost = this.dbWrapper.getCostByPLUCode(pluCode);
			assertEquals(cost, price);
		} catch (NonexistentPLUCodeException e) {
			fail("Product should exist");
		}
	}

	@Test (expected = NonexistentPLUCodeException.class)
	public void testGetCostByPLUCodeInvalid() throws NonexistentPLUCodeException {
		PriceLookupCode pluCode = new PriceLookupCode("25437");
		this.dbWrapper.getCostByPLUCode(pluCode);
	}
	
	@Test
	public void testGetProductByPLUCode() {
		PriceLookupCode pluCode = new PriceLookupCode("25437");
		String description = "product description";
		BigDecimal price = new BigDecimal("10.00");
		PLUCodedProduct newProduct = new PLUCodedProduct(pluCode, description, price);
		this.dbWrapper.addDatabaseItem(newProduct);
		
		try {
			PLUCodedProduct product = this.dbWrapper.getProductByPLUCode(pluCode);
			assertEquals(product, newProduct);
		} catch (NonexistentPLUCodeException e) {
			fail("Product should exist");
		}
	}

	@Test (expected = NonexistentPLUCodeException.class)
	public void testGetProductByPLUCodeInvalid() throws NonexistentPLUCodeException {
		PriceLookupCode pluCode = new PriceLookupCode("25437");
		this.dbWrapper.getProductByPLUCode(pluCode);
	}
}
