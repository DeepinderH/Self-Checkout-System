package selfcheckout.software.views.gui.pages.attendantreceiptinkaddition;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.InvalidPrinterRefillException;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AttendantTextFieldActionHandler;
import selfcheckout.software.views.gui.pages.TextFieldButtonAttendantPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AttendantRefillReceiptInkHandler extends AttendantTextFieldActionHandler {

	public AttendantRefillReceiptInkHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		super(viewStateManager, controller, frame);
		this.attendantTextButtonActionPanel = new TextFieldButtonAttendantPanel(
			"Quantity of Ink to Add:", "Add Ink",
			new AddInkButtonListener(), new ReturnToAttendantMenuListener());
		this.frame.setContentPane(this.attendantTextButtonActionPanel);
		this.frame.pack();
	}

	private class AddInkButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String quantityString = attendantTextButtonActionPanel.getInputText();
			int quantity;
			try {
				quantity = Integer.parseInt(quantityString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(frame, "Number of units must be an integer");
				return;
			}
			try {
				controller.getAttendantConsoleController().addInkToReceiptPrinter(quantity);
			} catch (InvalidPrinterRefillException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage());
				return;
			}
			JOptionPane.showMessageDialog(frame, "Successfully added ink!");
			countDownLatch.countDown();
		}
	}
}
