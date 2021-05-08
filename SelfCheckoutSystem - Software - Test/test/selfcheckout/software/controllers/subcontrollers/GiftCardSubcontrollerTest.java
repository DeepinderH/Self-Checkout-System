package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.MagneticStripeFailureException;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.GiftCardAccount;
import selfcheckout.software.controllers.GiftCardDatabase;
import selfcheckout.software.controllers.GiftCardDatabaseWrapper;
import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.InvalidCardException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.PaymentIncompleteException;

public class GiftCardSubcontrollerTest {
	private GiftCardDatabaseWrapper giftCardWrapper;
	private GiftCardSubcontroller giftCardSubController;
	private CardReader cardReader;
	private PaymentManager paymentManager;
	private PurchaseManager purchaseManager;

	@Before
	public void setUp() {
		GiftCardDatabaseWrapper.initializeGiftCardDatabase();
		this.paymentManager = new PaymentManager();
		ProductDatabasesWrapper.initializeDatabases();
		ProductDatabasesWrapper productDatabaseWrapper = new ProductDatabasesWrapper();
		this.purchaseManager = new PurchaseManager(productDatabaseWrapper);
		this.paymentManager = new PaymentManager();
		this.giftCardWrapper = new GiftCardDatabaseWrapper();
	}

	@After
	public void tearDown() {
		GiftCardDatabaseWrapper.clearGiftCardDatabase();
	}

	
	@Test
	public void testProcessTillNoPaymentBySwipingCardWithInsufficientBalance() throws PaymentIncompleteException{
		try {
			this.cardReader = new WorkingGiftCardReader();
			GiftCardDatabaseWrapper.clearGiftCardDatabase();
			this.giftCardWrapper.addGiftCardToDatabase(
					PSTC.GIFTCARDBALANCE,
					PSTC.gSwipeCard,
					PSTC.SWIPEGIFTCARDHOLDERNAME,
					PSTC.SWIPEGIFTCARDNUMBER);
			this.purchaseManager.addItem(PSTC.INVALID_ITEM);
			this.purchaseManager.addItem(PSTC.INVALID_ITEM);

			this.giftCardSubController = new GiftCardSubcontroller(
					this.giftCardWrapper, this.cardReader, this.paymentManager, this.purchaseManager);
			this.giftCardSubController.processUntilNoBalanceWithCard(PSTC.SWIPEGIFTCARDNUMBER);
		} catch (InvalidCardException | NoSuchItemException  e) {
			fail("No exception should be thrown");
		}
	}	

	@Test
	public void testProcessTillNoPaymentBySwipingCardWithInsufficientBalance2() throws PaymentIncompleteException{
		try {
			this.cardReader = new WorkingGiftCardReader();
			GiftCardDatabaseWrapper.clearGiftCardDatabase();
			this.giftCardWrapper.addGiftCardToDatabase(
					PSTC.GIFTCARDBALANCE,
					PSTC.gSwipeCard,
					PSTC.SWIPEGIFTCARDHOLDERNAME,
					PSTC.SWIPEGIFTCARDNUMBER);
			this.purchaseManager.addItem(PSTC.INVALID_ITEM);
			this.purchaseManager.addItem(PSTC.INVALID_ITEM);

			this.giftCardSubController = new GiftCardSubcontroller(
					this.giftCardWrapper, this.cardReader, this.paymentManager, this.purchaseManager);
			this.giftCardSubController.processUntilNoBalanceWithCard(PSTC.SWIPEGIFTCARDNUMBER);
		} catch (InvalidCardException | NoSuchItemException  e) {
			fail("No exception should be thrown");
		}
	}	
	
	@Test
	public void testProcessTillNoPaymentBySwipingCardWithSufficientBalance() {
		try {
			this.cardReader = new WorkingGiftCardReader();
			GiftCardDatabaseWrapper.clearGiftCardDatabase();
			this.giftCardWrapper.addGiftCardToDatabase(
					PSTC.GIFTCARDBALANCE, PSTC.gSwipeCard,
					PSTC.SWIPEGIFTCARDHOLDERNAME,
					PSTC.SWIPEGIFTCARDNUMBER);
			this.purchaseManager.addItem(PSTC.INVALID_ITEM);

			this.giftCardSubController = new GiftCardSubcontroller(
					this.giftCardWrapper, this.cardReader, this.paymentManager, this.purchaseManager);
			this.giftCardSubController.processUntilNoBalanceWithCard(PSTC.SWIPEGIFTCARDNUMBER);
		} catch ( InvalidCardException | NoSuchItemException  e) {
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void testProcessTillNoPaymentBySwipingCardWithSufficientBalance2() {
		try {
			this.cardReader = new WorkingGiftCardReader2();
			GiftCardDatabaseWrapper.clearGiftCardDatabase();
			this.giftCardWrapper.addGiftCardToDatabase(
					PSTC.GIFTCARDBALANCE2,
					PSTC.gSwipeCard2,
					PSTC.SWIPEGIFTCARDHOLDERNAME2,
					PSTC.SWIPEGIFTCARDNUMBER2);
			this.purchaseManager.addItem(PSTC.INVALID_ITEM);

			this.giftCardSubController = new GiftCardSubcontroller(
					this.giftCardWrapper, this.cardReader, this.paymentManager, this.purchaseManager);
			this.giftCardSubController.processUntilNoBalanceWithCard(PSTC.SWIPEGIFTCARDNUMBER2);		
		} catch ( InvalidCardException | NoSuchItemException  e) {
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void testGetInformationFromGiftCard() {
		this.cardReader = new WorkingGiftCardReader();
		GiftCardDatabaseWrapper.clearGiftCardDatabase();
		try {
			this.giftCardWrapper.addGiftCardToDatabase(
					PSTC.GIFTCARDBALANCE,
					PSTC.gSwipeCard,
					PSTC.SWIPEGIFTCARDHOLDERNAME,
					PSTC.SWIPEGIFTCARDNUMBER);
			GiftCardAccount getGiftCardAccount = this.giftCardWrapper.getAccount(PSTC.SWIPEGIFTCARDNUMBER);
			assertEquals(PSTC.SWIPEGIFTCARDHOLDERNAME, getGiftCardAccount.getCardholder());
			assertEquals(PSTC.SWIPEGIFTCARDNUMBER, getGiftCardAccount.getNumber());
			assertEquals(PSTC.GIFTCARDBALANCE, getGiftCardAccount.getBalance());
			assertEquals(null, PSTC.giftCardData.getCVV());
		} catch (InvalidCardException e) {
			fail("No exception should be thrown");
		}
	}

	@Test
	public void testGetCardBalanceViaSubcontroller() {
		try {
			this.cardReader = new WorkingGiftCardReader();
			GiftCardDatabaseWrapper.clearGiftCardDatabase();
			this.giftCardWrapper.addGiftCardToDatabase(
					PSTC.GIFTCARDBALANCE2,
					PSTC.gSwipeCard,
					PSTC.SWIPEGIFTCARDHOLDERNAME,
					PSTC.SWIPEGIFTCARDNUMBER);
			this.giftCardSubController = new GiftCardSubcontroller(this.giftCardWrapper, 
					this.cardReader, this.paymentManager, this.purchaseManager);
			assertEquals(PSTC.GIFTCARDBALANCE2,this.giftCardSubController.getCardBalance(PSTC.SWIPEGIFTCARDNUMBER));	
		} catch (InvalidCardException e) {
			fail("No exception should be thrown");			
		}
	}

	@Test
	public void testGiftCardWrapperGetInformation() {
		try {
			this.cardReader = new WorkingGiftCardReader();
			GiftCardDatabaseWrapper.clearGiftCardDatabase();
			this.giftCardWrapper.addGiftCardToDatabase(
					PSTC.GIFTCARDBALANCE,
					PSTC.gSwipeCard,
					PSTC.SWIPEGIFTCARDHOLDERNAME,
					PSTC.SWIPEGIFTCARDNUMBER);
			assertEquals(PSTC.SWIPEGIFTCARDHOLDERNAME, this.giftCardWrapper.getCardHolderName(PSTC.SWIPEGIFTCARDNUMBER));
			assertTrue(this.giftCardWrapper.giftCardIssued(PSTC.SWIPEGIFTCARDNUMBER));
			assertEquals(PSTC.GIFTCARDBALANCE, this.giftCardWrapper.getCardBalance(PSTC.SWIPEGIFTCARDNUMBER));
			assertEquals(PSTC.gSwipeCard, GiftCardDatabaseWrapper.getGiftCard(PSTC.SWIPEGIFTCARDNUMBER));

		} catch (InvalidCardException e) {
			fail("No exception should be thrown");
		}
	}
	
	@Test (expected = InvalidCardException.class)
	public void testMultipleSameGiftCard() throws InvalidCardException{
		this.cardReader = new WorkingGiftCardReader();
		GiftCardDatabaseWrapper.clearGiftCardDatabase();
		this.giftCardWrapper.addGiftCardToDatabase(
				PSTC.GIFTCARDBALANCE, PSTC.gSwipeCard, PSTC.SWIPEGIFTCARDHOLDERNAME, PSTC.SWIPEGIFTCARDNUMBER);
		this.giftCardWrapper.addGiftCardToDatabase(
				PSTC.GIFTCARDBALANCE, PSTC.gSwipeCard, PSTC.SWIPEGIFTCARDHOLDERNAME, PSTC.SWIPEGIFTCARDNUMBER);
	}

	@Test (expected = InvalidCardException.class)
	public void testIOExceptionGiftCard() throws InvalidCardException{
		this.cardReader = new GiftCardReaderFailureStub();
		this.giftCardWrapper.addGiftCardToDatabase(
			PSTC.GIFTCARDBALANCE, PSTC.gSwipeCard, PSTC.SWIPEGIFTCARDHOLDERNAME, PSTC.SWIPEGIFTCARDNUMBER);
		this.giftCardSubController = new GiftCardSubcontroller(
			this.giftCardWrapper, this.cardReader, this.paymentManager, this.purchaseManager);
		this.giftCardSubController.processUntilNoBalanceWithCard(PSTC.SWIPEGIFTCARDNUMBER);
	}

	@Test (expected = InvalidCardException.class)
	public void testWrongInfoGiftCard() throws InvalidCardException{
		this.cardReader = new WrongInfoGiftCardReader();
		this.giftCardWrapper.addGiftCardToDatabase(
			PSTC.GIFTCARDBALANCE, PSTC.gSwipeCard, PSTC.SWIPEGIFTCARDHOLDERNAME, PSTC.SWIPEGIFTCARDNUMBER);
		this.giftCardSubController = new GiftCardSubcontroller(
			this.giftCardWrapper, this.cardReader, this.paymentManager, this.purchaseManager);
		this.giftCardSubController.processUntilNoBalanceWithCard(PSTC.SWIPEGIFTCARDNUMBER);
	}
	
	private static class GiftCardReaderFailureStub extends CardReader {
	    @Override
	    public CardData swipe(Card card, BufferedImage signature) throws IOException {
	        throw new MagneticStripeFailureException();
	    }
	}	
	
	private static class WrongInfoGiftCardReader extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			for(CardReaderListener l : listeners) {
				l.cardSwiped(this);
			}
			this.notifyBadCardDataRead();
			return PSTC.giftWrongCardData;
		}
		
		private void notifyBadCardDataRead() {
			for(CardReaderListener l : listeners) {
				l.cardDataRead(this, PSTC.giftWrongCardData);
			}
		}
	}
	
	private static class WorkingGiftCardReader extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			for(CardReaderListener l : listeners) {
				l.cardSwiped(this);
			}
			notifyGiftCardDataRead();
			return PSTC.giftCardData;
		}

		private void notifyGiftCardDataRead() {
			for(CardReaderListener l : listeners) {
				l.cardDataRead(this, PSTC.giftCardData);
			}
		}
	}
	
	private static class WorkingGiftCardReader2 extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			for(CardReaderListener l : listeners) {
				l.cardSwiped(this);
			}
			notifyGiftCardDataRead();
			return PSTC.giftCardData2;
		}

		private void notifyGiftCardDataRead() {
			for(CardReaderListener l : listeners) {
				l.cardDataRead(this, PSTC.giftCardData2);
			}
		}
	}
}
