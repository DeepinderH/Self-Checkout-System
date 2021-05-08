package selfcheckout.software.views.gui.pages.attendantreceiptpaperaddition;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.InvalidPrinterRefillException;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AttendantTextFieldActionHandler;
import selfcheckout.software.views.gui.pages.TextFieldButtonAttendantPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AttendantRefillReceiptPaperHandler extends AttendantTextFieldActionHandler {

	public AttendantRefillReceiptPaperHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		super(viewStateManager, controller, frame);
		this.attendantTextButtonActionPanel = new TextFieldButtonAttendantPanel(
			"Units of Paper to Add:", "Add Paper",
			new AddPaperButtonListenerListener(), new ReturnToAttendantMenuListener());
		this.frame.setContentPane(this.attendantTextButtonActionPanel);
		this.frame.pack();
	}

	private class AddPaperButtonListenerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String unitsString = attendantTextButtonActionPanel.getInputText();
			int units;
			try {
				units = Integer.parseInt(unitsString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Number of units must be an integer");
				return;
			}
			try {
				controller.getAttendantConsoleController().addPaperToReceiptPrinter(units);
			} catch (InvalidPrinterRefillException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage());
				return;
			}
			JOptionPane.showMessageDialog(frame, "Successfully added paper!");
			countDownLatch.countDown();
		}
	}
}
