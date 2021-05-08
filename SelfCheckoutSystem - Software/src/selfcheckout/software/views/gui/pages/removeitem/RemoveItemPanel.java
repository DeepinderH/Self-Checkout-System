package selfcheckout.software.views.gui.pages.removeitem;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JButton;

public class RemoveItemPanel extends JPanel {
	private final JTextField itemIndexTextField;

	/**
	 * Create the panel.
	 */
	public RemoveItemPanel(
			String currentOrderSummary,
			ActionListener returnToAttendantMenuListener,
			ActionListener removeItemButtonListener) {
		setLayout(new GridLayout(1, 0, 0, 0));

		JTextPane currentItemSummaryPane = new JTextPane();
		currentItemSummaryPane.setText(currentOrderSummary);
		add(currentItemSummaryPane);

		JPanel rightPanel = new JPanel();
		add(rightPanel);
		rightPanel.setLayout(new GridLayout(2, 2, 0, 0));

		JLabel itemNumberToRemoveLabel = new JLabel("Item Number to Remove:");
		rightPanel.add(itemNumberToRemoveLabel);

		itemIndexTextField = new JTextField();
		rightPanel.add(itemIndexTextField);
		itemIndexTextField.setColumns(10);

		JButton returnToAttendantMenuButton = new JButton("Return To Attendant Menu:");
		returnToAttendantMenuButton.addActionListener(returnToAttendantMenuListener);
		rightPanel.add(returnToAttendantMenuButton);

		JButton removeItemButton = new JButton("Remove Item");
		removeItemButton.addActionListener(removeItemButtonListener);
		rightPanel.add(removeItemButton);
	}

	public String getItemIndex() {
		return itemIndexTextField.getText();
	}
}
