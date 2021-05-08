package selfcheckout.software.views.gui.pages.giftcardpayment;

import org.junit.Before;
import org.junit.Test;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.subcontrollers.PSTC;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import javax.swing.*;
import java.util.Currency;

import static org.junit.Assert.assertEquals;

public class GiftCardPaymentHandlerTest {


	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel giftCardPaymentPanel;

	@Before
	public void setUp() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.GIFT_CARD_SWIPE);
		this.frame = new JFrame();
		this.scc = new GiftCardPaymentHandlerController();
		createGiftCardPaymentPanel();
	}

	private void createGiftCardPaymentPanel() {
		new GiftCardGUIHandler(this.viewStateManager, this.scc, this.frame);
		this.giftCardPaymentPanel = (GiftCardPaymentPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToOrderPaymentMenu() {
		JButton returnToOrderPaymentMenuButton = (JButton) (giftCardPaymentPanel.getComponents()[4]);
		returnToOrderPaymentMenuButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.ORDER_PAYMENT_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testSwipeGiftCard() {
		JTextField cardNumberField = (JTextField) (giftCardPaymentPanel.getComponents()[2]);
		cardNumberField.setText(PSTC.CARDNUMBER);
		JButton swipeCardButton = (JButton) (giftCardPaymentPanel.getComponents()[3]);
		swipeCardButton.doClick();
	}

	private static class GiftCardPaymentHandlerController extends SelfCheckoutController {
		public GiftCardPaymentHandlerController() {
			super(new BasicSelfCheckoutStation(), null,
				null,null, null, null);
		}

		@Override
		public void payUntilNoBalanceWithGiftCard(String number) {
			assertEquals(number, PSTC.CARDNUMBER);
			throw new ExpectedThrownException();
		}
	}

}
