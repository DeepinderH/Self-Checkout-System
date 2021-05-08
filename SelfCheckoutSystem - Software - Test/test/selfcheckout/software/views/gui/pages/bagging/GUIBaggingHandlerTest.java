package selfcheckout.software.views.gui.pages.bagging;

import static org.junit.Assert.*;

import javax.swing.*;

import org.junit.Before;
import org.junit.Test;

import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

public class GUIBaggingHandlerTest {
	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel guiBaggingPanel;

	private static class CheckoutController extends SelfCheckoutController {
		public CheckoutController(ProductDatabasesWrapper products) {
			super(new BasicSelfCheckoutStation(),
				products, null, null, null, null);
		}

		@Override
		public void bagLastItem(double weight) {
			throw new ExpectedThrownException();
		}
	}
	
	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		ProductDatabasesWrapper products = new ProductDatabasesWrapper();
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ITEM_BAGGING);
		this.frame = new JFrame();
		this.scc = new CheckoutController(products);
		createGuiBagging();
	}

	private void createGuiBagging() {
		new GUIBaggingHandler(viewStateManager, this.scc, this.frame);
		this.guiBaggingPanel = (JPanel) frame.getContentPane();
	}

	@Test
	public void testRefuseBagButton() {
		JButton skipBaggingButton = (JButton) (this.guiBaggingPanel.getComponents()[4]);
		skipBaggingButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.SKIP_BAGGING_ITEM);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testCorrectWeightButton() {
		JTextField weightTextArea = (JTextField) (this.guiBaggingPanel.getComponents()[2]);
		weightTextArea.setText("2.0");
		JButton inputWeightButton = (JButton) (this.guiBaggingPanel.getComponents()[3]);
		inputWeightButton.doClick();
	}

}
