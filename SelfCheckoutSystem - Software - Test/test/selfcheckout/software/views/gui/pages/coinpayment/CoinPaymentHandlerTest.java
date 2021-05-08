package selfcheckout.software.views.gui.pages.coinpayment;

import org.junit.Before;
import org.junit.Test;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;

public class CoinPaymentHandlerTest {

	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel coinPaymentPanel;

	@Before
	public void setUp() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.COIN_PAYMENT);
		this.frame = new JFrame();
		this.scc = new CoinInsertionHandlerController();
		createCoinInsertionPanel();
	}

	private void createCoinInsertionPanel() {
		new CoinPaymentHandler(this.viewStateManager, this.scc, this.frame);
		this.coinPaymentPanel = (CoinPaymentPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToOrderPaymentMenu() {
		JButton returnToOrderPaymentMenuButton = (JButton) (coinPaymentPanel.getComponents()[4]);
		returnToOrderPaymentMenuButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.ORDER_PAYMENT_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testInsertCoin() {
		JTextField valueField = (JTextField) (coinPaymentPanel.getComponents()[1]);
		valueField.setText("5");
		JTextField currencyField = (JTextField) (coinPaymentPanel.getComponents()[3]);
		currencyField.setText("CAD");
		JButton insertCoinButton = (JButton) (coinPaymentPanel.getComponents()[5]);
		insertCoinButton.doClick();
	}


	private static class CoinInsertionHandlerController extends SelfCheckoutController {
		public CoinInsertionHandlerController() {
			super(new BasicSelfCheckoutStation(), null,
				null,null, null, null);
		}

		@Override
		public void insertCoin(BigDecimal value, Currency currency) {
			throw new ExpectedThrownException();
		}
	}

}
