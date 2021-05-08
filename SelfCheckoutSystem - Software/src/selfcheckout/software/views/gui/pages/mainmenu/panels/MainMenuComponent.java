package selfcheckout.software.views.gui.pages.mainmenu.panels;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class MainMenuComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.s
	 */
	public MainMenuComponent(
		String orderSummaryText,
		boolean canPayForOrder,
		ActionListener startOrderPaymentListener,
		ActionListener scanItemListener,
		ActionListener enterPLUCodedItemListener,
		ActionListener addOwnBagsListener,
		ActionListener swipeMembershipCardListener,
		ActionListener requestAttendantAssistanceListener,
		ActionListener openAttendantMenuListener
	) {
		setLayout(new GridLayout(2, 1, 0, 0));

		JPanel mainMenuOrderStatusPanel = new MainMenuOrderStatusPanel(
			orderSummaryText, canPayForOrder, startOrderPaymentListener);
		add(mainMenuOrderStatusPanel);

		JPanel mainMenuOptionsPanel = new MainMenuOptionsPanel(
			scanItemListener, enterPLUCodedItemListener,
			addOwnBagsListener, swipeMembershipCardListener,
			requestAttendantAssistanceListener, openAttendantMenuListener);
		add(mainMenuOptionsPanel);
	}
}
