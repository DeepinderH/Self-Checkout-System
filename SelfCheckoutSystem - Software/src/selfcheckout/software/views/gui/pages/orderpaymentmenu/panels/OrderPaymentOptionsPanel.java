package selfcheckout.software.views.gui.pages.orderpaymentmenu.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class OrderPaymentOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public OrderPaymentOptionsPanel(
		ActionListener paymentCardOptionListener,
		ActionListener giftCardOptionListener,
		ActionListener insertBanknotesOptionListener,
		ActionListener insertCoinsOptionListener,
		ActionListener returnToItemAdditionButtonListener
	) {
		setLayout(new GridLayout(3, 2, 0, 0));

		JButton payWithPaymentCardButton = new JButton("Pay with Debit/Credit Card");
		payWithPaymentCardButton.addActionListener(paymentCardOptionListener);
		add(payWithPaymentCardButton);

		JButton payWithGiftCardButton = new JButton("Pay with Gift Card");
		payWithGiftCardButton.addActionListener(giftCardOptionListener);
		add(payWithGiftCardButton);

		JButton insertBanknotesButton = new JButton("Insert Banknotes");
		insertBanknotesButton.addActionListener(insertBanknotesOptionListener);
		add(insertBanknotesButton);

		JButton insertCoinsButton = new JButton("Insert Coins");
		insertCoinsButton.addActionListener(insertCoinsOptionListener);
		add(insertCoinsButton);

		JButton returnToItemAdditionButton = new JButton("Add More Items");
		returnToItemAdditionButton.addActionListener(returnToItemAdditionButtonListener);
		add(returnToItemAdditionButton);
	}
}
