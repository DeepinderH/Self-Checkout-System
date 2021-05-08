package selfcheckout.software.views.gui.pages.skipbagging;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.WeightOverloadException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AttendantLoginPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class SkipBaggingHandler {
	
		private final ViewStateManager viewStateManager;
		private final SelfCheckoutController controller;
		private final JFrame frame;
		private final AttendantLoginPanel skipBaggingApprovalPanel;
		private final CountDownLatch countDownLatch;
		private static final String panelMessage = "Customer has requested to skip item bagging. Enter attendant credentials to continue.";
		private static final String buttonMessage = "SKIP ITEM BAGGING";

		
		public SkipBaggingHandler(
				ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
			this.viewStateManager = viewStateManager;
			this.controller = controller;
			this.frame = frame;	
			this.skipBaggingApprovalPanel = new AttendantLoginPanel(new SkipBaggingListener(),
					panelMessage, 
					buttonMessage);
			this.countDownLatch = new CountDownLatch(1);
			this.frame.setContentPane(skipBaggingApprovalPanel);
			this.frame.pack();
		}
		
		public void handleSkipBaggingApproval() {
			try {
				this.countDownLatch.await();
			} catch (InterruptedException e) {
				// ignored, either an actions has happened or this has been
				// interrupted, either way the expected action will occur
			}
		}
		
		private class SkipBaggingListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent event) {
				String attendantID = skipBaggingApprovalPanel.getAttendantID();
				int attendantIDNumber = Integer.parseInt(attendantID);
				String attendantPassword = skipBaggingApprovalPanel.getAttendantPasscode();
				try {
					controller.getAttendantConsoleController().skipBaggingLastItem(attendantIDNumber, attendantPassword);
					viewStateManager.setState(ViewStateEnum.MAIN_MENU);
					countDownLatch.countDown();
				} catch (IncorrectAttendantLoginInformationException e) {
					JOptionPane.showMessageDialog(frame, e.getMessage());
				}
			}
			
	}

}
