package selfcheckout.software.views.gui.pages.attendantrefillcoins;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.RefillCoinException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.RefillCashPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.CountDownLatch;

public class AttendantRefillCoinsHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;
	private final RefillCashPanel refillCoinsPanel;

	public AttendantRefillCoinsHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.refillCoinsPanel = new RefillCashPanel(
			"Coin",
			new RefillCoinsListener(),
			new ReturnToAttendantMenuListener());
		this.countDownLatch = new CountDownLatch(1);
		this.frame.setContentPane(this.refillCoinsPanel);
		this.frame.pack();
	}

	public void handleAttendantRefillCoins() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}

	private class RefillCoinsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			String coinValueString = refillCoinsPanel.getValue();
			BigDecimal coinValue;
			try {
				coinValue = new BigDecimal(coinValueString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Coin value is not valid. It must be of the form '0.25'");
				return;
			}

			String currencyString = refillCoinsPanel.getCurrency();
			Currency currency;
			try {
				currency = Currency.getInstance(currencyString);
			} catch (NullPointerException | IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, "Currency is invalid. It must be three letters.");
				return;
			}

			int numCoinsInput = refillCoinsPanel.getNumCash();

			try {
				controller.getAttendantConsoleController().refillCoinDispenser(
					coinValue, currency, numCoinsInput);
			} catch (RefillCoinException e) {
				JOptionPane.showMessageDialog(frame, e.getLocalizedMessage());
				return;
			}
			JOptionPane.showMessageDialog(frame, "Coins added successfully!");
			countDownLatch.countDown();
		}
	}

	private class ReturnToAttendantMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_MENU);
			countDownLatch.countDown();
		}
	}
}
