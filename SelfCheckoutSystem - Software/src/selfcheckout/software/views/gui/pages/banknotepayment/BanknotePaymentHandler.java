package selfcheckout.software.views.gui.pages.banknotepayment;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.BanknoteRejectedException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.CashPaymentHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Currency;

public class BanknotePaymentHandler extends CashPaymentHandler {

	private final BanknotePaymentPanel banknotePaymentPanel;

	public BanknotePaymentHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		super(viewStateManager, controller, frame);
		this.banknotePaymentPanel = new BanknotePaymentPanel(
			new BanknoteInsertedListener(), new ReturnToOrderPaymentMenuListener());
		this.frame.setContentPane(this.banknotePaymentPanel);
		this.frame.pack();
	}

	public void handleBanknotePayment() {
		this.handleCashPayment();
	}

	private class BanknoteInsertedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			String banknoteValueString = banknotePaymentPanel.getValue();
			String banknoteCurrencyString = banknotePaymentPanel.getCurrencyString();
			int banknoteValue;
			try {
				banknoteValue = Integer.parseInt(banknoteValueString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Invalid banknote value! Banknotes must be of the form '5'");
				return;
			}

			Currency banknoteCurrency;
			try {
				banknoteCurrency = Currency.getInstance(banknoteCurrencyString.toUpperCase());
			} catch (NullPointerException | IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, "Invalid currency code! Currencies are 3 letters");
				return;
			}
			try {
				controller.insertBanknote(banknoteValue, banknoteCurrency);
			} catch (BanknoteRejectedException e) {
				JOptionPane.showMessageDialog(frame, "Banknote rejected! Press okay to remove the banknote");
				controller.removeDanglingBanknote();
				countDownLatch.countDown();
				return;
			} catch (StorageUnitFullException e) {
				JOptionPane.showMessageDialog(frame, "Banknote storage unit is full! Press okay to remove the banknote and pay using a different method");
				controller.removeDanglingBanknote();
				viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
				countDownLatch.countDown();
				return;
			}
			JOptionPane.showMessageDialog(frame,
				"Banknote insertion successful! New payment total is $" + controller.getTotalPayment());
			countDownLatch.countDown();
		}
	}

}
