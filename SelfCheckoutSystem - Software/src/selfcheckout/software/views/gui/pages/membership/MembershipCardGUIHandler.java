package selfcheckout.software.views.gui.pages.membership;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.InvalidCardException;
import selfcheckout.software.controllers.exceptions.NotAMemberException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

public class MembershipCardGUIHandler {
	
	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final MembershipGUIPanel membershipPanel;
	private final CountDownLatch countDownLatch;

	
	public MembershipCardGUIHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;	
		this.membershipPanel = new MembershipGUIPanel(new MembershipCardListener(), new MembershipNumberListener(),
														new ReturnToMainMenuListener());
		this.countDownLatch = new CountDownLatch(1);
		this.frame.setContentPane(membershipPanel);
		this.frame.pack();	
	}
	
	public void handleMembershipCard() {		
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
	private class MembershipCardListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String membershipNumber = membershipPanel.getEnteredMembershipNumber();
			try {
				controller.processMembershipCard(membershipNumber);
				String userProfileMsg = constructMessage();
				JOptionPane.showMessageDialog(frame, userProfileMsg);
			} catch (NotAMemberException | InvalidCardException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage());
			}
			countDownLatch.countDown();
		}
		
	}
	
	private class MembershipNumberListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String membershipNumber = membershipPanel.getEnteredMembershipNumber();
			try {
				controller.processMembershipNumber(membershipNumber);
				String userProfileMsg = constructMessage();
				JOptionPane.showMessageDialog(frame, userProfileMsg);
			} catch (NotAMemberException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage());
			}
			countDownLatch.countDown();
		}
		
	}
	
	private class ReturnToMainMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			viewStateManager.setState(ViewStateEnum.MAIN_MENU);
			countDownLatch.countDown();
		}
		
	}
	
	private String constructMessage() {
		String customerName = controller.getCurrentCustomerName();
		int points = controller.getCurrentAccountPoints();
		return("Hello, " + customerName + "! You currently have " + points + " points.\n");
	}


}

