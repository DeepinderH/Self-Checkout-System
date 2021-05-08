package selfcheckout.software.views.gui.pages.attendantproductlookup;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AttendantTextFieldActionHandler;
import selfcheckout.software.views.gui.pages.TextFieldButtonAttendantPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class AttendantProductLookUpHandler extends AttendantTextFieldActionHandler {

	public AttendantProductLookUpHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller,
			JFrame frame) {
		super(viewStateManager, controller, frame);
		this.attendantTextButtonActionPanel = new TextFieldButtonAttendantPanel(
			"Product Description:", "Search for Products",
			new DescriptionLookUpListener(), new ReturnToAttendantMenuListener());
		this.frame.setContentPane(this.attendantTextButtonActionPanel);
		this.frame.pack();
	}

	private class DescriptionLookUpListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String description = attendantTextButtonActionPanel.getInputText();
			String relevantProducts = controller.getAttendantConsoleController().lookUpProductByDescription(description);
			JOptionPane.showMessageDialog(frame, relevantProducts);
		}
	}
}
