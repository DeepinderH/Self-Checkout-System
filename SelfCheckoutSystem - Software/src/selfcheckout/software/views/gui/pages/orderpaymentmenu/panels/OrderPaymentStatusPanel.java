package selfcheckout.software.views.gui.pages.orderpaymentmenu.panels;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

class OrderPaymentStatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public OrderPaymentStatusPanel(BigDecimal amountOwed) {
		setLayout(new GridLayout(1, 1, 0, 0));

		String paymentStatusText;

		int compareToResult = BigDecimal.ZERO.compareTo(amountOwed);

		if (compareToResult == 0) {
			paymentStatusText = "Order fully paid! Press Finish Order to continue";
		} else if (compareToResult < 0) {
			paymentStatusText = "Amount you owe: $" + amountOwed.toPlainString();
		} else {
			paymentStatusText = "Order fully paid! Change to be returned when 'Finish Order' is pressed: $" +
								BigDecimal.ZERO.subtract(amountOwed).toPlainString();
		}

		JTextPane orderStatusTextPane = new JTextPane();
		orderStatusTextPane.setText(paymentStatusText);
		add(orderStatusTextPane);
	}
}
