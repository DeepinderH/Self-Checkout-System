package selfcheckout.software.views;

import selfcheckout.software.controllers.SelfCheckoutController;

public abstract class SelfCheckoutView {

	protected SelfCheckoutController controller;
	protected ViewStateManager stateManager;

	public SelfCheckoutView(SelfCheckoutController selfCheckoutController) {
		this.controller = selfCheckoutController;
		this.stateManager = new ViewStateManager(ViewStateEnum.STATION_STARTUP);
	}

	public void run() {
		while(this.stateManager.getState() != ViewStateEnum.EXIT_APPLICATION) {
			try {
				this.handleState();
			} catch(Exception e) {
				this.stateManager.setState(ViewStateEnum.EXIT_APPLICATION);
				System.out.println("Encountered Error:");
				System.out.println(e.toString());
				System.out.println("Exiting...");
			}
		}
	}

	protected void handleState() {
		if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_EMPTY_BANKNOTES) {
			this.handleAttendantEmptyBanknotes();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_EMPTY_COINS) {
			this.handleAttendantEmptyCoins();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_LOGIN) {
			this.handleAttendantLogin();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_LOGOUT) {
			this.handleAttendantLogout();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_MENU) {
			this.handleAttendantMenu();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_PRODUCT_LOOKUP) {
			this.handleAttendantProductLookup();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_RECEIPT_INK_ADDITION) {
			this.handleAttendantReceiptInkAddition();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_RECEIPT_PAPER_ADDITION) {
			this.handleAttendantReceiptPaperAddition();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_REFILL_BANKNOTES) {
			this.handleAttendantRefillBanknotes();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_REFILL_COINS) {
			this.handleAttendantRefillCoins();
		} else if (this.stateManager.getState() == ViewStateEnum.ATTENDANT_WEIGHT_DISCREPANCY_APPROVAL) {
			this.handleAttendantWeightDiscrepancyApproval();
		} else if (this.stateManager.getState() == ViewStateEnum.BAG_ADDITION) {
			this.handleBagAddition();
		} else if (this.stateManager.getState() == ViewStateEnum.BANKNOTE_PAYMENT) {
			this.handleBanknotePayment();
		} else if (this.stateManager.getState() == ViewStateEnum.COIN_PAYMENT) {
			this.handleCoinPayment();
		} else if (this.stateManager.getState() == ViewStateEnum.CUSTOMER_PRODUCT_LOOKUP) {
			this.handleCustomerProductLookup();
		} else if (this.stateManager.getState() == ViewStateEnum.DISABLED) {
			this.handleDisabled();
		} else if (this.stateManager.getState() == ViewStateEnum.FINISH_PURCHASE) {
			this.handleFinishPurchase();
		} else if (this.stateManager.getState() == ViewStateEnum.GIFT_CARD_SWIPE) {
			this.handleGiftCardSwipe();
		} else if (this.stateManager.getState() == ViewStateEnum.ITEM_BAGGING) {
			this.handleItemBagging();
		} else if (this.stateManager.getState() == ViewStateEnum.ITEM_REMOVAL) {
			this.handleItemRemoval();
		} else if (this.stateManager.getState() == ViewStateEnum.ITEM_SCANNING) {
			this.handleItemScanning();
		} else if (this.stateManager.getState() == ViewStateEnum.MAIN_MENU) {
			this.handleMainMenu();
		} else if (this.stateManager.getState() == ViewStateEnum.MEMBERSHIP_CARD) {
			this.handleMembershipCard();
		} else if (this.stateManager.getState() == ViewStateEnum.ORDER_PAYMENT_MENU) {
			this.handleOrderPaymentMenu();
		} else if (this.stateManager.getState() == ViewStateEnum.PAYMENT_CARD_MENU) {
			this.handlePaymentCardMenu();
		} else if (this.stateManager.getState() == ViewStateEnum.PAYMENT_START) {
			this.handlePaymentStart();
		} else if (this.stateManager.getState() == ViewStateEnum.PLU_ITEM_INPUT) {
			this.handlePLUItemInput();
		} else if (this.stateManager.getState() == ViewStateEnum.PRINT_RECEIPT) {
			this.handlePrintReceipt();
		} else if (this.stateManager.getState() == ViewStateEnum.REQUEST_ATTENDANT_ASSISTANCE) {
			this.handleRequestAttendantAssistance();
		} else if (this.stateManager.getState() == ViewStateEnum.SKIP_BAGGING_ITEM) {
			this.handleSkipBaggingItem();
		} else if (this.stateManager.getState() == ViewStateEnum.STATION_STARTUP) {
			this.handleStationStartup();
		} else {
			throw new IllegalStateException("State is invalid");
		}
	}

	protected void handleAttendantEmptyBanknotes() { throw new NotImplementedException(); }
	protected void handleAttendantEmptyCoins() { throw new NotImplementedException(); }
	protected void handleAttendantLogin() { throw new NotImplementedException(); }
	protected void handleAttendantLogout() { throw new NotImplementedException(); }
	protected void handleAttendantMenu() { throw new NotImplementedException(); }
	protected void handleAttendantProductLookup() { throw new NotImplementedException(); }
	protected void handleAttendantReceiptInkAddition() { throw new NotImplementedException(); }
	protected void handleAttendantReceiptPaperAddition() { throw new NotImplementedException(); }
	protected void handleAttendantRefillBanknotes() { throw new NotImplementedException(); }
	protected void handleAttendantRefillCoins() { throw new NotImplementedException(); }
	protected void handleAttendantWeightDiscrepancyApproval() { throw new NotImplementedException(); }
	protected void handleBagAddition() { throw new NotImplementedException(); }
	protected void handleBanknotePayment() { throw new NotImplementedException(); }
	protected void handleCoinPayment() { throw new NotImplementedException(); }
	protected void handleCustomerProductLookup() { throw new NotImplementedException(); }
	protected void handleDisabled() { throw new NotImplementedException(); }
	protected void handleFinishPurchase() { throw new NotImplementedException(); }
	protected void handleGiftCardSwipe() { throw new NotImplementedException(); }
	protected void handleItemBagging() { throw new NotImplementedException(); }
	protected void handleItemRemoval() { throw new NotImplementedException(); }
	protected void handleItemScanning() { throw new NotImplementedException(); }
	protected void handleMainMenu() { throw new NotImplementedException(); }
	protected void handleMembershipCard() { throw new NotImplementedException(); }
	protected void handleOrderPaymentMenu() { throw new NotImplementedException(); }
	protected void handlePaymentCardMenu() { throw new NotImplementedException(); }
	protected void handlePaymentStart() { throw new NotImplementedException(); }
	protected void handlePrintReceipt() {throw new NotImplementedException(); }
	protected void handlePLUItemInput() { throw new NotImplementedException(); }
	protected void handleRequestAttendantAssistance() { throw new NotImplementedException(); }
	protected void handleSkipBaggingItem() { throw new NotImplementedException(); }
	protected void handleStationStartup() { throw new NotImplementedException(); }
}
