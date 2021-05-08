package selfcheckout.software.controllers;

import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;
import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.controllers.subcontrollers.*;

public class SelfCheckoutController {

	private final SelfCheckoutStation station;

	private final BagAdditionSubcontroller bagAdditionSubcontroller;
	private final BanknoteInsertionSubcontroller banknoteInsertionSubcontroller;
	private final CoinInsertionSubcontroller coinInsertionSubcontroller;
	private final ItemBaggingSubcontroller itemBaggingSubcontroller;
	private final ItemScanningSubcontroller itemScanningSubcontroller;
	private final DispenseChangeSubcontroller dispenseChangeSubcontroller;
	private final MembershipCardSubcontroller membershipCardSubcontroller;
	private final PaymentCardSubcontroller paymentSubcontroller;
	private final PLUItemInputSubcontroller pluItemInputSubController;
	private final ProductDatabasesWrapper productDatabasesWrapper;
	private final PurchaseManager purchaseManager;
	private final PaymentManager paymentManager;
	private final AttendantConsoleController attendantConsoleController;
	private final ControllerStateManager controllerStateManager;
	private final GiftCardSubcontroller giftCardSubcontroller;
	private final ReceiptPrinterSubcontroller receiptPrinterSubcontroller;
	private final ProductLookupSubcontroller productLookupSubcontroller;
	static final String NOT_ITEM_ADDITION_STATE_ERROR = "Must be in the item addition state";
	private static final String NOT_ORDER_PAYMENT_STATE_ERROR = "Must be in the order payment state";
	private static final String NOT_FINISHED_PAYMENT_STATE_ERROR = "Must be in finished payment state";
	private static final String ONLY_ATTENDANT_ENABLE_ERROR = "Only the attendant can enable the self checkout station";
	private static final String DISABLED_ERROR = "Cannot perform that action while machine is disabled";

	public SelfCheckoutController(
			SelfCheckoutStation selfCheckoutStation, ProductDatabasesWrapper databaseWrapper,
			CardIssuer cardIssuer, MembershipDatabaseWrapper members,
			GiftCardDatabaseWrapper giftCardDBWrapper, AttendantDatabaseWrapper attendantDBWrapper) {

		this.station = selfCheckoutStation;
		this.purchaseManager = new PurchaseManager(databaseWrapper);
		this.paymentManager = new PaymentManager();
		this.productDatabasesWrapper = databaseWrapper;

		this.bagAdditionSubcontroller = new BagAdditionSubcontroller(this.station.baggingArea, this.purchaseManager);
		this.banknoteInsertionSubcontroller = new BanknoteInsertionSubcontroller(this.station.banknoteInput, this.station.banknoteValidator, this.paymentManager);
		this.coinInsertionSubcontroller = new CoinInsertionSubcontroller(this.station.coinSlot, this.station.coinValidator, this.paymentManager);

		// monitor to compare weights registered by bagging area scale and scanning area scale 
		// and send messages to purchase interface if needed
		this.itemBaggingSubcontroller = new ItemBaggingSubcontroller(this.station.baggingArea, this.station.scale, this.purchaseManager);
		this.itemScanningSubcontroller = new ItemScanningSubcontroller(
		  this.station.mainScanner, this.station.scale, this.purchaseManager);

		this.dispenseChangeSubcontroller = new DispenseChangeSubcontroller(
		  selfCheckoutStation.coinDispensers, 
		  selfCheckoutStation.banknoteDispensers, 
		  selfCheckoutStation.banknoteOutput,
		  paymentManager, 
		  purchaseManager
		);
    
		this.membershipCardSubcontroller = new MembershipCardSubcontroller(members, this.station.cardReader);
		this.paymentSubcontroller = new PaymentCardSubcontroller(
			this.station.cardReader, this.paymentManager, cardIssuer, this.purchaseManager);

		this.controllerStateManager = new ControllerStateManager(ControllerStateEnum.DISABLED);

		this.attendantConsoleController = new AttendantConsoleController(
			this.station, this.productDatabasesWrapper, this.purchaseManager,
			this.controllerStateManager, attendantDBWrapper);

		this.giftCardSubcontroller = new GiftCardSubcontroller(
			giftCardDBWrapper, this.station.cardReader, this.paymentManager,
			this.purchaseManager);
		this.pluItemInputSubController = new PLUItemInputSubcontroller(this.station.scale, this.purchaseManager);
		this.receiptPrinterSubcontroller = new ReceiptPrinterSubcontroller(this.station.printer,
				this.purchaseManager, this.productDatabasesWrapper);
		this.productLookupSubcontroller = new ProductLookupSubcontroller(this.productDatabasesWrapper);
	}

	public void insertBanknote(int banknoteValue, Currency banknoteCurrency) throws BanknoteRejectedException, StorageUnitFullException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException("must be in the payment state");
		}
		this.banknoteInsertionSubcontroller.insertBanknote(banknoteValue, banknoteCurrency);
	}

	public void removeDanglingBanknote() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException("must be in the payment state");
		}
		this.banknoteInsertionSubcontroller.removeDanglingBanknote();
 	}

	public void insertCoin(BigDecimal coinValue, Currency coinCurrency) throws CoinRejectedException, StorageUnitFullException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException("must be in the payment state");
		}
		this.coinInsertionSubcontroller.insertCoin(coinValue, coinCurrency);
	}

	public BigDecimal getTotalPayment() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException("must be in the payment state");
		}
		return this.paymentManager.getCurrentPaymentTotal().setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	/**
	 * Removes all coins (if any) from the coin tray
	 */
	public void emptyCoinTray() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT
			&& getControllerStateEnumStatus() != ControllerStateEnum.FINISHED_PAYMENT) {
			throw new ControlSoftwareException(NOT_FINISHED_PAYMENT_STATE_ERROR);
		}
		this.station.coinTray.collectCoins();
	}

	public void addBags(Double bagWeight) throws BagAdditionException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		try {
			this.bagAdditionSubcontroller.handleBagAddition(bagWeight);
		} catch (InvalidWeightException | WeightOverloadException e) {
			throw new BagAdditionException(e.getLocalizedMessage());
		}
	}

	public double getCurrentBagWeight() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.bagAdditionSubcontroller.getCurrentBagWeight();
	}

	public double getMaxBagWeight() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.bagAdditionSubcontroller.getMaxBagWeight();
	}

	public void changeBagWeightLimit(double newMaxWeight) {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		this.bagAdditionSubcontroller.changeBagWeightLimit(newMaxWeight);
	}

	/**
	 * Once an item is placed in the bagging area, determines if the weight
	 * registered is cohesive with the rest of the system
	 *
	 * Should be always be called immediately after scanItem(String barcode, double weight)
	 * if and only if the scanItem method does not throw an ItemScanningException.
	 * @param baggingAreaWeight
	 * 		 the item placed on the bagging area scale
	 * @throws InvalidWeightException
	 *      If the weight is invalid e.g. negative
	 * @throws WeightOverloadException
	 *      If the bagging area scale is overloaded
	 * @throws WeightMismatchException
	 *      If the weight on the bagging scale is different than the
	 *      weight of the item when it was on the scanning scale
	 *
	 */
	public void bagLastItem(double baggingAreaWeight) throws InvalidWeightException, WeightMismatchException, WeightOverloadException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		Item lastItem = this.purchaseManager.getLastItem();
		try {
			this.itemBaggingSubcontroller.handleItemBagging(lastItem, baggingAreaWeight);
		} catch (WeightOverloadException e) {
			// if there is an error, remove the item from the purchase
			this.purchaseManager.removeLastItem();
			throw e;
		} catch (InvalidWeightException | WeightMismatchException e) {
			// block the checkout station as a result of the weight discrepancy 
			controllerStateManager.setState(ControllerStateEnum.DISABLED);
			// re-throw the error
			throw e;
		}
	}

	/**
	 * Handles the scanning of a BarcodedItem
	 * @param barcode The Barcode of the BarcodedItem
	 * @param weight The weight of the BarcodedItem
	 * @throws ItemScanningException If there is an error when scanning
	 */
	public void scanItem(String barcode, double weight) throws ItemScanningException {
		if (getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		try {
			this.itemScanningSubcontroller.scanItem(barcode, weight);
		} catch (NonexistentBarcodeException | InvalidBarcodeException | InvalidWeightException | WeightOverloadException e) {
			throw new ItemScanningException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * 
	 * @param pluCode
	 * 		The PriceLookupCode of the item
	 * @param weight
	 * 		The weight of the PLU item
	 * @throws InvalidPLUCodeException 
	 * 		If there is an error when entering the code
	 */
	public void inputPLUItem(String pluCode, double weight) throws InvalidPLUCodeException {
		if (getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		try {
			this.pluItemInputSubController.enterPLUCode(pluCode, weight);
		} catch (NonexistentPLUCodeException | InvalidPLUCodeException | InvalidWeightException | WeightOverloadException e) {
			throw new InvalidPLUCodeException(e.getLocalizedMessage());
		}
	}

	public void swipePaymentCard(
			String type, String number, String cardholder, String cvv,
			String pin, boolean isTapEnabled, boolean hasChip, BufferedImage signature)
				throws InvalidPaymentException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException(NOT_ORDER_PAYMENT_STATE_ERROR);
		}
		try {
			this.paymentSubcontroller.swipeCard(type, number, cardholder, cvv, pin, isTapEnabled, hasChip, signature);
		} catch (PaymentIncompleteException | WrongCardInfoException | CannotReadCardException e) {
			throw new InvalidPaymentException(e.getLocalizedMessage());
		}
	}


	public void insertPaymentCard(
			String type, String number, String cardholder,
			String cvv, String pin, boolean isTapEnabled, boolean hasChip) throws InvalidPaymentException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException(NOT_ORDER_PAYMENT_STATE_ERROR);
		}
		try {
			this.paymentSubcontroller.insertCard(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);
		}  catch (PaymentIncompleteException | WrongCardInfoException | CannotReadCardException e) {
			throw new InvalidPaymentException(e.getLocalizedMessage());
		}
	}

	public void removeCard() {
		this.paymentSubcontroller.removeCard();
	}

	public void tapPaymentCard(
			String type, String number, String cardholder,
			String cvv, String pin, boolean isTapEnabled, boolean hasChip) throws InvalidPaymentException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException(NOT_ORDER_PAYMENT_STATE_ERROR);
		}
		try {
			this.paymentSubcontroller.tapCard(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);
		} catch (PaymentIncompleteException | WrongCardInfoException | CannotReadCardException e) {
			throw new InvalidPaymentException(e.getLocalizedMessage());
		}
	}
	
	/**
	 * This function does the payment using gift card until balance on the
	 * gift card is zero or the purchase has been fully paid, whichever is less
	 * @param giftCardNumber
	 * 		the cardNumber of the gift card
	 * @throws InvalidCardException
	 * 		thrown if the information of the card could not be read or is not correct
	 */
	public void payUntilNoBalanceWithGiftCard(String giftCardNumber) throws InvalidCardException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException(NOT_ORDER_PAYMENT_STATE_ERROR);
		}
		this.giftCardSubcontroller.processUntilNoBalanceWithCard(giftCardNumber);
	}
	
	public BigDecimal getGiftCardBalance(String cardNumber) {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException(NOT_ORDER_PAYMENT_STATE_ERROR);
		}
		return giftCardSubcontroller.getCardBalance(cardNumber).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
	
	public void addPlasticBagsUsed(int numBagsUsed) throws WeightOverloadException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		this.itemBaggingSubcontroller.addPlasticBagsUsed(numBagsUsed);
	}

	public ArrayList<Product> getCurrentProducts() {
		return purchaseManager.getProducts();
	}

	public ArrayList<BarcodedProduct> getFullBarcodedProductList() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.productDatabasesWrapper.getAllBarcodedProducts();
	}

	public ArrayList<PLUCodedProduct> getFullPLUCodedProductList() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.productDatabasesWrapper.getAllPLUCodedProducts();
	}

	/**
	 * Search for a BarcodedProduct
	 * @param barcode
	 * 		The Barcode of the product 
	 * @return
	 * 		The BarcodedProduct's description and price
	 */
	public String lookUpBarcodedProduct(String barcode) {
		if (this.controllerStateManager.getState() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.productLookupSubcontroller.lookupBarcodedProduct(barcode);
	}

	/**
	 * Search for a PLUCodedProduct
	 * @param pluCode
	 * 		The PriceLookupCode of the product 
	 * @return
	 * 		The PLUCodedProduct's description and price
	 */
	public String lookUpPLUProduct(String pluCode) {
		if (this.controllerStateManager.getState() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.productLookupSubcontroller.lookupPLUCodedProduct(pluCode);
	}

	public String lookUpAllBarcodedProductsByDescription(String description) {
		if (this.controllerStateManager.getState() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.productLookupSubcontroller.lookupBarcodedProductByDescription(description);
	}

	public String lookUpAllPLUProductsByDescription(String description) {
		if (this.controllerStateManager.getState() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		return this.productLookupSubcontroller.lookupPLUCodedProductByDescription(description);
	}

	public BigDecimal getTotalPrice() {
		return purchaseManager.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}

	public BigDecimal getAmountOwedByCustomer() {
		if (this.controllerStateManager.getState() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException(NOT_ORDER_PAYMENT_STATE_ERROR);
		}
		BigDecimal amountOwed = purchaseManager.getTotalPrice().subtract(paymentManager.getCurrentPaymentTotal());
		amountOwed = amountOwed.setScale(2, BigDecimal.ROUND_DOWN);
		return amountOwed;
	}
	public void processMembershipCard(String cardNumber) throws NotAMemberException, InvalidCardException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		this.membershipCardSubcontroller.processCard(cardNumber);
	}
	
	public void processMembershipNumber(String membershipNumber) throws NotAMemberException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(NOT_ITEM_ADDITION_STATE_ERROR);
		}
		this.membershipCardSubcontroller.processNumber(membershipNumber);
	}
	
	public String getCurrentCustomerName() {
		return membershipCardSubcontroller.getActiveMembership().getCardholder();
	}
	
	public int getCurrentAccountPoints() {
		return membershipCardSubcontroller.getActiveMembership().getPoints();
	}
	
	public String printReceipt() throws OutOfInkException, OutOfPaperException {
		if (getControllerStateEnumStatus() != ControllerStateEnum.FINISHED_PAYMENT) {
			throw new ControlSoftwareException(NOT_FINISHED_PAYMENT_STATE_ERROR);
		}
		return this.receiptPrinterSubcontroller.printReceipt(this.paymentManager.getPreviousPaymentTotal());
	}

	public void goToItemAdditionState() {
		if (controllerStateManager.getState() == ControllerStateEnum.DISABLED) {
			throw new ControlSoftwareException(ONLY_ATTENDANT_ENABLE_ERROR);
		}
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
	}

	public void goToOrderPaymentState() {
		if (controllerStateManager.getState() == ControllerStateEnum.DISABLED) {
			throw new ControlSoftwareException(ONLY_ATTENDANT_ENABLE_ERROR);
		}
		if(getCurrentProducts().isEmpty()) {
			throw new ControlSoftwareException("Cannot change to order payment state with empty cart");
		}
		try {
			double scanningScaleWeight = this.station.scale.getCurrentWeight();
			if (scanningScaleWeight != 0) {
				throw new ControlSoftwareException("There are still items on the scanning scale");
			}
		} catch (OverloadException e) {
			throw new ControlSoftwareException("There are still items on the scanning scale");
		}
		this.controllerStateManager.setState(ControllerStateEnum.ORDER_PAYMENT);
	}

	public ControllerStateEnum getControllerStateEnumStatus() {
		return this.controllerStateManager.getState();
	}
	
	// Returns the amount of change dispensed
	public BigDecimal finishOrder() throws OrderIncompleteException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ORDER_PAYMENT) {
			throw new ControlSoftwareException(NOT_ORDER_PAYMENT_STATE_ERROR);
		}
		if(this.paymentManager.getCurrentPaymentTotal().compareTo(getTotalPrice()) < 0) {
			throw new OrderIncompleteException("Current payment is less than total price");
		}
		try {
			if(this.station.scale.getCurrentWeight() != 0.0) {
				throw new OrderIncompleteException("Scale is not empty");
			}
		} catch (OverloadException e) {
			throw new OrderIncompleteException(e.getLocalizedMessage());
		}

		try {
			if(this.station.baggingArea.getCurrentWeight() != purchaseManager.getTotalWeight()) {
				throw new ControlSoftwareException("bag weight is not equal to items' total weight");
			}
		} catch (OverloadException e) {
			throw new ControlSoftwareException("Over load exception");
		}

		BigDecimal changeDispensed;
		try {
			changeDispensed = dispenseChangeSubcontroller.dispenseChange();
		} catch (CannotReturnChangeException e) {
			throw new ControlSoftwareException(e.getLocalizedMessage());
		}
		this.membershipCardSubcontroller.removeActiveMembership();
		
		this.bagAdditionSubcontroller.resetBagWeightLimit();
		this.purchaseManager.saveCurrentPurchase();
		// To automatically print out a receipt after a successful purchase.
		this.controllerStateManager.setState(ControllerStateEnum.FINISHED_PAYMENT);
		//goToItemAdditionState();

		this.paymentManager.resetPayment();

		return changeDispensed;
	}

	public void removeChange() {
		if(getControllerStateEnumStatus() != ControllerStateEnum.FINISHED_PAYMENT) {
			throw new ControlSoftwareException(NOT_FINISHED_PAYMENT_STATE_ERROR);
		}
		while (true) {
			try {
				// remove all dangling banknotes
				this.dispenseChangeSubcontroller.removeDanglingBanknote();
			} catch (NoDanglingBanknoteException e) {
				break;
			}
		}
		this.emptyCoinTray();
	}

	public void removeItemsPaidFor() {
		if (this.controllerStateManager.getState() != ControllerStateEnum.FINISHED_PAYMENT) {
			throw new ControlSoftwareException(NOT_FINISHED_PAYMENT_STATE_ERROR);
		}
		Purchase lastPurchase = this.purchaseManager.getLastPurchase();
		ArrayList<Item> itemsInBaggingArea = lastPurchase.getCurrentPurchases();
		for (Item item : itemsInBaggingArea) {
			if (lastPurchase.isBagged(item)) {
				this.station.baggingArea.remove(item);
			}
		}
		this.goToItemAdditionState();
	}

	public String getCustomerOrderSummary() {
		return purchaseManager.getCustomerOrderSummary();
	}

	public int getNumberOfItems() {
		return this.purchaseManager.getItems().size();
	}

	public AttendantConsoleController getAttendantConsoleController() {
		return this.attendantConsoleController;
	}
}
