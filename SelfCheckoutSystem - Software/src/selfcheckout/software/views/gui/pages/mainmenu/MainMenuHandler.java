package selfcheckout.software.views.gui.pages.mainmenu;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.SelfCheckoutComponent;
import selfcheckout.software.views.gui.pages.mainmenu.panels.MainMenuComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class MainMenuHandler {

	private final ViewStateManager viewStateManager;
	private final CountDownLatch countDownLatch;

	public MainMenuHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.countDownLatch = new CountDownLatch(1);
		MainMenuComponent mainMenuComponent = new MainMenuComponent(
			controller.getCustomerOrderSummary(),
			!controller.getCurrentProducts().isEmpty(),
			new StartOrderPaymentListener(), new ScanItemListener(),
			new EnterPLUCodedItemListener(), new AddOwnBagsListener(),
			new SwipeMembershipCardListener(), new RequestAttendantAssistanceListener(),
			new OpenAttendantMenuListener());
		frame.setContentPane(new SelfCheckoutComponent(mainMenuComponent));
		frame.pack();
	}

	public void handleMainMenu() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}
	private class StartOrderPaymentListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.PAYMENT_START);
			countDownLatch.countDown();
		}
	}

	private class ScanItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ITEM_SCANNING);
			countDownLatch.countDown();
		}
	}

	private class EnterPLUCodedItemListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.PLU_ITEM_INPUT);
			countDownLatch.countDown();
		}
	}

	private class AddOwnBagsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.BAG_ADDITION);
			countDownLatch.countDown();
		}
	}

	private class SwipeMembershipCardListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.MEMBERSHIP_CARD);
			countDownLatch.countDown();
		}
	}

	private class RequestAttendantAssistanceListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.REQUEST_ATTENDANT_ASSISTANCE);
			countDownLatch.countDown();
		}
	}

	private class OpenAttendantMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.ATTENDANT_LOGIN);
			countDownLatch.countDown();
		}
	}
}
