package selfcheckout.software.views.gui.pages.orderpaymentmenu;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.OrderIncompleteException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.orderpaymentmenu.panels.OrderPaymentMenuComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

public class OrderPaymentMenuHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;

	public OrderPaymentMenuHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
		OrderPaymentMenuComponent orderPaymentMenuComponent;

		BigDecimal amountOwed = this.controller.getAmountOwedByCustomer();

		if (BigDecimal.ZERO.compareTo(amountOwed) >= 0) {
			// if we have either paid or we need to return change
			orderPaymentMenuComponent = new OrderPaymentMenuComponent(
				amountOwed, new FinishOrderListener(),
				null, null,
				null, null,
				null);
		} else {
			orderPaymentMenuComponent = new OrderPaymentMenuComponent(
				amountOwed, null,
				new PaymentCardOptionListener(), new GiftCardOptionListener(),
				new InsertBanknotesOptionListener(), new InsertCoinsOptionListener(),
				new ReturnToItemAdditionListener());
		}

		this.frame.setContentPane(orderPaymentMenuComponent);
		this.frame.pack();
	}

	public void handleOrderPaymentMenu() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}
	private class FinishOrderListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			BigDecimal changeReturned;
			try {
				changeReturned = controller.finishOrder();
			} catch (OrderIncompleteException orderIncompleteException) {
				JOptionPane.showMessageDialog(frame, orderIncompleteException.getMessage());
				return;
			}
			if (BigDecimal.ZERO.compareTo(changeReturned) < 0) {
				// they are owed change
				String changeReturnedString = changeReturned.setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
				JOptionPane.showMessageDialog(frame,  "$" + changeReturnedString + " has been returned." +
																" Press okay to collect banknotes and empty coin tray");
				controller.removeChange();
			}
			viewStateManager.setState(ViewStateEnum.FINISH_PURCHASE);
			countDownLatch.countDown();
		}
	}

	private class PaymentCardOptionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.PAYMENT_CARD_MENU);
			countDownLatch.countDown();
		}
	}

	private class GiftCardOptionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.GIFT_CARD_SWIPE);
			countDownLatch.countDown();
		}
	}

	private class InsertBanknotesOptionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.BANKNOTE_PAYMENT);
			countDownLatch.countDown();
		}
	}

	private class InsertCoinsOptionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.COIN_PAYMENT);
			countDownLatch.countDown();
		}
	}

	private class ReturnToItemAdditionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.goToItemAdditionState();
			viewStateManager.setState(ViewStateEnum.MAIN_MENU);
			countDownLatch.countDown();
		}
	}
}
