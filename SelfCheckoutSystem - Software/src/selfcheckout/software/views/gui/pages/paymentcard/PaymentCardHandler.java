package selfcheckout.software.views.gui.pages.paymentcard;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.InvalidPaymentException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;

public class PaymentCardHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;
	private final PaymentCardPanel paymentCardPanel;

	public PaymentCardHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
		this.paymentCardPanel = new PaymentCardPanel(
			new CardTappedListener(),
			new CardSwipedListener(),
			new CardInsertedListener(),
			new ReturnToOrderPaymentMenuListener());
		this.frame.setContentPane(this.paymentCardPanel);
		this.frame.pack();
	}

	public void handlePaymentCardUse() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}

	private class CardSwipedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			String signatureText = paymentCardPanel.getSignature();
			if (!signatureText.equals(paymentCardPanel.getCardholderName())) {
				JOptionPane.showMessageDialog(frame, "Invalid signature!");
				return;
			}
			BufferedImage signature = new BufferedImage(600,100,BufferedImage.TYPE_INT_RGB);
			signature.createGraphics().drawString(signatureText, 0, 0);

			try {
				controller.swipePaymentCard(
					paymentCardPanel.getCardType(),
					paymentCardPanel.getCardNumber(),
					paymentCardPanel.getCardholderName(),
					paymentCardPanel.getCVV(),
					paymentCardPanel.getPin(),
					false, false,
					signature
				);
			} catch (InvalidPaymentException e) {
				JOptionPane.showMessageDialog(frame, "Could not process card swipe. Please double check the card data and try again");
				return;
			}
			JOptionPane.showMessageDialog(frame, "Swipe successful!");
			viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			countDownLatch.countDown();
		}
	}

	private class CardInsertedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				controller.insertPaymentCard(
					paymentCardPanel.getCardType(),
					paymentCardPanel.getCardNumber(),
					paymentCardPanel.getCardholderName(),
					paymentCardPanel.getCVV(),
					paymentCardPanel.getPin(),
					false, true
				);
			} catch (InvalidPaymentException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage() + System.lineSeparator() + "Press okay to remove card");
				controller.removeCard();
				return;
			}
			JOptionPane.showMessageDialog(frame, "Card insertion successful! Press okay to remove card");
			controller.removeCard();
			viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			countDownLatch.countDown();
		}
	}


	private class CardTappedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			try {
				controller.tapPaymentCard(
					paymentCardPanel.getCardType(),
					paymentCardPanel.getCardNumber(),
					paymentCardPanel.getCardholderName(),
					paymentCardPanel.getCVV(),
					paymentCardPanel.getPin(),
					true, false
				);
			} catch (InvalidPaymentException e) {
				JOptionPane.showMessageDialog(frame, "Could not process card tap. Please double check the card data and try again");
				return;
			}
			JOptionPane.showMessageDialog(frame, "Card tap successful!");
			viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			countDownLatch.countDown();
		}
	}

	private class ReturnToOrderPaymentMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			countDownLatch.countDown();
		}
	}

}
