package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;
import org.lsmr.selfcheckout.external.CardIssuer;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;

public class PaymentCardSubcontrollerTest {
	private PaymentManager paymentManager;
	private CardReader cardReader;
	private CardIssuer cardIssuer;
	private PurchaseManager purchaseManager;
	private PaymentCardSubcontroller paymentCardSubcontroller;

	@Before
	public void setUp() {
		this.paymentManager = new PaymentManager();
		this.cardIssuer = new CardIssuer(PSTC.ISSUER_NAME);
		ProductDatabasesWrapper.initializeDatabases();
		this.purchaseManager = new PurchaseManager(new ProductDatabasesWrapper());
		Item validItem = new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 2.0);
		try {
			this.purchaseManager.addItem(validItem);
		} catch (NoSuchItemException e) {
			fail("Item in purchase must exist");
		}

		Calendar futureExpiryDate = GregorianCalendar.getInstance();
		futureExpiryDate.add(Calendar.YEAR, 1);

		this.cardIssuer.addCardData(
			PSTC.CREDIT_CARD_DATA.getNumber(),
			PSTC.CREDIT_CARD_DATA.getCardholder(),
			futureExpiryDate,
			PSTC.CREDIT_CARD_DATA.getCVV(),
			PSTC.CREDITAMOUNT);
		this.cardIssuer.addCardData(
			PSTC.DEBIT_CARD_DATA.getNumber(),
			PSTC.DEBIT_CARD_DATA.getCardholder(),
			futureExpiryDate,
			PSTC.DEBIT_CARD_DATA.getCVV(),
			PSTC.DEBITBALANCE);
	}

	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}


	@Test
	public void testInsertCreditCard(){
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.NOTAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException | CannotReadCardException e) {
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void testInsertDebitCard(){
		try {
			this.cardReader = new WorkingDebitCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.DEBIT_CARD_DATA.getType(),
				PSTC.DEBIT_CARD_DATA.getNumber(),
				PSTC.DEBIT_CARD_DATA.getCardholder(),
				PSTC.DEBIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.NOTAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException | CannotReadCardException e) {
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void testSwipeCreditCard(){
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.swipeCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.NOTAP,
				PSTC.CHIP,
				PSTC.SIGNATURE);
		} catch (PaymentIncompleteException | WrongCardInfoException | CannotReadCardException e) {
			fail("No exception should be thrown");
		}
	}
	
	@Test
	public void testSwipeDebitCard(){
		try {
			this.cardReader = new WorkingDebitCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.swipeCard(
				PSTC.DEBIT_CARD_DATA.getType(),
				PSTC.DEBIT_CARD_DATA.getNumber(),
				PSTC.DEBIT_CARD_DATA.getCardholder(),
				PSTC.DEBIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.NOTAP,
				PSTC.CHIP,
				PSTC.SIGNATURE);
		} catch (PaymentIncompleteException| WrongCardInfoException | CannotReadCardException e) {
			fail("No exception should not be thrown");
		}
	}
	
	@Test
	public void testTapCreditCard(){
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.tapCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException| WrongCardInfoException | CannotReadCardException e) {
			fail("No exception should not be thrown");
		}
	}
	
	@Test
	public void testTapDebitCard() {
		try {
			this.cardReader = new WorkingDebitCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.tapCard(
				PSTC.DEBIT_CARD_DATA.getType(),
				PSTC.DEBIT_CARD_DATA.getNumber(),
				PSTC.DEBIT_CARD_DATA.getCardholder(),
				PSTC.DEBIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException| WrongCardInfoException | CannotReadCardException e) {
			fail("No exception should not be thrown");
		}
	}


	
	@Test(expected = CannotReadCardException.class)
	public void testCannotReadCardExceptionByInsertingCreditCard() throws CannotReadCardException {
		try {
			this.cardReader = new CardReaderFailureStub();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("CannotReadCardException should be thrown");
		}
	}

	@Test(expected = CannotReadCardException.class)
	public void testCannotReadCardExceptionByInsertingDebitCard() throws CannotReadCardException {
		try {
			this.cardReader = new CardReaderFailureStub();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.DEBIT_CARD_DATA.getType(),
				PSTC.DEBIT_CARD_DATA.getNumber(),
				PSTC.DEBIT_CARD_DATA.getCardholder(),
				PSTC.DEBIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("CannotReadCardException should be thrown");
		}
	}

	@Test(expected = CannotReadCardException.class)
	public void testCannotReadCardExceptionBySwipingCreditCard() throws CannotReadCardException {
		try {
			this.cardReader = new CardReaderFailureStub();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.cardIssuer.block(PSTC.CARDNUMBER);
			this.paymentCardSubcontroller.swipeCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.NOTAP,
				PSTC.CHIP,
				PSTC.SIGNATURE);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("CannotReadCardException should be thrown");
		}
	}

	@Test(expected = CannotReadCardException.class)
	public void testCannotReadCardExceptionBySwipingDebitCard() throws CannotReadCardException {
		try {
			this.cardReader = new CardReaderFailureStub();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.swipeCard(
				PSTC.DEBIT_CARD_DATA.getType(),
				PSTC.DEBIT_CARD_DATA.getNumber(),
				PSTC.DEBIT_CARD_DATA.getCardholder(),
				PSTC.DEBIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP,
				PSTC.SIGNATURE);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("CannotReadCardException should be thrown");
		}
	}

	@Test(expected = CannotReadCardException.class)
	public void testCannotReadCardExceptionByTappingCreditCard() throws CannotReadCardException {
		try {
			this.cardReader = new CardReaderFailureStub();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.tapCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.NOTAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("CannotReadCardException should be thrown");
		}
	}

	@Test(expected = CannotReadCardException.class)
	public void testCannotReadCardExceptionByTappingDebitCard() throws CannotReadCardException {
		try {
			this.cardReader = new CardReaderFailureStub();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.tapCard(
				PSTC.DEBIT_CARD_DATA.getType(),
				PSTC.DEBIT_CARD_DATA.getNumber(),
				PSTC.DEBIT_CARD_DATA.getCardholder(),
				PSTC.DEBIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("CannotReadCardException should be thrown");
		}
	}



	@Test(expected = WrongCardInfoException.class)
	public void testWrongInfoByInsertingCreditCard() throws WrongCardInfoException {
		try {
			this.cardReader = new WrongInfoCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | CannotReadCardException e) {
			fail("CannotReadCardException should be thrown");
		}
	}

	@Test(expected = WrongCardInfoException.class)
	public void testWrongInfoByInsertingDebitCard() throws WrongCardInfoException {
		try {
			this.cardReader = new WrongInfoCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | CannotReadCardException e) {
			fail("CannotReadCardException should be thrown");
		}
	}


	@Test(expected =  WrongCardInfoException.class)
	public void testWrongCardInfoExceptionByInvalidType() throws WrongCardInfoException{
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.RTYPE,
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | CannotReadCardException e) {
			fail("Only WrongCardInfoException should be thrown");
		}
	}

	@Test(expected =  WrongCardInfoException.class)
	public void testWrongCardInfoExceptionByWrongType() throws WrongCardInfoException{
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.DTYPE,
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | CannotReadCardException e) {
			fail("Only WrongCardInfoException should be thrown");
		}
	}

	@Test(expected = WrongCardInfoException.class)
	public void testWrongCardInfoExceptionByWrongCardNumber() throws WrongCardInfoException {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.WRONGCARDNUMBER,
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | CannotReadCardException e) {
			fail("Only WrongCardInfoException should be thrown");
		}
	}

	@Test(expected = WrongCardInfoException.class)
	public void testWrongCardInfoExceptionByWrongCardholder() throws WrongCardInfoException {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.WRONGCARDHOLDER,
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | CannotReadCardException e) {
			fail("Only WrongCardInfoException should be thrown");
		}
	}

	@Test(expected = WrongCardInfoException.class)
	public void testWrongCardInfoExceptionByWrongCVV() throws WrongCardInfoException {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.WRONGCARDCVV,
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (PaymentIncompleteException | CannotReadCardException e) {
			fail("Only WrongCardInfoException should be thrown");
		}
	}

	@Test
	public void testSwipeCardWrongCVVNoException() {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.swipeCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.WRONGCARDCVV,
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP,
				PSTC.SIGNATURE);
		} catch (PaymentIncompleteException | CannotReadCardException | WrongCardInfoException e) {
			fail("No exception should be thrown");
		}
	}


	@Test(expected =  CannotReadCardException.class)
	public void testNullCardInsert() throws CannotReadCardException {
		try {
			this.cardReader = new CardReaderNull();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.tapCard(PSTC.CTYPE, PSTC.CARDNUMBER, PSTC.CARDHOLDER, PSTC.CARDCVV, PSTC.CARDPIN, PSTC.TAP, PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("Only CannotReadCardException should be thrown");
		}
	}
	
	@Test(expected =  CannotReadCardException.class)
	public void testNullCardSwipe() throws CannotReadCardException{
		try {
			this.cardReader = new CardReaderNull();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.swipeCard(PSTC.CTYPE, PSTC.CARDNUMBER, PSTC.CARDHOLDER, PSTC.CARDCVV, PSTC.CARDPIN, PSTC.TAP, PSTC.CHIP, PSTC.SIGNATURE);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("Only CannotReadCardException should be thrown");
		}
	}

	@Test(expected =  CannotReadCardException.class)
	public void testNullCardTap() throws CannotReadCardException{
		try {
			this.cardReader = new CardReaderNull();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentCardSubcontroller.tapCard(PSTC.CTYPE, PSTC.CARDNUMBER, PSTC.CARDHOLDER, PSTC.CARDCVV, PSTC.CARDPIN, PSTC.TAP, PSTC.CHIP);
		} catch (PaymentIncompleteException | WrongCardInfoException e) {
			fail("Only CannotReadCardException should be thrown");
		}
	}





	@Test(expected = PaymentIncompleteException.class)
	public void testInsufficientFundsInsert() throws PaymentIncompleteException {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);this.paymentManager.resetPayment();
			int holdNumber = this.cardIssuer.authorizeHold(PSTC.CREDIT_CARD_DATA.getNumber(), PSTC.CREDITAMOUNT);
			this.cardIssuer.postTransaction(PSTC.CREDIT_CARD_DATA.getNumber(), holdNumber, PSTC.CREDITAMOUNT);

			// spent entire credit limit
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (WrongCardInfoException | CannotReadCardException e) {
			fail("Only PaymentIncompleteException should be thrown");
		}
	}

	@Test(expected = PaymentIncompleteException.class)
	public void testInsufficientFundsSwipe() throws PaymentIncompleteException {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);this.paymentManager.resetPayment();
			int holdNumber = this.cardIssuer.authorizeHold(PSTC.CREDIT_CARD_DATA.getNumber(), PSTC.CREDITAMOUNT);
			this.cardIssuer.postTransaction(PSTC.CREDIT_CARD_DATA.getNumber(), holdNumber, PSTC.CREDITAMOUNT);

			// spent entire credit limit
			this.paymentCardSubcontroller.swipeCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP,
				PSTC.SIGNATURE);
		} catch (WrongCardInfoException | CannotReadCardException e) {
			fail("Only PaymentIncompleteException should be thrown");
		}
	}

	@Test(expected = PaymentIncompleteException.class)
	public void testInsufficientFundsTap() throws PaymentIncompleteException {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);this.paymentManager.resetPayment();

			// spent entire credit limit
			int holdNumber = this.cardIssuer.authorizeHold(PSTC.CREDIT_CARD_DATA.getNumber(), PSTC.CREDITAMOUNT);
			this.cardIssuer.postTransaction(PSTC.CREDIT_CARD_DATA.getNumber(), holdNumber, PSTC.CREDITAMOUNT);

			this.paymentCardSubcontroller.tapCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (WrongCardInfoException | CannotReadCardException e) {
			fail("Only PaymentIncompleteException should be thrown");
		}
	}


	@Test(expected = PaymentNotRequiredException.class)
	public void testPaymentNotRequiredExceptionEqual()  {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			this.paymentManager.addPayment(PSTC.FULL_PAYMENT);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (CannotReadCardException | WrongCardInfoException | PaymentIncompleteException e) {
			fail("Only PaymentNotRequiredException should be thrown");
		}
	}

	@Test(expected = PaymentNotRequiredException.class)
	public void testPaymentNotRequiredExceptionLarger()  {
		try {
			this.cardReader = new WorkingCreditCardReader();
			this.paymentCardSubcontroller = new PaymentCardSubcontroller(
				this.cardReader, this.paymentManager, this.cardIssuer, this.purchaseManager);
			// overpay
			this.paymentManager.addPayment(PSTC.FULL_PAYMENT);
			this.paymentManager.addPayment(PSTC.FULL_PAYMENT);
			this.paymentCardSubcontroller.insertCard(
				PSTC.CREDIT_CARD_DATA.getType(),
				PSTC.CREDIT_CARD_DATA.getNumber(),
				PSTC.CREDIT_CARD_DATA.getCardholder(),
				PSTC.CREDIT_CARD_DATA.getCVV(),
				PSTC.CARDPIN,
				PSTC.TAP,
				PSTC.CHIP);
		} catch (CannotReadCardException | WrongCardInfoException | PaymentIncompleteException e) {
			fail("Only PaymentNotRequiredException should be thrown");
		}
	}


	private static class WorkingCreditCardReader extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			for(CardReaderListener l : listeners) {
				l.cardSwiped(this);
			}
			notifyCreditCardDataRead();
			return PSTC.CREDIT_CARD_DATA;
		}

		@Override
		public CardData tap(Card card) {
			for(CardReaderListener l : listeners) {
				l.cardTapped(this);
			}
			notifyCreditCardDataRead();
			return PSTC.CREDIT_CARD_DATA;
		}

		@Override
		public CardData insert(Card card, String pin) {
			for(CardReaderListener l : listeners) {
				l.cardInserted(this);
			}
			notifyCreditCardDataRead();
			return PSTC.CREDIT_CARD_DATA;
		}

		private void notifyCreditCardDataRead() {
			for(CardReaderListener l : listeners) {
				l.cardDataRead(this, PSTC.CREDIT_CARD_DATA);
			}
		}
	}

	private static class WorkingDebitCardReader extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			for(CardReaderListener l : listeners) {
				l.cardSwiped(this);
			}
			this.notifyDebitCardDataRead();
			return PSTC.DEBIT_CARD_DATA;
		}

		@Override
		public CardData tap(Card card) {
			for(CardReaderListener l : listeners) {
				l.cardTapped(this);
			}
			this.notifyDebitCardDataRead();
			return PSTC.DEBIT_CARD_DATA;
		}

		@Override
		public CardData insert(Card card, String pin) {
			for(CardReaderListener l : listeners) {
				l.cardInserted(this);
			}
			this.notifyDebitCardDataRead();
			return PSTC.DEBIT_CARD_DATA;
		}

		private void notifyDebitCardDataRead() {
			for(CardReaderListener l : listeners) {
				l.cardDataRead(this, PSTC.DEBIT_CARD_DATA);
			}
		}
	}

	private static class CardReaderFailureStub extends CardReader {
	    @Override
	    public CardData insert(Card card, String pin) throws IOException {
	        throw new ChipFailureException();
	    }
	    
	    @Override
	    public CardData swipe(Card card, BufferedImage signature) throws IOException {
	        throw new MagneticStripeFailureException();
	    }
	    
	    @Override
	    public CardData tap(Card card) throws IOException {
	    	throw new ChipFailureException();
	    }
	}
	
	private static class WrongInfoCardReader extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			for(CardReaderListener l : listeners) {
				l.cardSwiped(this);
			}
			this.notifyBadCardDataRead();
			return PSTC.BAD_CARD_DATA;
		}
		
		@Override
		public CardData tap(Card card) {
			for(CardReaderListener l : listeners) {
				l.cardTapped(this);
			}
			this.notifyBadCardDataRead();
			return PSTC.BAD_CARD_DATA;
		}
		
		@Override
		public CardData insert(Card card, String pin) {
			for(CardReaderListener l : listeners) {
				l.cardInserted(this);
			}
			this.notifyBadCardDataRead();
			return PSTC.BAD_CARD_DATA;
		}
		
		private void notifyBadCardDataRead() {
			for(CardReaderListener l : listeners) {
				l.cardDataRead(this, PSTC.BAD_CARD_DATA);
			}
		}
	}
	
	private static class CardReaderNull extends CardReader {
		@Override
		public CardData swipe(Card card, BufferedImage signature) {
			return null;
		}
		
		@Override
		public CardData tap(Card card) {
			return null;
		}
		
		@Override
		public CardData insert(Card card, String pin) {
			return null;
		}
	}
}
