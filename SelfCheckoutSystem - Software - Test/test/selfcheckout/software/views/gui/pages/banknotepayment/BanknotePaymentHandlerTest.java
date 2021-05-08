package selfcheckout.software.views.gui.pages.banknotepayment;

import org.junit.Before;
import org.junit.Test;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import javax.swing.*;

import java.util.Currency;

import static org.junit.Assert.assertEquals;

public class BanknotePaymentHandlerTest {

	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel banknotePaymentPanel;

	@Before
	public void setUp() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.BANKNOTE_PAYMENT);
		this.frame = new JFrame();
		this.scc = new BanknoteInsertionHandlerController();
		createBanknoteInsertionPanel();
	}

	private void createBanknoteInsertionPanel() {
		new BanknotePaymentHandler(this.viewStateManager, this.scc, this.frame);
		this.banknotePaymentPanel = (BanknotePaymentPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToOrderPaymentMenu() {
		JButton returnToOrderPaymentMenuButton = (JButton) (banknotePaymentPanel.getComponents()[4]);
		returnToOrderPaymentMenuButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.ORDER_PAYMENT_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testInsertBanknote() {
		JTextField valueField = (JTextField) (banknotePaymentPanel.getComponents()[1]);
		valueField.setText("5");
		JTextField currencyField = (JTextField) (banknotePaymentPanel.getComponents()[3]);
		currencyField.setText("CAD");
		JButton insertBanknoteButton = (JButton) (banknotePaymentPanel.getComponents()[5]);
		insertBanknoteButton.doClick();
	}


	private static class BanknoteInsertionHandlerController extends SelfCheckoutController {
		public BanknoteInsertionHandlerController() {
			super(new BasicSelfCheckoutStation(), null,
				null,null, null, null);
		}

		@Override
		public void insertBanknote(int value, Currency currency) {
			throw new ExpectedThrownException();
		}
	}

}
