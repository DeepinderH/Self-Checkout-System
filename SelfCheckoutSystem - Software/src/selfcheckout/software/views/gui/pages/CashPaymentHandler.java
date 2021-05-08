package selfcheckout.software.views.gui.pages;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public abstract class CashPaymentHandler {

	protected final ViewStateManager viewStateManager;
	protected final SelfCheckoutController controller;
	protected final JFrame frame;
	protected final CountDownLatch countDownLatch;

	public CashPaymentHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
	}

	protected void handleCashPayment() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}

	public class ReturnToOrderPaymentMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			countDownLatch.countDown();
		}
	}
}
