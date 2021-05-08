package selfcheckout.software.views.gui.pages;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class StationUnblockingHandler {

	private final ViewStateManager viewStateManager;
	private final SelfCheckoutController controller;
	private final JFrame frame;
	private final CountDownLatch countDownLatch;
	private final AttendantLoginPanel attendantLoginPanel;

	public StationUnblockingHandler(
		ViewStateManager viewStateManager, SelfCheckoutController controller,
		JFrame frame, String message
	) {
		this.viewStateManager = viewStateManager;
		this.controller = controller;
		this.frame = frame;
		this.countDownLatch = new CountDownLatch(1);
		this.attendantLoginPanel = new AttendantLoginPanel(
			new ConfirmButtonListener(),
			message,
			"Unblock Station"
		);
		this.frame.setContentPane(attendantLoginPanel);
		this.frame.pack();
	}

	public void handleStationUnblocking() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}

	private class ConfirmButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {
			int attendantID;
			try {
				attendantID = Integer.parseInt(attendantLoginPanel.getAttendantID());
			} catch (NumberFormatException numberFormatException) {
				JOptionPane.showMessageDialog(frame, "Attendant ID must be a number!");
				return;
			}
			try {
				controller.getAttendantConsoleController().unblockStation(
					attendantID,
					attendantLoginPanel.getAttendantPasscode());
			} catch (IncorrectAttendantLoginInformationException exception) {
				JOptionPane.showMessageDialog(frame, "Invalid login credentials!");
				return;
			}
			viewStateManager.setState(ViewStateEnum.MAIN_MENU);
			countDownLatch.countDown();
		}
	}
}
