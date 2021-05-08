package selfcheckout.software.views.gui.pages.mainmenu.panels;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;

class MainMenuOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public MainMenuOptionsPanel(
		ActionListener scanItemListener,
		ActionListener enterPLUCodedItemListener,
		ActionListener addOwnBagsListener,
		ActionListener swipeMembershipCardListener,
		ActionListener requestAttendantAssistanceListener,
		ActionListener openAttendantMenuListener
	) {
		setLayout(new GridLayout(3, 2, 0, 0));

		JButton scanItemButton = new JButton("Scan Item");
		scanItemButton.addActionListener(scanItemListener);
		add(scanItemButton);

		JButton enterPLUCodedItemButton = new JButton("Enter PLU Coded Item");
		enterPLUCodedItemButton.addActionListener(enterPLUCodedItemListener);
		add(enterPLUCodedItemButton);

		JButton addOwnBagsButton = new JButton("Add Own Bags");
		addOwnBagsButton.addActionListener(addOwnBagsListener);
		add(addOwnBagsButton);

		JButton swipeMembershipCardButton = new JButton("Swipe/Enter Membership Card");
		swipeMembershipCardButton.addActionListener(swipeMembershipCardListener);
		add(swipeMembershipCardButton);

		JButton requestAttendantAssistanceButton = new JButton("Request Attendant Assistance");
		requestAttendantAssistanceButton.addActionListener(requestAttendantAssistanceListener);
		add(requestAttendantAssistanceButton);

		JButton openAttendantMenuButton = new JButton("Open Attendant Menu");
		openAttendantMenuButton.addActionListener(openAttendantMenuListener);
		add(openAttendantMenuButton);
	}
}
