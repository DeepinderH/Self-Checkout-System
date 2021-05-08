package selfcheckout.software.views.gui.pages.orderpaymentmenu.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class OrderPaymentMenuComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public OrderPaymentMenuComponent(
		BigDecimal amountOwed, ActionListener canFinishPaymentListener,
		ActionListener paymentCardOptionListener, ActionListener giftCardOptionListener,
		ActionListener insertBanknotesOptionListener, ActionListener insertCoinsOptionListener,
		ActionListener addMoreItemsListener
	) {
		setLayout(new GridLayout(2, 1, 0, 0));

		JPanel orderPaymentStatusPanel = new OrderPaymentStatusPanel(amountOwed);
		add(orderPaymentStatusPanel);

		if (canFinishPaymentListener != null) {
			// paid full amount
			this.addFinishPaymentButton(canFinishPaymentListener);
		} else {
			// still missing money for order
			JPanel orderPaymentOptionsPanel = new OrderPaymentOptionsPanel(
					paymentCardOptionListener, giftCardOptionListener,
					insertBanknotesOptionListener, insertCoinsOptionListener,
					addMoreItemsListener);
			add(orderPaymentOptionsPanel);
		}
	}

	private void addFinishPaymentButton(ActionListener finishPaymentListener) {
		JButton finishOrderButton = new JButton("Finish Order");
		finishOrderButton.addActionListener(finishPaymentListener);
		add(finishOrderButton);
	}


}
