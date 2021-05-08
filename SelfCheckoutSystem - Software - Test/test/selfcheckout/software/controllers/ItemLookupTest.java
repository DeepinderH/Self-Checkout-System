package selfcheckout.software.controllers;

import static org.junit.Assert.*;
import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;
import selfcheckout.software.controllers.exceptions.NonexistentPLUCodeException;

public class ItemLookupTest {
	
	private ProductDatabasesWrapper dbWrapper;
	private SelfCheckoutController scc;

	public static final String FULL_NO_RESULTS_STRING = "Search by Product Description Results: \nNo products found";
	public static final String BARCODE_FULL_NO_RESULTS_STRING = "Barcoded Products Search Results: \nNo products found";
	public static final String PLU_FULL_NO_RESULTS_STRING = "Price Lookup Coded Products Search Results: \nNo products found";


	@Before
	public void setup() {
		SelfCheckoutStation station = new BasicSelfCheckoutStation();
		ProductDatabasesWrapper.initializeDatabases();
		this.dbWrapper = new ProductDatabasesWrapper();
		CardIssuer cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);
		MembershipDatabaseWrapper members = new MembershipDatabaseWrapper();
		GiftCardDatabaseWrapper giftCardDatabaseWrapper = new GiftCardDatabaseWrapper();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new SelfCheckoutController(
			station, this.dbWrapper, cardIssuer, members,
			giftCardDatabaseWrapper, attendantDatabaseWrapper);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant credentials should be valid during setup");
		}
	}

	@After
	public void teardown() {
		ProductDatabasesWrapper.resetDatabases();
	}
	
	@Test
	public void testlookUpBarcodedProductInvalidInput() {
		String result = this.scc.lookUpBarcodedProduct("ABC");
		assertEquals(result, BARCODE_FULL_NO_RESULTS_STRING);
	}

	@Test
	public void testlookUpBarcodedProductInvalidBarcode() {
		String result = this.scc.lookUpBarcodedProduct("123456");
		assertEquals(result, BARCODE_FULL_NO_RESULTS_STRING);
	}

	@Test
	public void testlookUpBarcodedProductValidBarcode() {
		try {
			BarcodedProduct product = this.dbWrapper.getProductByBarcode(ControllerTestConstants.VALID_BARCODE);
			BigDecimal price = product.getPrice();
			String description = product.getDescription();
			String expectedResult = "Barcoded Products Search Results: \n" + description +
				" with barcode " + ControllerTestConstants.VALID_BARCODE_STRING +
				" costs $" + price + " and has 100 units in inventory \n";
			assertEquals(expectedResult, this.scc.lookUpBarcodedProduct(ControllerTestConstants.VALID_BARCODE_STRING));
		} catch (NonexistentBarcodeException e) {
			fail("Product should exist");
		}
	}

	@Test
	public void testlookUpPLUCodedProductInvalidInput() {
		String result = this.scc.lookUpPLUProduct("ABC");
		assertEquals(result, PLU_FULL_NO_RESULTS_STRING);
	}

	@Test
	public void testlookUpPLUProductInvalidBarcode() {
		String result = this.scc.lookUpPLUProduct("63605");
		assertEquals(result, PLU_FULL_NO_RESULTS_STRING);
	}
	
	@Test
	public void testlookUpPLUProductValidPLUCode() {
		try {
			PLUCodedProduct product = this.dbWrapper.getProductByPLUCode(ControllerTestConstants.VALID_PLUCODE);
			BigDecimal price = product.getPrice();
			String description = product.getDescription();
			String expectedResult = "Price Lookup Coded Products Search Results: \n" + description
				+ " with PLU code " + ControllerTestConstants.VALID_PLUCODE_STRING +
				" costs $" + price + " per kilogram and has 100 units in inventory \n";
			assertEquals(expectedResult, this.scc.lookUpPLUProduct(ControllerTestConstants.VALID_PLUCODE_STRING));
		} catch (NonexistentPLUCodeException e) {
			fail("Product should exist");
		}
	}

	@Test
	public void testLookUpBarcodedProductDescriptionInvalidInput() {
		String result = this.scc.lookUpAllBarcodedProductsByDescription("Apple");
		assertEquals(result, FULL_NO_RESULTS_STRING);
	}

	@Test
	public void testLookUpValidDescriptionBarcodedProduct() {
		String result = this.scc.lookUpAllBarcodedProductsByDescription("Bread");
		assertNotEquals(result, FULL_NO_RESULTS_STRING);
	}

	@Test
	public void testLookUpPLUProductDescriptionInvalidInput() {
		String result = this.scc.lookUpAllPLUProductsByDescription("Bread");
		assertEquals(result, FULL_NO_RESULTS_STRING);
	}

	@Test
	public void testLookUpValidDescriptionPLUProduct() {
		String result = this.scc.lookUpAllPLUProductsByDescription("Apple");
		assertNotEquals(result, FULL_NO_RESULTS_STRING);
	}
}
