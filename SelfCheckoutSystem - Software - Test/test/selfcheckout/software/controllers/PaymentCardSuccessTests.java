package selfcheckout.software.controllers;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.InvalidPaymentException;
import selfcheckout.software.controllers.subcontrollers.PSTC;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class PaymentCardSuccessTests {

	private BasicSelfCheckoutStation station;
	private ProductDatabasesWrapper dbWrapper;
	private CardIssuer cardIssuer;
	private MembershipDatabaseWrapper memberDatabase;
	private SelfCheckoutController scc;

	@Before
	public void setUp() {
		this.station = new BasicSelfCheckoutStation();
		this.dbWrapper = new ProductDatabasesWrapper();
		ProductDatabasesWrapper.initializeDatabases();
		this.cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);

		Calendar futureExpiryDate = GregorianCalendar.getInstance();
		futureExpiryDate.add(Calendar.YEAR, 1);
		this.cardIssuer.addCardData(
			PSTC.CREDIT_CARD_DATA.getNumber(),
			PSTC.CREDIT_CARD_DATA.getCardholder(),
			futureExpiryDate,
			PSTC.CREDIT_CARD_DATA.getCVV(),
			PSTC.CREDITAMOUNT);
		this.memberDatabase = new MembershipDatabaseWrapper();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new SelfCheckoutController(
			this.station, this.dbWrapper, cardIssuer, this.memberDatabase,
			null, attendantDatabaseWrapper);
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

	private final int NUM_ITERATIONS = 10;

	@Test
	public void testSwipePaymentCardSuccess() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.scc.swipePaymentCard(
					PSTC.CREDIT_CARD_DATA.getType(),
					PSTC.CREDIT_CARD_DATA.getNumber(),
					PSTC.CREDIT_CARD_DATA.getCardholder(),
					PSTC.CREDIT_CARD_DATA.getCVV(),
					PSTC.CARDPIN,
					PSTC.TAP,
					PSTC.CHIP,
					PSTC.SIGNATURE
				);
			} catch (InvalidPaymentException e) {
				// payment will sporadically fail, just try again
				continue;
			}
			// successful swipe
			return;
		}
		fail("Could not swipe payment card");
	}

	@Test
	public void testInsertPaymentCardSuccess() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.scc.insertPaymentCard(
					PSTC.CREDIT_CARD_DATA.getType(),
					PSTC.CREDIT_CARD_DATA.getNumber(),
					PSTC.CREDIT_CARD_DATA.getCardholder(),
					PSTC.CREDIT_CARD_DATA.getCVV(),
					PSTC.CARDPIN,
					PSTC.TAP,
					PSTC.CHIP
				);
			} catch (InvalidPaymentException e) {
				// payment will sporadically fail, just remove inserted card
				// before trying again
				this.station.cardReader.remove();
				continue;
			}
			// successful insertion
			return;
		}
		fail("Could not insert payment card");
	}

	@Test
	public void testTapPaymentCardSuccess() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.scc.tapPaymentCard(
					PSTC.CREDIT_CARD_DATA.getType(),
					PSTC.CREDIT_CARD_DATA.getNumber(),
					PSTC.CREDIT_CARD_DATA.getCardholder(),
					PSTC.CREDIT_CARD_DATA.getCVV(),
					PSTC.CARDPIN,
					PSTC.TAP,
					PSTC.CHIP
				);
			} catch (InvalidPaymentException e) {
				// payment will sporadically fail, try again
				continue;
			}
			// successful tap
			return;
		}
		fail("Could not tap payment card");
	}
}
