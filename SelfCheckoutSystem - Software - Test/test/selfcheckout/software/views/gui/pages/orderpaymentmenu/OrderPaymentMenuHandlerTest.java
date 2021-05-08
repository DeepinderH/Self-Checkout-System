package selfcheckout.software.views.gui.pages.orderpaymentmenu;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import javax.swing.*;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class OrderPaymentMenuHandlerTest {

	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel orderPaymentMenuPanel;

	@Before
	public void setup() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.MAIN_MENU);
		this.frame = new JFrame();
		this.scc = new OrderPaymentMenuSelfCheckoutController();
		createOrderPaymentMenuPanel();
	}

	private void createOrderPaymentMenuPanel() {
		new OrderPaymentMenuHandler(this.viewStateManager, this.scc, this.frame);
		this.orderPaymentMenuPanel = (JPanel) frame.getContentPane();
	}

	@Test
	public void checkFinishOrderButtonFullPayment() {
		this.scc = new OrderPaymentMenuSelfCheckoutController() {
			@Override
			public BigDecimal getAmountOwedByCustomer() {
				return new BigDecimal("0.00");
			}

			@Override
			public BigDecimal finishOrder() {
				return new BigDecimal("0.00");
			}
		};
		createOrderPaymentMenuPanel();
		assertEquals(this.orderPaymentMenuPanel.getComponents().length, 2);
		JButton finishOrderButton = (JButton) this.orderPaymentMenuPanel.getComponent(1);
		finishOrderButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.FINISH_PURCHASE);
	}

	@Test
	public void checkFinishOrderButtonOverPayment() {
		this.scc = new OrderPaymentMenuSelfCheckoutController() {
			@Override
			public BigDecimal getAmountOwedByCustomer() {
				return new BigDecimal("-1.00");
			}

			@Override
			public BigDecimal finishOrder() {
				throw new ExpectedThrownException("DISPENSE_CHANGE");
			}
		};
		createOrderPaymentMenuPanel();
		assertEquals(this.orderPaymentMenuPanel.getComponents().length, 2);
		JButton finishOrderButton = (JButton) this.orderPaymentMenuPanel.getComponent(1);
		try {
			finishOrderButton.doClick();
		} catch (ExpectedThrownException e) {
			assertEquals(e.getMessage(), "DISPENSE_CHANGE");
			return;
		}
		fail("Should have thrown am ExpectedThrownException");
	}

	@Test
	public void handlePaymentCardButtonPress() {
		JPanel menuButtons = (JPanel) (this.orderPaymentMenuPanel.getComponents()[1]);
		JButton paymentCardButton = (JButton) (menuButtons.getComponents()[0]);
		paymentCardButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.PAYMENT_CARD_MENU);
	}

	@Test
	public void handleGiftCardButtonPress() {
		JPanel menuButtons = (JPanel) (this.orderPaymentMenuPanel.getComponents()[1]);
		JButton giftCardButton = (JButton) (menuButtons.getComponents()[1]);
		giftCardButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.GIFT_CARD_SWIPE);
	}

	@Test
	public void handleInsertBanknoteButtonPress() {
		JPanel menuButtons = (JPanel) (this.orderPaymentMenuPanel.getComponents()[1]);
		JButton insertBanknoteButton = (JButton) (menuButtons.getComponents()[2]);
		insertBanknoteButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.BANKNOTE_PAYMENT);
	}

	@Test
	public void handleInsertCoinButtonPress() {
		JPanel menuButtons = (JPanel) (this.orderPaymentMenuPanel.getComponents()[1]);
		JButton insertCoinButton = (JButton) (menuButtons.getComponents()[3]);
		insertCoinButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.COIN_PAYMENT);
	}

	@Test
	public void handleReturnToItemAdditionButtonPress() {
		boolean[] isHit = {false};
		this.scc = new OrderPaymentMenuSelfCheckoutController() {
			@Override
			public void goToItemAdditionState() {
				isHit[0] = true;
			}
		};
		createOrderPaymentMenuPanel();
		JPanel menuButtons = (JPanel) (this.orderPaymentMenuPanel.getComponents()[1]);
		JButton itemAdditionButton = (JButton) (menuButtons.getComponents()[4]);
		itemAdditionButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.MAIN_MENU);
		assertTrue(isHit[0]);
	}

	private static class OrderPaymentMenuSelfCheckoutController extends SelfCheckoutController {
		public OrderPaymentMenuSelfCheckoutController() {
			super(new BasicSelfCheckoutStation(),
				null,
				new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME),
				null, null, null);
		}

		@Override
		public BigDecimal getAmountOwedByCustomer() {
			return new BigDecimal("1.00");
		}
	}
}
