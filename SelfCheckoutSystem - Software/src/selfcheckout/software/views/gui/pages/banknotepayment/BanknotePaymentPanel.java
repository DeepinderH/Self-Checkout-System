package selfcheckout.software.views.gui.pages.banknotepayment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BanknotePaymentPanel extends JPanel {
	private final JTextField banknoteValueTextField;
	private final JTextField banknoteCurrencyTextField;

	public BanknotePaymentPanel(ActionListener banknoteInsertedListener,
	                            ActionListener returnToOrderPaymentMenuListener) {
		setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel enterBanknoteValueLabel = new JLabel("Enter banknote value (as an integer):");
		add(enterBanknoteValueLabel);
		
		banknoteValueTextField = new JTextField();
		add(banknoteValueTextField);
		banknoteValueTextField.setColumns(10);
		
		JLabel enterBanknoteCurrencyLabel = new JLabel("Enter banknote currency (3 letters):");
		add(enterBanknoteCurrencyLabel);
		
		banknoteCurrencyTextField = new JTextField();
		add(banknoteCurrencyTextField);
		banknoteCurrencyTextField.setColumns(10);
		
		JButton returnToOrderPaymentMenuButton = new JButton("Return to Order Payment Menu");
		returnToOrderPaymentMenuButton.addActionListener(returnToOrderPaymentMenuListener);
		add(returnToOrderPaymentMenuButton);
		
		JButton insertBanknoteButton = new JButton("Insert Banknote");
		insertBanknoteButton.addActionListener(banknoteInsertedListener);
		add(insertBanknoteButton);
	}

	public String getValue() {
		return banknoteValueTextField.getText();
	}

	public String getCurrencyString() {
		return banknoteCurrencyTextField.getText();
	}

}
