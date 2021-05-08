package selfcheckout.software.views.gui.pages.plucode;

import org.junit.Before;
import org.junit.Test;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AddItemPanel;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import javax.swing.*;

import static org.junit.Assert.assertEquals;

public class PLUInputHandlerTest {


	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel pluInputPanel;

	@Before
	public void setUp() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.PLU_ITEM_INPUT);
		this.frame = new JFrame();
		this.scc = new PLUInputHandlerController();
		createPLUInputPanel();
	}

	private void createPLUInputPanel() {
		new PLUCodeGUIHandler(this.viewStateManager, this.scc, this.frame);
		this.pluInputPanel = (AddItemPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToMainMenu() {
		JButton returnToMainMenuButton = (JButton) (pluInputPanel.getComponents()[9]);
		returnToMainMenuButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.MAIN_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testLookUpProduct() {
		this.scc = new PLUInputHandlerController() {
			@Override
			public String lookUpAllPLUProductsByDescription(String description) {
				assertEquals(description, "DESCRIPTION");
				throw new ExpectedThrownException();
			}
		};
		createPLUInputPanel();
		JTextField itemDescriptionField = (JTextField) (pluInputPanel.getComponents()[1]);
		itemDescriptionField.setText("DESCRIPTION");
		JButton searchProductButton = (JButton) (pluInputPanel.getComponents()[2]);
		searchProductButton.doClick();
	}

	@Test
	public void testAddItem() {
		this.scc = new PLUInputHandlerController() {
			@Override
			public void inputPLUItem(String pluCode, double weight) {
				assertEquals(pluCode, ControllerTestConstants.VALID_PLUCODE_STRING);
				assertEquals(weight, 1.0, 0.001);
			}
		};
		createPLUInputPanel();
		JTextField itemBarcodeField = (JTextField) (pluInputPanel.getComponents()[5]);
		itemBarcodeField.setText(ControllerTestConstants.VALID_PLUCODE_STRING);
		JTextField itemWeightField = (JTextField) (pluInputPanel.getComponents()[7]);
		itemWeightField.setText("1.0");
		JButton searchProductButton = (JButton) (pluInputPanel.getComponents()[8]);
		searchProductButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.ITEM_BAGGING);
	}

	private static class PLUInputHandlerController extends SelfCheckoutController {
		public PLUInputHandlerController() {
			super(new BasicSelfCheckoutStation(), null,
				null,null, null, null);
		}
	}
}
