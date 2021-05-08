package selfcheckout.software.views.gui.pages.removeitem;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class RemoveItemHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;
	private final RemoveItemPanel removeItemPanel;

	public RemoveItemHandler(
		ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
		this.removeItemPanel = new RemoveItemPanel(
			this.controller.getCustomerOrderSummary(),
			new ReturnToMainMenuListener(),
			new RemoveItemListener());
		this.frame.setContentPane(this.removeItemPanel);
		this.frame.pack();
	}

	public void handleRemoveItem() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}

	private class ReturnToMainMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_MENU);
			countDownLatch.countDown();
		}
	}

	private class RemoveItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			String itemIndexString = removeItemPanel.getItemIndex();
			int itemIndex;
			try {
				itemIndex = Integer.parseInt(itemIndexString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Invalid item index");
				return;
			}
			int numItems = controller.getNumberOfItems();
			if (itemIndex < 1 || numItems < itemIndex) {
				JOptionPane.showMessageDialog(frame, "Invalid item index");
				return;
			}
			controller.getAttendantConsoleController().removeItem(itemIndex - 1);
			JOptionPane.showMessageDialog(frame, "Item " + itemIndex + " successfully removed!");
			if (numItems == 1) {
				// no more items
				viewStateManager.setState(ViewStateEnum.ATTENDANT_MENU);
			}
			countDownLatch.countDown();
		}
	}

}
