package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.MembersDatabase;
import selfcheckout.software.controllers.MembershipCardData;
import selfcheckout.software.controllers.MembershipDatabaseWrapper;
import selfcheckout.software.controllers.exceptions.InvalidCardException;
import selfcheckout.software.controllers.exceptions.NotAMemberException;

public class MembershipCardScanningSubcontrollerTest {

	private static class CorrectSwipeCardReader extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			CardData data = new CardData() {
				@Override
				public String getType() {
					return "Membership Card";
				}
				@Override
				public String getNumber() {
					return ControllerTestConstants.VALID_MEMBERSHIP_NUMBER;
				}
				@Override
				public String getCardholder() {
					return ControllerTestConstants.VALID_CARDHOLDER_NAME;
				}
				@Override
				public String getCVV() {
					return "932";
				}
			};

			for(CardReaderListener l : listeners) {
				l.cardDataRead(this, data);
			}
			for(CardReaderListener l : listeners) {
				l.cardSwiped(this);
			}

			return data;
		}
	}

	/*
	 * Card reader that always returns malformed card data
	 */
	private static class IncorrectCardReader extends CardReader {

		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			MembershipCardData data = new MembershipCardData(
				"3278462387462387", "Mr. Name");
			for (CardReaderListener l : listeners) {
				l.cardDataRead(this, data);
			}
			return data;
		}	
	}

	private MembershipDatabaseWrapper wrapper;
	private MembershipCardSubcontroller subcontroller;

	@Before
	public void setUp() {
		CardReader cardReader = new CorrectSwipeCardReader();
		MembershipDatabaseWrapper.initializeMembershipDatabase();
		wrapper = new MembershipDatabaseWrapper();
		subcontroller = new MembershipCardSubcontroller(wrapper, cardReader);
	}

	@After
	public void tearDown() {
		MembershipDatabaseWrapper.clearMembershipDatabase();
		subcontroller.removeActiveMembership();
	}

	@Test
	public void validMembershipCardTest() {
		try {
			subcontroller.processCard(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		} catch (NotAMemberException | InvalidCardException e) {
			fail("No exception should be thrown.");
		}

		// verify that account details can be correctly retrieved
		assertEquals(wrapper.getCardHolderName(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER),
						ControllerTestConstants.VALID_CARDHOLDER_NAME);
		assertEquals(subcontroller.getActiveMembership().getNumber(),
						ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		assertEquals(subcontroller.getActiveMembership().getPoints(), 0);

		// verify points can be added to this account
		subcontroller.getActiveMembership().addPoints(50);
		assertEquals(subcontroller.getActiveMembership().getPoints(), 50);

		// verify this same account can be removed
		subcontroller.removeActiveMembership();
		assertNull(subcontroller.getActiveMembership());

	}
	
	@Test
	public void validMembershipNumberTest() {
		try {
			subcontroller.processNumber(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		} catch (NotAMemberException e) {
			fail("No exception should be thrown.");
		}

		// verify that account details can be correctly retrieved
		assertEquals(wrapper.getCardHolderName(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER),
						ControllerTestConstants.VALID_CARDHOLDER_NAME);
		assertEquals(subcontroller.getActiveMembership().getNumber(),
						ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		assertEquals(subcontroller.getActiveMembership().getPoints(), 0);

		// verify points can be added to this account
		subcontroller.getActiveMembership().addPoints(100);
		assertEquals(subcontroller.getActiveMembership().getPoints(), 100);

		// verify this same account can be removed
		subcontroller.removeActiveMembership();
		assertNull(subcontroller.getActiveMembership());

	}


	// test three membership numbers not linked to any existing accounts

	@Test(expected = NotAMemberException.class)
	public void invalidMembershipTest1() throws NotAMemberException {
		try {
			subcontroller.processCard("123456789"); 			// number very similar to an existing account
		} catch (InvalidCardException e) {
			fail("Should only throw a NotAMemberException");
		}	
	}

	@Test(expected = NotAMemberException.class)
	public void invalidMembershipTest2() throws NotAMemberException {
		try {
			subcontroller.processCard("0");						// a very short number
		} catch (InvalidCardException e) {
			fail("Should only throw a NotAMemberException");
		}				
	}

	@Test(expected = NotAMemberException.class)
	public void invalidMembershipTest3() throws NotAMemberException {
		try {
			subcontroller.processCard("1585043220");			// a number with the same length as a matching number
		} catch (InvalidCardException e) {
			fail("Should only throw a NotAMemberException");
		}
	}

	@Test
	public void mismatchedCompareCardDataTest() {
		MembershipCardData card1 = new MembershipCardData("3233457223", ControllerTestConstants.VALID_CARDHOLDER_NAME);
		MembershipCardData card2 = new MembershipCardData(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER, ControllerTestConstants.VALID_CARDHOLDER_NAME);

		assertFalse(subcontroller.compareCardData(card1, card2));
	}

	@Test(expected = InvalidCardException.class)
	public void mismatchedCardDataTest() throws InvalidCardException {
		// create a card reader that will always generate incorrect data
		IncorrectCardReader faultyReader = new IncorrectCardReader();
		MembershipCardSubcontroller faultySubcontroller = new MembershipCardSubcontroller(wrapper, faultyReader);
		try {
			faultySubcontroller.processCard(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		} catch (NotAMemberException e) {
			fail("Only a NotAMemberException should be thrown");
		}
	}

	private static class BrokenCardReader extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) throws IOException {
			throw new IOException("error");
		}
	}

	@Test(expected = InvalidCardException.class)
	public void invalidCardDataTest() throws InvalidCardException {
		// create a card reader that will always generate incorrect data
		BrokenCardReader brokenReader = new BrokenCardReader();
		MembershipCardSubcontroller faultySubcontroller = new MembershipCardSubcontroller(wrapper, brokenReader);
		try {
			faultySubcontroller.processCard(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		} catch (NotAMemberException e) {
			fail("Only an InvalidCardException should be thrown");
		}
	}

	@Test
	public void testNullMembershipCardCVV() {
		MembershipCardData cardData = new MembershipCardData(
			ControllerTestConstants.VALID_MEMBERSHIP_NUMBER,
			ControllerTestConstants.VALID_CARDHOLDER_NAME);
		assertNull(cardData.getCVV());
	}

	@Test
	public void testGlobalMemberDatabase() {
		MembersDatabase md = new MembersDatabase();
		assertEquals(MembersDatabase.MEMBERSHIP_DATABASE, md.MEMBERSHIP_DATABASE);
	}

}
