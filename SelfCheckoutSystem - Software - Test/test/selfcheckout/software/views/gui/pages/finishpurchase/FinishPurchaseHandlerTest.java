package selfcheckout.software.views.gui.pages.finishpurchase;

import static org.junit.Assert.*;

import javax.swing.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.InvalidPLUCodeException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.OrderIncompleteException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.finishpurchase.FinishPurchaseHandler;

import java.util.Currency;

public class FinishPurchaseHandlerTest {

	private SelfCheckoutStation station;
	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel finishPurchasePanel;
	
	private static class FinishPurchaseSelfCheckoutController extends SelfCheckoutController {
		
		public FinishPurchaseSelfCheckoutController(
				SelfCheckoutStation station, ProductDatabasesWrapper pdw,
				AttendantDatabaseWrapper attendantDatabaseWrapper) {
			super(station, pdw, null, null, null, attendantDatabaseWrapper);
		}
	}

	@Before
	public void setUp() {

		ProductDatabasesWrapper.initializeDatabases();
		ProductDatabasesWrapper productDatabasesWrapper = new ProductDatabasesWrapper();
		
		this.station = new BasicSelfCheckoutStation();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new FinishPurchaseSelfCheckoutController(station, productDatabasesWrapper, attendantDatabaseWrapper);

		try {
			this.scc.getAttendantConsoleController().unblockStation(
				ControllerTestConstants.VALID_ATTENDANT_ID,
				ControllerTestConstants.VALID_ATTENDANT_PASSWORD
			);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("login credentials should be valid");
		}

		// Simulates customer having purchased the following items. (Used by printReceipt() in ReceiptPrinterSubcontroller.)
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, productDatabasesWrapper);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);

		this.scc.goToOrderPaymentState();
		BasicSelfCheckoutStation.insertBanknoteSuccessfully(this.scc, 10);
		try {
			this.scc.finishOrder();
		} catch (OrderIncompleteException e) {
			fail("Order should be completed");
		}
		
		this.viewStateManager = new ViewStateManager(ViewStateEnum.FINISH_PURCHASE);
		this.frame = new JFrame();
	}
	
	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	private void createFinishPurchasePanel() {
		new FinishPurchaseHandler(viewStateManager, this.scc, this.frame);
		this.finishPurchasePanel = (JPanel) frame.getContentPane();
	}

	// Tests normal usage of the panel
	@Test
	public void testNormal() {
		this.station.printer.addInk(500);
		this.station.printer.addPaper(50);
		createFinishPurchasePanel();
		JButton finishButton = (JButton) this.finishPurchasePanel.getComponents()[1];
		finishButton.doClick();
		assertEquals(ViewStateEnum.MAIN_MENU, viewStateManager.getState());
		assertEquals(this.scc.getControllerStateEnumStatus(), ControllerStateEnum.ITEM_ADDITION);
	}

	// Tests normal usage of the panel
	@Test
	public void testOutOfSupplies() {
		createFinishPurchasePanel();
		JButton finishButton = (JButton) this.finishPurchasePanel.getComponents()[1];
		finishButton.doClick();
		assertEquals(ViewStateEnum.MAIN_MENU, viewStateManager.getState());
		assertEquals(this.scc.getControllerStateEnumStatus(), ControllerStateEnum.ITEM_ADDITION);
	}

}
