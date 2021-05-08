package selfcheckout.software.views.gui.pages.attendantapproveweight;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.WeightOverloadException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AttendantLoginPanel;
import selfcheckout.software.views.gui.pages.giftcardpayment.GiftCardPaymentPanel;

public class AttendantWeightDiscrepancyHandler {
	
		private final ViewStateManager viewStateManager;
		private final SelfCheckoutController controller;
		private final JFrame frame;
		private final AttendantLoginPanel weightApprovalPanel;
		private final CountDownLatch countDownLatch;
		private final String panelMessage = "Bagging area weight does not match scanning scale weight. Enter attendant credentials to override.";
		private final String buttonMessage = "APPROVE WEIGHT DISCREPANCY";

		
		public AttendantWeightDiscrepancyHandler(
				ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
			this.viewStateManager = viewStateManager;
			this.controller = controller;
			this.frame = frame;	
			this.weightApprovalPanel = new AttendantLoginPanel(new ApproveWeightListener(),
					panelMessage, 
					buttonMessage);
			this.countDownLatch = new CountDownLatch(1);
			this.frame.setContentPane(weightApprovalPanel);
			this.frame.pack();
		}
		
		public void handleWeightDiscrepancyApproval() {
			try {
				this.countDownLatch.await();
			} catch (InterruptedException e) {
				// ignored, either an actions has happened or this has been
				// interrupted, either way the expected action will occur
			}
		}
		
		private class ApproveWeightListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent event) {
				String attendantID = weightApprovalPanel.getAttendantID();
				int attendantIDNumber = Integer.parseInt(attendantID);
				String attendantPassword = weightApprovalPanel.getAttendantPasscode();
				try {
					controller.getAttendantConsoleController().approveLastItemWeight(attendantIDNumber, attendantPassword);
					viewStateManager.setState(ViewStateEnum.MAIN_MENU);
					countDownLatch.countDown();
				} catch (IncorrectAttendantLoginInformationException e) {
					JOptionPane.showMessageDialog(frame, e.getMessage());
				} catch(WeightOverloadException e) {
					JOptionPane.showMessageDialog(frame, e.getMessage());
					viewStateManager.setState(ViewStateEnum.MAIN_MENU);
					countDownLatch.countDown();
				}
			}
			
	}

}
