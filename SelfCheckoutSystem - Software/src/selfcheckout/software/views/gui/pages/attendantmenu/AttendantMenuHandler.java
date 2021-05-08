package selfcheckout.software.views.gui.pages.attendantmenu;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

public class AttendantMenuHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;

	public AttendantMenuHandler(
		ViewStateManager viewStateManager, SelfCheckoutController controller,
		JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
		RemoveItemFromOrderListener removeItemFromOrderListener = null;
		if (!this.controller.getCurrentProducts().isEmpty()) {
			removeItemFromOrderListener = new RemoveItemFromOrderListener();
		}

		AttendantMenuPanel attendantMenuPanel = new AttendantMenuPanel(
			new LogOutListener(),
			removeItemFromOrderListener, new LookUpProductListener(),
			new RefillReceiptPaperListener(), new RefillReceiptInkListener(),
			new EmptyStationCoinsListener(), new EmptyStationBanknotesListener(),
			new RefillStationCoinsListener(), new RefillStationBanknotesListener(),
			new BlockStationListener(), new ShutDownStationListener()
		);

		this.frame.setContentPane(attendantMenuPanel);
		this.frame.pack();
	}

	public void handleAttendantMenu() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}

	private class LogOutListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.goToItemAdditionState();
			viewStateManager.setState(ViewStateEnum.MAIN_MENU);
			countDownLatch.countDown();
		}
	}

	private class RemoveItemFromOrderListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ITEM_REMOVAL);
			countDownLatch.countDown();
		}
	}

	private class LookUpProductListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_PRODUCT_LOOKUP);
			countDownLatch.countDown();
		}
	}

	private class RefillReceiptPaperListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_RECEIPT_PAPER_ADDITION);
			countDownLatch.countDown();
		}
	}

	private class RefillReceiptInkListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_RECEIPT_INK_ADDITION);
			countDownLatch.countDown();
		}
	}

	private class EmptyStationCoinsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			BigDecimal coinValue = controller.getAttendantConsoleController().emptyCoinStorageUnit();
			JOptionPane.showMessageDialog(frame, "Coins have been emptied! Total value was $" + coinValue.toPlainString());
		}
	}

	private class EmptyStationBanknotesListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			BigDecimal coinValue = controller.getAttendantConsoleController().emptyBanknoteStorageUnit();
			JOptionPane.showMessageDialog(frame, "Banknotes have been emptied! Total value was $" + coinValue.toPlainString());
		}
	}

	private class RefillStationCoinsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_REFILL_COINS);
			countDownLatch.countDown();
		}
	}

	private class RefillStationBanknotesListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_REFILL_BANKNOTES);
			countDownLatch.countDown();
		}
	}

	private class BlockStationListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.getAttendantConsoleController().blockStation();
			viewStateManager.setState(ViewStateEnum.DISABLED);
			countDownLatch.countDown();
		}
	}

	private class ShutDownStationListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			controller.getAttendantConsoleController().shutdownStation();
			viewStateManager.setState(ViewStateEnum.EXIT_APPLICATION);
			countDownLatch.countDown();
		}
	}
}
