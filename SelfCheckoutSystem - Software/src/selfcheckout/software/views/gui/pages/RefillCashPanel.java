package selfcheckout.software.views.gui.pages;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.GridLayout;

public class RefillCashPanel extends JPanel {
	private final JTextField cashValueTextField;
	private final JTextField currencyTextField;
	private final JSpinner numCashSpinner;

	public RefillCashPanel(
			String cashType,
			ActionListener refillCashButtonListener,
			ActionListener returnToAttendantMenuListener) {
		setLayout(new GridLayout(4, 2, 0, 0));
		
		JLabel coinValueLabel = new JLabel(cashType + " Value:");
		add(coinValueLabel);
		
		cashValueTextField = new JTextField();
		add(cashValueTextField);
		cashValueTextField.setColumns(10);
		
		JLabel valueLabel = new JLabel(cashType + " Currency:");
		add(valueLabel);
		
		currencyTextField = new JTextField();
		add(currencyTextField);
		currencyTextField.setColumns(10);

		JLabel numValuesLabel = new JLabel("Number of " + cashType + "s:");
		add(numValuesLabel);
		
		numCashSpinner = new JSpinner();
		numCashSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
		add(numCashSpinner);

		JButton returnToAttendantMenuButton = new JButton("Return to Attendant Menu");
		returnToAttendantMenuButton.addActionListener(returnToAttendantMenuListener);
		add(returnToAttendantMenuButton);

		JButton addCurrencyButton = new JButton("Add " + cashType + "(s)");
		addCurrencyButton.addActionListener(refillCashButtonListener);
		add(addCurrencyButton);
		
	}


	public String getValue() {
		return cashValueTextField.getText();
	}

	public String getCurrency() {
		return currencyTextField.getText();
	}

	public int getNumCash() {
		return (int) numCashSpinner.getValue();
	}
}
