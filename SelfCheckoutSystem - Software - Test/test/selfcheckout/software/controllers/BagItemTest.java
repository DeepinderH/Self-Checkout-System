package selfcheckout.software.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.exceptions.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BagItemTest {

	private BasicSelfCheckoutStation station;
	private ProductDatabasesWrapper dbWrapper;
	private SelfCheckoutController scc;

	@Before
	public void setup() {
		this.station = new BasicSelfCheckoutStation();
		ProductDatabasesWrapper.initializeDatabases();
		this.dbWrapper = new ProductDatabasesWrapper();
		CardIssuer cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);
		MembershipDatabaseWrapper members = new MembershipDatabaseWrapper();
		GiftCardDatabaseWrapper giftCardDatabaseWrapper = new GiftCardDatabaseWrapper();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new SelfCheckoutController(
			this.station, dbWrapper, cardIssuer, members,
			giftCardDatabaseWrapper, attendantDatabaseWrapper);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant credentials should be valid during setup");
		}
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, dbWrapper, 5.0);
	}

	@After
	public void teardown() {
		ProductDatabasesWrapper.resetDatabases();
	}


	@Test
	public void testNoBaggingException() {
		try {
			// bag item with same weight as scanned item
			this.scc.bagLastItem(5.0);
		} catch (ItemBaggingException e) {
			fail("Should not have thrown a BaggingException");
		}
		assertEquals(this.scc.getCurrentProducts().size(), 1);
		try {
			assertEquals(this.station.baggingArea.getCurrentWeight(), 5.0, 0.001);
		} catch (OverloadException e) {
			fail("Should not be overloaded");
		}

	}

	@Test
	public void testBaggingWeightMismatchApproved() {
		try {
			this.scc.bagLastItem(3.0);
		} catch (WeightMismatchException e1) {
			// we expect there to be a BaggingException thrown because
			// of a weight mismatch
			// when this occurs, the purchase should be empty
			assertEquals(this.scc.getCurrentProducts().size(), 1);
			try {
				// when this occurs, there should also be no weight on either scale
				assertEquals(this.station.scale.getCurrentWeight(), 0.0, 0.001);
				assertEquals(this.station.baggingArea.getCurrentWeight(), 0.0, 0.001);
			} catch (OverloadException e2) {
				fail("scales should not be overloaded");
			}
			try {
				this.scc.getAttendantConsoleController().approveLastItemWeight(
					AttendantConsoleConstant.VALID_ATTENDANT_ID,
					AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
			} catch (IncorrectAttendantLoginInformationException | WeightOverloadException e) {
				fail("ID should be correct and scale should not be overloaded");
			}
			assertEquals(this.scc.getCurrentProducts().size(), 1);
			try {
				// when this occurs, there should also be no weight on either scale
				assertEquals(this.station.scale.getCurrentWeight(), 0.0, 0.001);
				assertEquals(this.station.baggingArea.getCurrentWeight(), 5.0, 0.001);
			} catch (OverloadException e2) {
				fail("scales should not be overloaded");
			}
			return;
		} catch (ItemBaggingException e) {
			fail("Should have thrown WeightMismatchException");
		}
		fail("Should have thrown a BaggingException");
	}

	@Test(expected = IncorrectAttendantLoginInformationException.class)
	public void testBaggingWeightMismatchInvalidCredentials() throws IncorrectAttendantLoginInformationException {
		try {
			this.scc.bagLastItem(3.0);
		} catch (WeightMismatchException e) {
			// we expect there to be a BaggingException thrown because
			// of a weight mismatch
			// when this occurs, the purchase should be empty
			assertEquals(this.scc.getCurrentProducts().size(), 1);
			try {
				// when this occurs, there should also be no weight on either scale
				assertEquals(this.station.scale.getCurrentWeight(), 0.0, 0.001);
				assertEquals(this.station.baggingArea.getCurrentWeight(), 0.0, 0.001);
			} catch (OverloadException e2) {
				fail("scales should not be overloaded");
			}
			try {
				this.scc.getAttendantConsoleController().approveLastItemWeight(
					AttendantConsoleConstant.VALID_ATTENDANT_ID,
					AttendantConsoleConstant.WRONG_ATTENDANT_PASSWORD);
			} catch (WeightOverloadException e2) {
				fail("scale should not be overloaded");
			}
			fail("should have thrown an IncorrectAttendantLoginInformationException");
		} catch (ItemBaggingException e) {
			fail("Should have thrown WeightMismatchException");
		}
		fail("Should have thrown a BaggingException");
	}

	@Test(expected = InvalidWeightException.class)
	public void testInvalidWeightException() throws InvalidWeightException {
		try {
			// bag item with same weight as scanned item
			this.scc.bagLastItem(-5.0);
		} catch (WeightMismatchException | WeightOverloadException e) {
			fail("Should have thrown an InvalidWeightException");
		}
	}

	@Test(expected = WeightOverloadException.class)
	public void testOverloadDuringApproval() throws WeightOverloadException {
		try {
			// bag item with same weight as scanned item
			this.scc.bagLastItem(5.0);
		} catch (ItemBaggingException e) {
			fail("Should not have thrown a BaggingException");
		}
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, this.dbWrapper, 6.0);
		try {
			this.scc.bagLastItem(3.0);
		} catch (WeightMismatchException e1) {
			// we expect there to be a BaggingException thrown because
			// of a weight mismatch
			// when this occurs, the purchase should be empty
			assertEquals(this.scc.getCurrentProducts().size(), 2);
			try {
				// when this occurs, there should also be no weight on either scale
				assertEquals(this.station.scale.getCurrentWeight(), 0.0, 0.001);
				assertEquals(this.station.baggingArea.getCurrentWeight(), 5.0, 0.001);
			} catch (OverloadException e2) {
				fail("scales should not be overloaded");
			}
			try {
				this.scc.getAttendantConsoleController().approveLastItemWeight(
					AttendantConsoleConstant.VALID_ATTENDANT_ID,
					AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
			} catch (IncorrectAttendantLoginInformationException e) {
				fail("credentials should be correct");
			}
			fail("should have thrown an IncorrectAttendantLoginInformationException");
		} catch (ItemBaggingException e) {
			fail("Should have thrown WeightMismatchException");
		}
		fail("Should have thrown a BaggingException");
	}
}
