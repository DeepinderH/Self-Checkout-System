package selfcheckout.software.views.gui.pages.bagging;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

public class GUIBaggingHandler {
	
	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final GUIBaggingPanel baggingPanel;
	private final CountDownLatch countDownLatch;

	
	public GUIBaggingHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;	
		this.baggingPanel = new GUIBaggingPanel(
			new BaggingWeightListener(), new RefuseBagListener());
		this.countDownLatch = new CountDownLatch(1);
		this.frame.setContentPane(baggingPanel);
		this.frame.pack();
	}
	
	public void handleItemBagging() {		
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}
	

	private class BaggingWeightListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String baggingScaleWeightString = baggingPanel.getBaggingScaleWeight();
			double baggingScaleWeight;
			try {
				baggingScaleWeight = Double.parseDouble(baggingScaleWeightString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Weight is not a number!");
				return;
			}

			try {
				controller.bagLastItem(baggingScaleWeight);
				JOptionPane.showMessageDialog(frame, controller.getCustomerOrderSummary());
				// successful bagging of item, go to main menu
				viewStateManager.setState(ViewStateEnum.MAIN_MENU);
				countDownLatch.countDown();
			} catch (WeightOverloadException e) {
				// the weight scale is overloaded
				JOptionPane.showMessageDialog(frame, e.getMessage());
				// item will be automatically removed from order
				// so just return to main menu
				viewStateManager.setState(ViewStateEnum.MAIN_MENU);
				countDownLatch.countDown();
			} catch (InvalidWeightException | WeightMismatchException e) {
				// user entered something invalid
				// inform user that weights did not match or there was an error
				JOptionPane.showMessageDialog(frame, e.getMessage());
				// take the user to the weight discrepancy screen where the
				// attendant must approve for us to continue
				viewStateManager.setState(ViewStateEnum.ATTENDANT_WEIGHT_DISCREPANCY_APPROVAL);
				countDownLatch.countDown();
			}
		}
	}

	private class RefuseBagListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			viewStateManager.setState(ViewStateEnum.SKIP_BAGGING_ITEM);
			countDownLatch.countDown();
		}
	}
}
	
