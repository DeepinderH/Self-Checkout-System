package selfcheckout.software.views.gui.pages.giftcardpayment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.InvalidCardException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

public class GiftCardGUIHandler {
	

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final GiftCardPaymentPanel giftCardPanel;
	private final CountDownLatch countDownLatch;

	
	public GiftCardGUIHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;	
		this.giftCardPanel = new GiftCardPaymentPanel(new GiftCardListener(),new ReturnToPaymentMenuListener());
		this.countDownLatch = new CountDownLatch(1);
		this.frame.setContentPane(giftCardPanel);
		this.frame.pack();
	}
	
	public void handleGiftCard() {	
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}
	
	/*
	 * Listener to monitors button press events in the membership card panel
	 * Sends entered number to the controller for processing 
	 */
	private class GiftCardListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String giftCardNumber = giftCardPanel.getGiftCardNumber();
			try {
				controller.payUntilNoBalanceWithGiftCard(giftCardNumber);
				String userProfileMsg = constructMessage(giftCardNumber);
				JOptionPane.showMessageDialog(frame, userProfileMsg);
				viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			} catch (InvalidCardException e) {
				JOptionPane.showMessageDialog(frame, "Your card did not match what we have on file.");
			}
			countDownLatch.countDown();
		}
		
	}
	
	private class ReturnToPaymentMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			countDownLatch.countDown();
		}
		
	}
	
	private String constructMessage(String giftCardNumber) {
		BigDecimal remainingCost = controller.getAmountOwedByCustomer();
		BigDecimal bigZero = new BigDecimal(0);
		if (remainingCost.compareTo(bigZero) > 0) {
			return "You still owe $" + remainingCost + ". Please select another payment type to complete the transaction.\n";
		} else {
			BigDecimal remainingGiftCardBalance = controller.getGiftCardBalance(giftCardNumber);
			return "Payment completed.\nYou still have $" + remainingGiftCardBalance + " left on your gift card.";
			
		}
	}



}
