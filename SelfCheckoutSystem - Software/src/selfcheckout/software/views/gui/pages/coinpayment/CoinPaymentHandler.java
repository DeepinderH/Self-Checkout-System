package selfcheckout.software.views.gui.pages.coinpayment;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.CoinRejectedException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.CashPaymentHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;

public class CoinPaymentHandler extends CashPaymentHandler {

	private final CoinPaymentPanel coinPaymentPanel;

	public CoinPaymentHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		super(viewStateManager, controller, frame);
		this.coinPaymentPanel = new CoinPaymentPanel(
			new CoinInsertedListener(), new ReturnToOrderPaymentMenuListener());
		this.frame.setContentPane(this.coinPaymentPanel);
		this.frame.pack();
	}

	public void handleCoinPayment() {
		this.handleCashPayment();
	}

	private class CoinInsertedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			String coinValueString = coinPaymentPanel.getValue();
			String coinCurrencyString = coinPaymentPanel.getCurrencyString();
			BigDecimal coinValue;
			try {
				coinValue = new BigDecimal(coinValueString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Invalid coin value! Coins must be of the form '0.25'");
				return;
			}

			Currency coinCurrency;
			try {
				coinCurrency = Currency.getInstance(coinCurrencyString.toUpperCase());
			} catch (NullPointerException | IllegalArgumentException e) {
				JOptionPane.showMessageDialog(frame, "Invalid currency code! Currencies are 3 letters");
				return;
			}
			try {
				controller.insertCoin(coinValue, coinCurrency);
			} catch (CoinRejectedException e) {
				JOptionPane.showMessageDialog(frame, "Coin rejected! Press okay to remove the coin");
				controller.emptyCoinTray();
				countDownLatch.countDown();
				return;
			} catch (StorageUnitFullException e) {
				JOptionPane.showMessageDialog(frame, "Coin storage unit is full! Press okay to remove the coin and pay using a different method");
				controller.emptyCoinTray();
				viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
				countDownLatch.countDown();
				return;
			}
			JOptionPane.showMessageDialog(frame,
				"Coin insertion successful! New payment total is $" + controller.getTotalPayment());
			countDownLatch.countDown();
		}
	}

}
