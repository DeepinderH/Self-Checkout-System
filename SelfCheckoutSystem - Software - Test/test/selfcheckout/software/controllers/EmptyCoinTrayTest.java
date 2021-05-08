package selfcheckout.software.controllers;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.exceptions.CoinRejectedException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;

import static org.junit.Assert.fail;

public class EmptyCoinTrayTest {
	private SelfCheckoutController scc;

	@Before
	public void setUp() {
		SelfCheckoutStation station = new BasicSelfCheckoutStation();
		ProductDatabasesWrapper dbWrapper = new ProductDatabasesWrapper();
		ProductDatabasesWrapper.initializeDatabases();
		CardIssuer cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);
		MembershipDatabaseWrapper members = new MembershipDatabaseWrapper();
		GiftCardDatabaseWrapper giftCardDatabaseWrapper = new GiftCardDatabaseWrapper();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new SelfCheckoutController(
			station, dbWrapper, cardIssuer, members,
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

	@Test(expected = CoinRejectedException.class)
	public void testEmptyCoinTray() throws CoinRejectedException {

		// fill coin tray to capacity
		for (int i = 0; i < SelfCheckoutStation.COIN_TRAY_CAPACITY; i ++) {
			try {
				this.scc.insertCoin(
					ControllerTestConstants.INVALID_COIN_DENOMINATION,
					ControllerTestConstants.CURRENCY);
			} catch(StorageUnitFullException e) {
				fail("coin tray should not become full");
			} catch (CoinRejectedException e) {
				// we expect this exception to be thrown when an invalid
				// coin is input
				continue;
			}
			fail("coin should be rejected");
		}
		// remove all coins in the coin tray
		this.scc.emptyCoinTray();

		// the next invalid coin should be rejected like normal, but
		// if the coin tray was not emptied properly, then a
		// SimulationException will be thrown rather than a
		// CoinRejectedException
		try {
			this.scc.insertCoin(
				ControllerTestConstants.INVALID_COIN_DENOMINATION,
				ControllerTestConstants.CURRENCY);
		} catch(StorageUnitFullException e) {
			fail("storage unit should not become full");
		}
	}
}
