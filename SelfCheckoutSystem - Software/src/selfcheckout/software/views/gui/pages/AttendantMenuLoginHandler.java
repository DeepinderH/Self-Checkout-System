package selfcheckout.software.views.gui.pages;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class AttendantMenuLoginHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;
	private final AttendantLoginPanel attendantLoginPanel;

	public AttendantMenuLoginHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame, String message) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
		this.attendantLoginPanel = new AttendantLoginPanel(new ProcessLoginListener(), message, "Log in");
		this.frame.setContentPane(attendantLoginPanel);
		this.frame.pack();
	}

	public void handleAttendantLogin() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}
	private class ProcessLoginListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int attendantID;
			try {
				attendantID = Integer.parseInt(attendantLoginPanel.getAttendantID());
			} catch (NumberFormatException numberFormatException) {
				JOptionPane.showMessageDialog(frame, "Attendant ID must be a number!");
				return;
			}
			try {
				controller.getAttendantConsoleController().loginAsAttendant(
					attendantID,
					attendantLoginPanel.getAttendantPasscode());
			} catch (IncorrectAttendantLoginInformationException exception) {
				JOptionPane.showMessageDialog(frame, "Invalid login credentials!");
				return;
			}
			viewStateManager.setState(ViewStateEnum.ATTENDANT_MENU);
			countDownLatch.countDown();
		}
	}
}
