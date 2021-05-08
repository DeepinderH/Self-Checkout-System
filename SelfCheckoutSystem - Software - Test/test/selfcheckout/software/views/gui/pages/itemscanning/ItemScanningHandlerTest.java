package selfcheckout.software.views.gui.pages.itemscanning;

import org.junit.Before;
import org.junit.Test;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.subcontrollers.PSTC;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AddItemPanel;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;
import selfcheckout.software.views.gui.pages.giftcardpayment.GiftCardGUIHandler;
import selfcheckout.software.views.gui.pages.giftcardpayment.GiftCardPaymentPanel;

import javax.swing.*;

import static org.junit.Assert.assertEquals;

public class ItemScanningHandlerTest {


	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel itemScanningPanel;

	@Before
	public void setUp() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ITEM_SCANNING);
		this.frame = new JFrame();
		this.scc = new ItemScanningHandlerController();
		createItemScanningPanel();
	}

	private void createItemScanningPanel() {
		new ItemScanningGUIHandler(this.viewStateManager, this.scc, this.frame);
		this.itemScanningPanel = (AddItemPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToMainMenu() {
		JButton returnToMainMenuButton = (JButton) (itemScanningPanel.getComponents()[9]);
		returnToMainMenuButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.MAIN_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testLookUpProduct() {
		this.scc = new ItemScanningHandlerController() {
			@Override
			public String lookUpAllBarcodedProductsByDescription(String description) {
				assertEquals(description, "DESCRIPTION");
				throw new ExpectedThrownException();
			}
		};
		createItemScanningPanel();
		JTextField itemDescriptionField = (JTextField) (itemScanningPanel.getComponents()[1]);
		itemDescriptionField.setText("DESCRIPTION");
		JButton searchProductButton = (JButton) (itemScanningPanel.getComponents()[2]);
		searchProductButton.doClick();
	}

	@Test
	public void testAddItem() {
		this.scc = new ItemScanningHandlerController() {
			@Override
			public void scanItem(String barcode, double weight) {
				assertEquals(barcode, ControllerTestConstants.VALID_BARCODE_STRING);
				assertEquals(weight, 1.0, 0.001);
			}
		};
		createItemScanningPanel();
		JTextField itemBarcodeField = (JTextField) (itemScanningPanel.getComponents()[5]);
		itemBarcodeField.setText(ControllerTestConstants.VALID_BARCODE_STRING);
		JTextField itemWeightField = (JTextField) (itemScanningPanel.getComponents()[7]);
		itemWeightField.setText("1.0");
		JButton searchProductButton = (JButton) (itemScanningPanel.getComponents()[8]);
		searchProductButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.ITEM_BAGGING);
	}

	private static class ItemScanningHandlerController extends SelfCheckoutController {
		public ItemScanningHandlerController() {
			super(new BasicSelfCheckoutStation(), null,
				null,null, null, null);
		}
	}
}
