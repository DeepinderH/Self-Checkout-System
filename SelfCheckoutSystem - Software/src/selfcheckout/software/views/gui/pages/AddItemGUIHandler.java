package selfcheckout.software.views.gui.pages;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public abstract class AddItemGUIHandler {

	protected final ViewStateManager viewStateManager;
	protected final SelfCheckoutController controller;
	protected final JFrame frame;
	protected AddItemPanel addItemPanel;
	protected final CountDownLatch countDownLatch;


	public AddItemGUIHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
	}

	public class ReturnToMainMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.MAIN_MENU);
			countDownLatch.countDown();
		}
	}
}
