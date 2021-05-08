package selfcheckout.software.controllers;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import selfcheckout.software.controllers.exceptions.*;

import java.math.BigDecimal;

import static org.junit.Assert.fail;

/**
 * A {@link SelfCheckoutStation} that acts as a test fixture for
 * {@link SelfCheckoutController} tests. Uses the test constants defined in
 * the {@link ControllerTestConstants} to initialize the station.
 */
public class BasicSelfCheckoutStation extends SelfCheckoutStation {

	public BasicSelfCheckoutStation() {
		super(
			ControllerTestConstants.CURRENCY,
			ControllerTestConstants.BANKNOTE_DENOMINATIONS,
			ControllerTestConstants.COIN_DENOMINATIONS,
			ControllerTestConstants.SCALE_MAXIMUM_WEIGHT,
			ControllerTestConstants.SCALE_SENSITIVITY
		);
	}

	public static void scanOneItemSuccessfully(SelfCheckoutController scc, ProductDatabasesWrapper dbWrapper, double weight) {
		// utility function to scan exactly one item successfully
		// which must be done before the item is bagged
		String validBarcodeString = ControllerTestConstants.VALID_BARCODE_STRING;
		Barcode validItemBarcode = new Barcode(validBarcodeString);
		try {
			dbWrapper.getProductByBarcode(validItemBarcode);
		} catch (NonexistentBarcodeException e) {
			fail("for this test, the Barcode must exist");
		}

		boolean successfulScan = false;

		// attempt to scan ten times. Verify that at least one scan succeeded.
		for (int i = 0; i < 10; i++) {
			try {
				scc.scanItem(validBarcodeString, weight);
			} catch (ItemScanningException e) {
				continue;
			}
			successfulScan = true;
			break;
		}

		if (!successfulScan) {
			fail("Scan was unsuccessful");
		}
	}

	public static void scanOneItemSuccessfully(SelfCheckoutController scc, ProductDatabasesWrapper dbWrapper) {
		BasicSelfCheckoutStation.scanOneItemSuccessfully(scc, dbWrapper, 0.001);
	}

	public static void bagOneItemSuccessfully(SelfCheckoutController scc, double weight) {
		// utility function to bag exactly one item successfully
		// which must be done immediately after scanning an item
		try {
			scc.bagLastItem(weight);
		} catch (ItemBaggingException e) {
			fail("Should not throw an exception when bagging an item");
		}
	}

	public static void bagOneItemSuccessfully(SelfCheckoutController scc) {
		BasicSelfCheckoutStation.bagOneItemSuccessfully(scc, 0.001);
	}

	public static void insertBanknoteSuccessfully(SelfCheckoutController scc, int banknoteValue) {
		for (int i = 0; i < 10; i++) {
			try {
				scc.insertBanknote(banknoteValue, ControllerTestConstants.CURRENCY);
			} catch (BanknoteRejectedException e) {
				// banknote was sporadically rejected, remove and then retry
				scc.removeDanglingBanknote();
				continue;
			} catch (StorageUnitFullException e) {
				fail("Storage unit full, not able to insert any more banknotes");
			}
			return;
		}
		fail("Could not insert a banknote");
	}

	public static void insertCoinSuccessfully(SelfCheckoutController scc, BigDecimal coinValue) {
		for (int i = 0; i < 10; i++) {
			try {
				scc.insertCoin(coinValue, ControllerTestConstants.CURRENCY);
			} catch (CoinRejectedException e) {
				// banknote was sporadically rejected, remove and then retry
				scc.emptyCoinTray();
				continue;
			} catch (StorageUnitFullException e) {
				fail("Storage unit full, not able to insert any more coins");
			}
			return;
		}
		fail("Could not insert a coin");
	}
}
