package selfcheckout.software.controllers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.external.CardIssuer;

import selfcheckout.software.controllers.exceptions.*;

public class FinishOrderTests {
	
	private BasicSelfCheckoutStation station;
	private SelfCheckoutController scc;
	private ProductDatabasesWrapper dbWrapper = new ProductDatabasesWrapper();

	@Before
	public void setUp() {
		this.station = new BasicSelfCheckoutStation();
		ProductDatabasesWrapper.initializeDatabases();
		CardIssuer cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);
		MembershipDatabaseWrapper members = new MembershipDatabaseWrapper();
		GiftCardDatabaseWrapper giftCardDatabaseWrapper = new GiftCardDatabaseWrapper();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new SelfCheckoutController(
			this.station, this.dbWrapper, cardIssuer, members,
			giftCardDatabaseWrapper, attendantDatabaseWrapper);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant credentials should be valid during setup");
		}
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, dbWrapper);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);
		this.scc.goToOrderPaymentState();
	}

	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	private void insertBanknoteSuccessfully(int banknoteValue) {
		for (int i = 0; i < 10; i++) {
			try {
				this.scc.insertBanknote(banknoteValue, ControllerTestConstants.CURRENCY);
			} catch (BanknoteRejectedException e) {
				// banknote was sporadically rejected, remove and then retry
				this.scc.removeDanglingBanknote();
				continue;
			} catch (StorageUnitFullException e) {
				fail("Storage unit full, not able to insert any more banknotes");
			}
			return;
		}
		fail("Could not insert a banknote");
	}

	@Test(expected = ControlSoftwareException.class)
	public void testFinishOrderIncorrectState() {
		try {
			this.scc.goToItemAdditionState();
			this.scc.finishOrder();
		} catch (OrderIncompleteException aOrderIncompleteException) {
			fail("OrderIncompleteException was thrown");
		}
	}
	
	@Test(expected = OrderIncompleteException.class)
	public void testFinishOrderPaymentException() throws OrderIncompleteException, BanknoteRejectedException, StorageUnitFullException {
		this.scc.goToOrderPaymentState();
		this.insertBanknoteSuccessfully(5);
		this.scc.finishOrder();
	}
	
	@Test(expected = OrderIncompleteException.class)
	public void testScanningScaleWeightChange() throws BanknoteRejectedException, StorageUnitFullException, OrderIncompleteException {
		Item lightItem = new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 2.0);
		this.station.scale.add(lightItem);
		this.insertBanknoteSuccessfully(20);
		this.scc.finishOrder();
	}

	@Test(expected = OrderIncompleteException.class)
	public void testScanningScaleOverloadException() throws BanknoteRejectedException, StorageUnitFullException, OrderIncompleteException {
		Item heavyItem = new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 100.0);
		this.station.scale.add(heavyItem);
		this.insertBanknoteSuccessfully(20);
		this.scc.finishOrder();
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testBaggingAreaWeightChange() throws OrderIncompleteException, BanknoteRejectedException, StorageUnitFullException {
		this.station.baggingArea.add(new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 1.0));
		this.insertBanknoteSuccessfully(10);
		this.scc.finishOrder();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testBaggingAreaWeightOverload() throws OrderIncompleteException, BanknoteRejectedException, StorageUnitFullException {
		this.station.baggingArea.add(new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 100.0));
		this.insertBanknoteSuccessfully(10);
		this.scc.finishOrder();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testDispenseChangeFailure() throws OrderIncompleteException, BanknoteRejectedException, StorageUnitFullException {
		for (CoinDispenser dispenser : this.station.coinDispensers.values()) {
			dispenser.unload();
		}
		for (BanknoteDispenser dispenser : this.station.banknoteDispensers.values()) {
			dispenser.unload();
		}
		this.insertBanknoteSuccessfully(100);
		// no change left to give, should throw ControlSoftwareException
		this.scc.finishOrder();
	}
	
	@Test
	public void testFinishOrderSuccessful() throws ItemScanningException, OrderIncompleteException, BanknoteRejectedException, StorageUnitFullException {
		this.insertBanknoteSuccessfully(10);
		this.scc.finishOrder();
		assertEquals(this.scc.getControllerStateEnumStatus(), ControllerStateEnum.FINISHED_PAYMENT);
		assertTrue(this.scc.getCurrentProducts().isEmpty());
	}
	
	@Test
	public void testRemovePurchasedItemsWithItems() throws OrderIncompleteException {
		this.insertBanknoteSuccessfully(10);
		this.scc.finishOrder();
		this.scc.removeItemsPaidFor();
	}

	@Test (expected = ControlSoftwareException.class)
	public void testRemovePurchasedItemsWrongState() {
		this.scc.removeItemsPaidFor();
	}

}
