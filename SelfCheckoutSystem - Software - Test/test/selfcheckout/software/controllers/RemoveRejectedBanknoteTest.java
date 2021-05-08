package selfcheckout.software.controllers;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.exceptions.BanknoteRejectedException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.NoDanglingBanknoteException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;

import static org.junit.Assert.fail;

public class RemoveRejectedBanknoteTest {

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

	@Test
	public void testRemoveDanglingBanknote() {
		try {
			this.scc.insertBanknote(
				ControllerTestConstants.INVALID_BANKNOTE_DENOMINATION,
				ControllerTestConstants.CURRENCY);
		} catch(StorageUnitFullException e) {
			fail("storage unit should be empty on first input");
		} catch (BanknoteRejectedException e) {
			// we expect this exception to be thrown when an invalid
			// banknote is input
		}

		// we should be able to remove the dangling banknote without
		// any exceptions being raised
		this.scc.removeDanglingBanknote();

		// now that the banknote was removed, one of the next 10 valid
		// banknotes should be accepted (we perform this 10 times as the
		// BanknoteValidator sometimes sporadically fails
		for(int i = 0; i < 10; i++) {
			try {
				this.scc.insertBanknote(
					ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
					ControllerTestConstants.CURRENCY);
			} catch(StorageUnitFullException e) {
				fail("storage unit should not be empty");
			} catch (BanknoteRejectedException e) {
				// we expect this exception to be thrown when an invalid
				// banknote is input
				this.scc.removeDanglingBanknote();
				continue;
			}
			// the banknote was accepted successfully as expected
			return;
		}
		fail("All valid banknotes were rejected because the dangling banknote was not removed");
	}

	@Test(expected = NoDanglingBanknoteException.class)
	public void testProgrammaticErrorNoDanglingBanknote() {
		// when there is no dangling banknote to be removed, we expect
		// the NoDanglingBanknoteException to be raised
		this.scc.removeDanglingBanknote();
	}

}
