package selfcheckout.software.views.gui.pages.mainmenu.panels;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;

class MainMenuOrderStatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public MainMenuOrderStatusPanel(
		String orderSummaryText,
		boolean canPayForOrder, ActionListener startOrderPaymentListener) {
		setLayout(new GridLayout(1, 2, 0, 0));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);

		JTextPane orderStatusTextPane = new JTextPane();
		scrollPane.setViewportView(orderStatusTextPane);
		orderStatusTextPane.setText(orderSummaryText);

		if (canPayForOrder) {
			this.addPayForOrderButton(startOrderPaymentListener);
		}
	}

	private void addPayForOrderButton(ActionListener startOrderPaymentListener) {
		JButton payForOrderButton = new JButton("Pay For Order");
		payForOrderButton.addActionListener(startOrderPaymentListener);
		add(payForOrderButton);
	}
}
