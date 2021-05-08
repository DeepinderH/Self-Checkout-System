package selfcheckout.software.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;
import selfcheckout.software.controllers.exceptions.ItemScanningException;

import static org.junit.Assert.fail;

public class ScanItemTest {

	private ProductDatabasesWrapper dbWrapper;
	private SelfCheckoutController scc;

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

	private static final int NUM_ITERATIONS = 10;

	@Test
	public void testNoScanException() {
		String validBarcodeString = "23578";
		Barcode validItemBarcode = new Barcode(validBarcodeString);
		try {
			this.dbWrapper.getProductByBarcode(validItemBarcode);
		} catch (NonexistentBarcodeException e) {
			fail("for this test, the Barcode must exist");
		}

		// attempt to scan ten times. Verify that at least one scan succeeded.
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.scc.scanItem(validBarcodeString, 1.0);
			} catch (ItemScanningException e) {
				continue;
			}
			// successfully scanned barcode, just return
			return;
		}
		fail("Unable to scan item");
	}

	@Test(expected = ItemScanningException.class)
	public void testScanException() throws ItemScanningException {
		// invalid barcode, expect to throw ScanException
		this.scc.scanItem("ABC", 1.0);
	}
}
