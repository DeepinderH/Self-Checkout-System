package selfcheckout.software.views.gui;

import selfcheckout.software.controllers.SelfCheckoutController;

import selfcheckout.software.views.SelfCheckoutView;
import selfcheckout.software.views.gui.pages.AttendantMenuLoginHandler;
import selfcheckout.software.views.gui.pages.attendantproductlookup.AttendantProductLookUpHandler;
import selfcheckout.software.views.gui.pages.attendantreceiptinkaddition.AttendantRefillReceiptInkHandler;
import selfcheckout.software.views.gui.pages.attendantreceiptpaperaddition.AttendantRefillReceiptPaperHandler;
import selfcheckout.software.views.gui.pages.attendantrefillbanknotes.AttendantRefillBanknotesHandler;
import selfcheckout.software.views.gui.pages.attendantrefillcoins.AttendantRefillCoinsHandler;
import selfcheckout.software.views.gui.pages.bagaddition.CustomerAddsOwnBagsHandler;
import selfcheckout.software.views.gui.pages.finishpurchase.FinishPurchaseHandler;
import selfcheckout.software.views.gui.pages.attendantmenu.AttendantMenuHandler;
import selfcheckout.software.views.gui.pages.bagging.GUIBaggingHandler;
import selfcheckout.software.views.gui.pages.banknotepayment.BanknotePaymentHandler;
import selfcheckout.software.views.gui.pages.coinpayment.CoinPaymentHandler;
import selfcheckout.software.views.gui.pages.giftcardpayment.GiftCardGUIHandler;
import selfcheckout.software.views.gui.pages.mainmenu.MainMenuHandler;
import selfcheckout.software.views.gui.pages.membership.MembershipCardGUIHandler;
import selfcheckout.software.views.gui.pages.paymentcard.PaymentCardHandler;
import selfcheckout.software.views.gui.pages.plucode.PLUCodeGUIHandler;
import selfcheckout.software.views.gui.pages.orderpaymentmenu.OrderPaymentMenuHandler;
import selfcheckout.software.views.gui.pages.plasticbagaddition.PlasticBagAdditionHandler;
import selfcheckout.software.views.gui.pages.StationUnblockingHandler;
import selfcheckout.software.views.gui.pages.attendantapproveweight.AttendantWeightDiscrepancyHandler;
import selfcheckout.software.views.gui.pages.itemscanning.ItemScanningGUIHandler;
import selfcheckout.software.views.gui.pages.removeitem.RemoveItemHandler;
import selfcheckout.software.views.gui.pages.skipbagging.SkipBaggingHandler;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;


public class GUIManager extends SelfCheckoutView {

	private final JFrame frame;

	private static final String[] componentsUsed = new String[] {
		"Button",
		"Label",
		"OptionPane",
		"PasswordField",
		"RadioButton",
		"ScrollPane",
		"Spinner",
		"TextArea",
		"TextField",
		"TextPane",
	};

	public GUIManager(SelfCheckoutController selfCheckoutController, JFrame frame) {
		super(selfCheckoutController);
		this.frame = frame;
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		this.frame.setSize(screenDimensions);
		this.frame.setMinimumSize(screenDimensions);
		this.frame.setMaximumSize(screenDimensions);
		this.frame.setPreferredSize(screenDimensions);
		FontUIResource font = new FontUIResource("Arial", Font.PLAIN, 30);
		for (String component: componentsUsed) {
			UIManager.put(component + ".font", font);
		}
	}

	@Override
	protected void handleAttendantLogin() {
		AttendantMenuLoginHandler attendantMenuLoginHandler = new AttendantMenuLoginHandler(
			this.stateManager, this.controller, this.frame,
			"Please enter credentials to open attendant menu");
		attendantMenuLoginHandler.handleAttendantLogin();
	}

	@Override
	protected void handleAttendantMenu() {
		AttendantMenuHandler attendantMenuHandler = new AttendantMenuHandler(
			this.stateManager, this.controller, this.frame);
		attendantMenuHandler.handleAttendantMenu();
	}

	@Override
	protected void handleAttendantProductLookup() {
		AttendantProductLookUpHandler handler = new AttendantProductLookUpHandler(
			this.stateManager, this.controller, this.frame);
		handler.handleAction();
	}
	@Override
	protected void handleAttendantRefillBanknotes() {
		AttendantRefillBanknotesHandler attendantRefillBanknotesHandler = new AttendantRefillBanknotesHandler(
			this.stateManager, this.controller, this.frame);
		attendantRefillBanknotesHandler.handleAttendantRefillBanknotes();
	}

	@Override
	protected void handleAttendantRefillCoins() {
		AttendantRefillCoinsHandler attendantRefillCoinsHandler = new AttendantRefillCoinsHandler(
			this.stateManager, this.controller, this.frame);
		attendantRefillCoinsHandler.handleAttendantRefillCoins();
	}

	@Override
	protected void handleAttendantReceiptInkAddition() {
		AttendantRefillReceiptInkHandler handler = new AttendantRefillReceiptInkHandler(
			this.stateManager, this.controller, this.frame);
		handler.handleAction();
	}

	@Override
	protected void handleAttendantReceiptPaperAddition() {
		AttendantRefillReceiptPaperHandler handler = new AttendantRefillReceiptPaperHandler(
			this.stateManager, this.controller, this.frame);
		handler.handleAction();
	}

	@Override
	protected void handleAttendantWeightDiscrepancyApproval() {
		AttendantWeightDiscrepancyHandler weightDiscrepancyHandler = new AttendantWeightDiscrepancyHandler(
				this.stateManager, this.controller, this.frame);
		weightDiscrepancyHandler.handleWeightDiscrepancyApproval();
	}
	@Override
	protected void handleBagAddition() {
		CustomerAddsOwnBagsHandler customerAddsOwnBagsHandler = new CustomerAddsOwnBagsHandler(
			this.stateManager, this.controller, this.frame);
		customerAddsOwnBagsHandler.handleCustomerBagging();
	}

	@Override
	protected void handleBanknotePayment() {
		BanknotePaymentHandler banknotePaymentHandler = new BanknotePaymentHandler(
			this.stateManager, this.controller, this.frame);
		banknotePaymentHandler.handleBanknotePayment();
	}

	@Override
	protected void handleCoinPayment() {
		CoinPaymentHandler coinPaymentHandler = new CoinPaymentHandler(
			this.stateManager, this.controller, this.frame);
		coinPaymentHandler.handleCoinPayment();
	}

	@Override
	protected void handleDisabled() {
		StationUnblockingHandler stationUnblockingHandler = new StationUnblockingHandler(
			this.stateManager, this.controller, this.frame,
			"Station is currently disabled/blocked. " +
				"Enter attendant credentials to enable/unblock");
		stationUnblockingHandler.handleStationUnblocking();
	}

	@Override
	protected void handleFinishPurchase() {
		FinishPurchaseHandler customerFinishesOrderHandler = new FinishPurchaseHandler(
			this.stateManager, this.controller, this.frame);
		customerFinishesOrderHandler.handleFinishOrder();
	}

	@Override
	protected void handleGiftCardSwipe() {
		GiftCardGUIHandler giftCardHandler = new GiftCardGUIHandler(this.stateManager, this.controller, this.frame);
		giftCardHandler.handleGiftCard();
	}

	@Override
	protected void handleItemBagging() {
		GUIBaggingHandler baggingHandler = new GUIBaggingHandler(this.stateManager, this.controller, this.frame);
		baggingHandler.handleItemBagging();
	}

	@Override
	protected void handleMainMenu() {
		MainMenuHandler mainMenuHandler = new MainMenuHandler(this.stateManager, this.controller, this.frame);
		mainMenuHandler.handleMainMenu();
	}
	
	@Override
	protected void handleMembershipCard() {
		MembershipCardGUIHandler membershipHandler = new MembershipCardGUIHandler(this.stateManager, this.controller, this.frame);
		membershipHandler.handleMembershipCard();
	}
	
	@Override
	protected void handlePLUItemInput() {
		PLUCodeGUIHandler pluHandler = new PLUCodeGUIHandler(this.stateManager, this.controller, this.frame);
		pluHandler.handlePLUItemInput();
	}

	@Override
	protected void handlePaymentStart() {
		PlasticBagAdditionHandler plasticBagAdditionHandler = new PlasticBagAdditionHandler(this.stateManager, this.controller, this.frame);
		plasticBagAdditionHandler.handlePlasticBagAddition();
	}

	@Override
	protected void handleOrderPaymentMenu() {
		OrderPaymentMenuHandler orderPaymentMenuHandler = new OrderPaymentMenuHandler(this.stateManager, this.controller, this.frame);
		orderPaymentMenuHandler.handleOrderPaymentMenu();
	}

	@Override
	protected void handlePaymentCardMenu() {
		PaymentCardHandler paymentCardHandler = new PaymentCardHandler(
			this.stateManager, this.controller, this.frame);
		paymentCardHandler.handlePaymentCardUse();
	}

	@Override
	protected void handleItemRemoval() {
		RemoveItemHandler removeItemHandler = new RemoveItemHandler(
			this.stateManager, this.controller, this.frame);
		removeItemHandler.handleRemoveItem();
	}

	@Override
	protected void handleRequestAttendantAssistance() {
		AttendantMenuLoginHandler attendantMenuLoginHandler = new AttendantMenuLoginHandler(
			this.stateManager, this.controller, this.frame,
			"Attendant has been requested! " +
				"Attendant must enter credentials to continue");
		attendantMenuLoginHandler.handleAttendantLogin();
	}

	@Override
	protected void handleItemScanning() {
		ItemScanningGUIHandler itemScanningGUIHandler = new ItemScanningGUIHandler(
			this.stateManager, this.controller, this.frame);
		itemScanningGUIHandler.handleScanItem();
	}

	@Override
	protected void handleSkipBaggingItem() {
		SkipBaggingHandler skipBaggingHandler = new SkipBaggingHandler(
			this.stateManager, this.controller, this.frame);
		skipBaggingHandler.handleSkipBaggingApproval();
	}

	@Override
	protected void handleStationStartup() {
		StationUnblockingHandler stationUnblockingHandler = new StationUnblockingHandler(
			this.stateManager, this.controller, this.frame,
			"Enter attendant credentials to start station");
		stationUnblockingHandler.handleStationUnblocking();
	}


}
