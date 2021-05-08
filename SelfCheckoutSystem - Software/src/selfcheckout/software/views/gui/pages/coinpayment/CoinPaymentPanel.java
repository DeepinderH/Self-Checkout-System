package selfcheckout.software.views.gui.pages.coinpayment;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.GridLayout;

public class CoinPaymentPanel extends JPanel {
	private final JTextField coinValueTextField;
	private final JTextField coinCurrencyTextField;

	public CoinPaymentPanel(ActionListener coinInsertedListener,
	                        ActionListener returnToOrderPaymentMenuListener) {
		setLayout(new GridLayout(3, 2, 0, 0));
		
		JLabel enterCoinValueLabel = new JLabel("Enter coin value (as a number with 2 decimal digits):");
		add(enterCoinValueLabel);
		
		coinValueTextField = new JTextField();
		add(coinValueTextField);
		coinValueTextField.setColumns(10);
		
		JLabel enterCoinCurrencyLabel = new JLabel("Enter coin currency (3 letters):");
		add(enterCoinCurrencyLabel);
		
		coinCurrencyTextField = new JTextField();
		add(coinCurrencyTextField);
		coinCurrencyTextField.setColumns(10);
		
		JButton returnToOrderPaymentMenuButton = new JButton("Return to Order Payment Menu");
		returnToOrderPaymentMenuButton.addActionListener(returnToOrderPaymentMenuListener);
		add(returnToOrderPaymentMenuButton);
		
		JButton insertCoinButton = new JButton("Insert Coin");
		insertCoinButton.addActionListener(coinInsertedListener);
		add(insertCoinButton);
	}

	public String getValue() {
		return coinValueTextField.getText();
	}

	public String getCurrencyString() {
		return coinCurrencyTextField.getText();
	}

}
