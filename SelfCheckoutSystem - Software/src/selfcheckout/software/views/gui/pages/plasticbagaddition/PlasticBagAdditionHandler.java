package selfcheckout.software.views.gui.pages.plasticbagaddition;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.WeightOverloadException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class PlasticBagAdditionHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final PlasticBagAdditionPanel plasticBagAdditionPanel;
	private final CountDownLatch countDownLatch;

	public PlasticBagAdditionHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.plasticBagAdditionPanel = new PlasticBagAdditionPanel(new ConfirmButtonListener());
		this.countDownLatch = new CountDownLatch(1);
		this.frame.setContentPane(plasticBagAdditionPanel);
		this.frame.pack();
	}

	public void handlePlasticBagAddition() {
		
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
	private class ConfirmButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			int numberOfBagsUsed = plasticBagAdditionPanel.getNumberOfBagsUsed();
			try {
				controller.addPlasticBagsUsed(numberOfBagsUsed);
			} catch (WeightOverloadException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage());
				return;
			}
			controller.goToOrderPaymentState();
			viewStateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
			countDownLatch.countDown();
		}
	}
}
