package selfcheckout.software.views.gui.pages.attendantrefillbanknotes;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.RefillBanknoteException;
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

public class AttendantRefillBanknotesHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;
	private final RefillCashPanel refillBanknotesPanel;

	public AttendantRefillBanknotesHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.refillBanknotesPanel = new RefillCashPanel(
			"Banknote",
			new RefillCoinsListener(),
			new ReturnToAttendantMenuListener());
		this.countDownLatch = new CountDownLatch(1);
		this.frame.setContentPane(this.refillBanknotesPanel);
		this.frame.pack();
	}

	public void handleAttendantRefillBanknotes() {
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
			String banknoteValueString = refillBanknotesPanel.getValue();
			int banknoteValue;
			try {
				banknoteValue = Integer.parseInt(banknoteValueString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Banknote value is not valid. It must be of the form '5'");
				return;
			}

			String currencyString = refillBanknotesPanel.getCurrency();
			Currency currency;
			try {
				currency = Currency.getInstance(currencyString);
			} catch (NullPointerException | IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, "Currency is invalid. It must be three letters.");
				return;
			}

			int numBanknotesInput = refillBanknotesPanel.getNumCash();

			try {
				controller.getAttendantConsoleController().refillBanknoteDispenser(
					banknoteValue, currency, numBanknotesInput);
			} catch (RefillBanknoteException e) {
				JOptionPane.showMessageDialog(frame, e.getLocalizedMessage());
				return;
			}
			JOptionPane.showMessageDialog(frame, "Banknotes added successfully!");
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
