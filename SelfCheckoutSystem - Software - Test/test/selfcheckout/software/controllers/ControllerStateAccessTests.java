package selfcheckout.software.controllers;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.junit.After;

import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.controllers.subcontrollers.PSTC;

import java.util.Currency;

import static org.junit.Assert.*;


public class ControllerStateAccessTests {

	private BasicSelfCheckoutStation station = new BasicSelfCheckoutStation();
	private SelfCheckoutController scc = null;
	private final ProductDatabasesWrapper dbWrapper = new ProductDatabasesWrapper();
	
	@Before
	public void setUp() {
		this.station = new BasicSelfCheckoutStation();
		ProductDatabasesWrapper.initializeDatabases();
		CardIssuer cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);
		MembershipDatabaseWrapper.initializeMembershipDatabase();
		MembershipDatabaseWrapper members = new MembershipDatabaseWrapper();
		GiftCardDatabaseWrapper.initializeGiftCardDatabase();
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
			fail("Credentials should be valid during setup");
		}
	}

	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
		MembershipDatabaseWrapper.clearMembershipDatabase();
		GiftCardDatabaseWrapper.clearGiftCardDatabase();
	}
	
	private void assignOrderPaymentState() {
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, dbWrapper);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);
		this.scc.goToOrderPaymentState();
	}


	@Test(expected = ControlSoftwareException.class)
	public void testInsertBanknoteIncorrectState() {
		try {
			this.scc.insertBanknote(0, null);
		} catch (BanknoteRejectedException aBanknoteRejectedException) {
			fail("BanknoteRejectedException was thrown");
		} catch (StorageUnitFullException aStorageUnitFullException) {
			fail("StorageUnitFullException was thrown");
		}
	}

	@Test(expected = BanknoteRejectedException.class)
	public void testInsertBanknoteCorrectState() throws BanknoteRejectedException {
		this.assignOrderPaymentState();
		try {
			this.scc.insertBanknote(ControllerTestConstants.INVALID_BANKNOTE_DENOMINATION,ControllerTestConstants.CURRENCY);
		} catch (StorageUnitFullException aStorageUnitFullException) {
			fail("StorageUnitFullException was thrown");
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testRemoveDanglingBanknoteIncorrectState() {
		this.scc.removeDanglingBanknote();
	}
	
	@Test
	public void testRemoveDanglingBanknoteCorrectState() {
		assignOrderPaymentState();
		try {
			this.scc.insertBanknote(ControllerTestConstants.INVALID_BANKNOTE_DENOMINATION,ControllerTestConstants.CURRENCY);
		} catch (BanknoteRejectedException e) {
			//expected to fail
			//we must test the removal
		} catch (StorageUnitFullException e) {
			fail("storage unit should be empty on first input");
		}
		this.scc.removeDanglingBanknote();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testInsertCoinIncorrectState() {
		try {
			this.scc.insertCoin(ControllerTestConstants.INVALID_COIN_DENOMINATION, ControllerTestConstants.CURRENCY);
		} catch (CoinRejectedException aCoinRejectedException) {
			fail("CoinRejectedException was thrown");
		} catch (StorageUnitFullException aStorageUnitFullException) {
			fail("StorageUnitFullException was thrown");
		}
	}
	
	@Test(expected = CoinRejectedException.class)
	public void testInsertCoinCorrectState() throws CoinRejectedException {
		try {
			assignOrderPaymentState();
			this.scc.insertCoin(
				ControllerTestConstants.INVALID_COIN_DENOMINATION,
				ControllerTestConstants.CURRENCY);
		} catch (StorageUnitFullException aStorageUnitFullException) {
			fail("StorageUnitFullException was thrown");
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetTotalPaymentIncorrectState() {
		this.scc.getTotalPayment();
	}
	
	@Test
	public void testGetTotalPaymentCorrectState() {
		assignOrderPaymentState();
		this.scc.getTotalPayment();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testEmptyCoinTrayIncorrectState() {
		this.scc.emptyCoinTray();
	}
	
	@Test
	public void testEmptyCoinTrayCorrectState() {
		assignOrderPaymentState();
		this.scc.emptyCoinTray();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testAddBagsIncorrectState() {
		try {
			assignOrderPaymentState();
			this.scc.addBags(1.0);
		} catch (BagAdditionException aBagAdditionException) {
			fail("BagAdditionException was thrown");
		}
	}

	@Test
	public void testAddBagsCorrectState() {
		try {
			this.scc.addBags(1.0);
		} catch (BagAdditionException aBagAdditionException) {
			fail("BagAdditionException was thrown");
		}
	}
	
	@Test(expected = BagAdditionException.class)
	public void testAddBagsCorrectStateException() throws BagAdditionException {
		this.scc.addBags(-1.0);
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testGetCurrentBagWeightIncorrectState() {
		assignOrderPaymentState();
		this.scc.getCurrentBagWeight();
	}

	@Test
	public void testGetCurrentBagWeightCorrectState() {
		double result = this.scc.getCurrentBagWeight();
		double expected = 0.0;
		assertEquals(expected, result, 0.0);
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testGetMaxBagWeightIncorrectState() {
		assignOrderPaymentState();
		this.scc.getMaxBagWeight();
	}
	
	@Test
	public void testGetMaxBagWeightCorrectState() {
		double result = this.scc.getMaxBagWeight();
		double expected = 2.0;
		assertEquals(expected, result, 0.0);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testChangeBagWeightLimitIncorrectState() {
		assignOrderPaymentState();
		this.scc.changeBagWeightLimit(3.0);
	}
	
	@Test
	public void testChangeBagWeightLimitCorrectState() {
		this.scc.changeBagWeightLimit(3.0);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testBagLastItemIncorrectState() {
		try {
			assignOrderPaymentState();
			this.scc.bagLastItem(1.00);
		} catch (ItemBaggingException aItemBaggingException) {
			fail("ItemBaggingException was thrown");
		}
	}

	@Test
	public void testBagLastItemCorrectState() {
		try {
			ProductDatabasesWrapper dbWrapper = new ProductDatabasesWrapper();
			BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, dbWrapper, 3.0);
			this.scc.bagLastItem(3.00);
		} catch (ItemBaggingException aItemBaggingException) {
			fail("ItemBaggingException was thrown");
		}
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testScanItemIncorrectState() {
		try {
			assignOrderPaymentState();
			this.scc.scanItem(ControllerTestConstants.VALID_BARCODE_STRING, 3.00);
		} catch (ItemScanningException aItemScanningException) {
			fail("ItemScanningException was thrown");
		}
	}

	@Test(expected = ItemScanningException.class)
	public void testScanItemCorrectState() throws ItemScanningException {
		this.scc.scanItem(ControllerTestConstants.VALID_BARCODE_STRING, -1.00);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testinputPLUItemIncorrectState() {
		try {
			assignOrderPaymentState();
			this.scc.inputPLUItem(ControllerTestConstants.VALID_PLUCODE_STRING, 3.00);
		} catch (InvalidPLUCodeException aInvalidPLUCodeException) {
			fail("InvalidPLUCodeException was thrown");
		}
	}

	@Test(expected = InvalidPLUCodeException.class)
	public void testInputPLUItemCorrectStateException() throws InvalidPLUCodeException {
		this.scc.inputPLUItem(ControllerTestConstants.VALID_PLUCODE_STRING, -1.00);
	}

	@Test
	public void testInputPLUItemCorrectState() {
		try {
			this.scc.inputPLUItem(ControllerTestConstants.VALID_PLUCODE_STRING, 1.00);
		} catch (InvalidPLUCodeException e) {
			fail("Should not have thrown an exception");
		}
	}

	private void swipeGiftCardUntilCorrect() {
		for (int i = 0; i < 10; i++) {
			try {
				this.scc.payUntilNoBalanceWithGiftCard("1234567890");
			} catch (InvalidCardException e) {
				continue;
			}
			return;

		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGiftCardUseIncorrectState() {
		this.swipeGiftCardUntilCorrect();
	}

	@Test
	public void testGiftCardUseCorrectState() {
		this.assignOrderPaymentState();
		this.swipeGiftCardUntilCorrect();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGiftCardBalanceIncorrectState() {
		this.scc.getGiftCardBalance("1234567890");
	}

	@Test
	public void testGiftCardBalanceCorrectState() {
		this.assignOrderPaymentState();
		this.scc.getGiftCardBalance("1234567890");
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetPLUListIncorrectState() {
		this.assignOrderPaymentState();
		this.scc.getFullPLUCodedProductList();
	}

	@Test
	public void testGetPLUListCorrectState() {
		this.scc.getFullPLUCodedProductList();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetAmountOwedByCustomerIncorrectState() {
		this.scc.getAmountOwedByCustomer();
	}

	@Test
	public void testGetAmountOwedByCustomerCorrectState() {
		this.assignOrderPaymentState();
		this.scc.getAmountOwedByCustomer();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testPrintReceiptIncorrectState() {
		try {
			this.scc.printReceipt();
		} catch (OutOfInkException | OutOfPaperException e) {
			fail("should not be out of ink or paper");
		}
	}

	@Test
	public void testPrintReceiptCorrectState() {
		this.station.printer.addPaper(20);
		this.station.printer.addInk(1000);
		this.assignOrderPaymentState();
		BasicSelfCheckoutStation.insertBanknoteSuccessfully(this.scc, 10);
		try {
			this.scc.finishOrder();
		} catch (OrderIncompleteException e) {
			fail("Order should be complete");
		}
		try {
			this.scc.printReceipt();
		} catch (OutOfInkException | OutOfPaperException e) {
			fail("should not be out of ink or paper");
		}
	}

	@Test(expected = OutOfPaperException.class)
	public void testPrintReceiptCorrectStateException() throws OutOfPaperException {
		this.station.printer.addPaper(10);
		this.station.printer.addInk(1000);
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, this.dbWrapper);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);
		this.assignOrderPaymentState();
		BasicSelfCheckoutStation.insertBanknoteSuccessfully(this.scc, 20);
		try {
			this.scc.finishOrder();
		} catch (OrderIncompleteException e) {
			fail("Order should be complete");
		}
		try {
			this.scc.printReceipt();
		} catch (OutOfInkException e) {
			fail("should not throw out of paper exception");
		}
	}

	@Test
	public void testRemoveChangeCorrectState() {
		this.assignOrderPaymentState();
		try {
			this.station.banknoteDispensers.get(10).load(new Banknote(10, Currency.getInstance("CAD")));
		} catch (OverloadException e) {
			fail("Should only have a single banknote");
		}
		BasicSelfCheckoutStation.insertBanknoteSuccessfully(this.scc, 20);
		try {
			this.scc.finishOrder();
		} catch (OrderIncompleteException e) {
			fail("Order should be complete");
		}
		this.scc.removeChange();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testRemoveChangeIncorrectState() {
		this.scc.removeChange();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGoToItemAdditionIncorrectState() {
		try {
			this.scc.getAttendantConsoleController().loginAsAttendant(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("login credentials should be correct");
		}
		this.scc.getAttendantConsoleController().blockStation();
		this.scc.goToItemAdditionState();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGoToOrderPaymentIncorrectState() {
		try {
			this.scc.getAttendantConsoleController().loginAsAttendant(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("login credentials should be correct");
		}
		this.scc.getAttendantConsoleController().blockStation();
		this.scc.goToItemAdditionState();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testSwipePaymentCardIncorrectState() {
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
			fail("ItemScanningException was thrown when ControlSoftwareException was expected");
		}
	}

	@Test(expected = InvalidPaymentException.class)
	public void testSwipePaymentCardCorrectState() throws InvalidPaymentException {
		this.assignOrderPaymentState();
		// should be in correct state to swipe card even if invalid card data
		this.scc.swipePaymentCard(
			PSTC.RTYPE,
			PSTC.WRONGCARDNUMBER,
			PSTC.WRONGCARDHOLDER,
			PSTC.WRONGCARDCVV,
			PSTC.WRONGCARDPIN,
			PSTC.NOTAP,
			PSTC.NOCHIP,
			PSTC.SIGNATURE
		);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testInsertPaymentCardIncorrectState() {
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
			fail("ItemScanningException was thrown when ControlSoftwareException was expected");
		}
	}

	@Test(expected = InvalidPaymentException.class)
	public void testInsertPaymentCardCorrectState() throws InvalidPaymentException {
		this.assignOrderPaymentState();
		// should be in correct state to insert card even if invalid card data
		this.scc.insertPaymentCard(
			PSTC.RTYPE,
			PSTC.WRONGCARDNUMBER,
			PSTC.WRONGCARDHOLDER,
			PSTC.WRONGCARDCVV,
			PSTC.WRONGCARDPIN,
			PSTC.NOTAP,
			PSTC.NOCHIP
		);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testTapPaymentCardIncorrectState() {
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
			fail("ItemScanningException was thrown when ControlSoftwareException was expected");
		}
	}

	@Test(expected = InvalidPaymentException.class)
	public void testTapPaymentCardCorrectState() throws InvalidPaymentException {
		this.assignOrderPaymentState();
		// should be in correct state to tap card even if invalid card data
		this.scc.tapPaymentCard(
			PSTC.RTYPE,
			PSTC.WRONGCARDNUMBER,
			PSTC.WRONGCARDHOLDER,
			PSTC.WRONGCARDCVV,
			PSTC.WRONGCARDPIN,
			PSTC.NOTAP,
			PSTC.NOCHIP
		);
	}

	@Test
	public void testProcessMembershipCardCorrectState() {
		for (int i = 0; i < 10; i++) {
			try {
				this.scc.processMembershipCard(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
			} catch (NotAMemberException | InvalidCardException e) {
				// ignore, will sporadically invalidate card
				continue;
			}
			assertEquals(this.scc.getCurrentCustomerName(), "Mr. Name");
			assertEquals(this.scc.getCurrentAccountPoints(), 0);
			return;
		}
		fail("Could not process membership card");
	}

	@Test(expected = ControlSoftwareException.class)
	public void testProcessMembershipCardIncorrectState() {
		this.assignOrderPaymentState();
		try {
			this.scc.processMembershipCard(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		} catch (NotAMemberException | InvalidCardException e) {
			fail("Expected ControlSoftwareException, not other exceptions");
		}
	}

	@Test
	public void testProcessMembershipNumberCorrectState() {
		for (int i = 0; i < 10; i++) {
			try {
				this.scc.processMembershipNumber(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
			} catch (NotAMemberException e) {
				// ignore, will sporadically invalidate card
				continue;
			}
			assertEquals(this.scc.getCurrentCustomerName(), "Mr. Name");
			assertEquals(this.scc.getCurrentAccountPoints(), 0);
			return;
		}
		fail("Could not process membership card");
	}

	@Test(expected = ControlSoftwareException.class)
	public void testProcessMembershipNumberIncorrectState() {
		this.assignOrderPaymentState();
		try {
			this.scc.processMembershipNumber(ControllerTestConstants.VALID_MEMBERSHIP_NUMBER);
		} catch (NotAMemberException e) {
			fail("Expected ControlSoftwareException, not other exceptions");
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testGetFullBarcodedProductListIncorrectState() {
		assignOrderPaymentState();
		this.scc.getFullBarcodedProductList();
	}
	
	@Test
	public void testGetFullBarcodedProductListCorrectState() {
		this.scc.getFullBarcodedProductList();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testAddPlasticBagsIncorrectState() {
		assignOrderPaymentState();
		try {
			this.scc.addPlasticBagsUsed(1);
		} catch (WeightOverloadException e) {
			fail("should not have thrown a WeightOverloadException");
		}
	}

	@Test
	public void testAddPlasticBagsCorrectState() {
		try {
			this.scc.addPlasticBagsUsed(1);
		} catch (WeightOverloadException e) {
			fail("should not have thrown a WeightOverloadException");
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testLookUpBarcodedProductIncorrectState() {
		assignOrderPaymentState();
		this.scc.lookUpBarcodedProduct("23578");
	}

	@Test
	public void testLookUpBarcodedProductCorrectState() {
		this.scc.lookUpBarcodedProduct("23578");
	}

	@Test(expected = ControlSoftwareException.class)
	public void testLookUpPLUProductIncorrectState() {
		assignOrderPaymentState();
		this.scc.lookUpPLUProduct("4131");
	}
	
	@Test
	public void testLookUpPLUProductCorrectState() {
		this.scc.lookUpPLUProduct("4131");
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testLookUpBarcodedProductDescriptionIncorrectState() {
		assignOrderPaymentState();
		this.scc.lookUpAllBarcodedProductsByDescription("Apple");
	}

	@Test
	public void testLookUpBarcodedProductDescriptionCorrectState() {
		this.scc.lookUpAllBarcodedProductsByDescription("Apple");
	}

	@Test(expected = ControlSoftwareException.class)
	public void testLookUpPLUProductDescriptionIncorrectState() {
		assignOrderPaymentState();
		this.scc.lookUpAllPLUProductsByDescription("Apple");
	}

	@Test
	public void testLookUpPLUProductDescriptionCorrectState() {
		this.scc.lookUpAllPLUProductsByDescription("Apple");
	}

	@Test
	public void testGetCustomerOrderSummaryCorrectState() {
		assertNotEquals(this.scc.getCustomerOrderSummary(), "");
	}

	@Test
	public void testGetNumberOfItemsCorrectState() {
		assertEquals(this.scc.getNumberOfItems(), 0);
	}

	@Test
	public void testSetControlStateEnum() {
		this.scc.goToItemAdditionState();
		ControllerStateEnum result = this.scc.getControllerStateEnumStatus();
		ControllerStateEnum expected = ControllerStateEnum.ITEM_ADDITION;
		assertEquals(result, expected);
	}

	@Test(expected = ControlSoftwareException.class)
	public void testSetControlStateEnumScanningScaleNotEmpty() {
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, this.dbWrapper);
		this.scc.goToOrderPaymentState();
	}

	@Test(expected = ControlSoftwareException.class)
	public void testSetControlStateEnumScanningScaleOverloaded() {
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, this.dbWrapper);
		Item heavyItem = new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 100.0);
		this.station.scale.add(heavyItem);
		this.scc.goToOrderPaymentState();
	}
	
	@Test(expected = ControlSoftwareException.class)
	public void testSetControlStateEnumException() {
		this.scc.goToOrderPaymentState();
	}

	@Test
	public void testGetControllerStateEnumStatus() {
		ControllerStateEnum result = this.scc.getControllerStateEnumStatus();
		ControllerStateEnum expected = ControllerStateEnum.ITEM_ADDITION;
		assertEquals(result, expected);
	}

}
