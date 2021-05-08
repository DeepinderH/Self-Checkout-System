package selfcheckout.software.views.gui.pages.paymentcard;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

public class PaymentCardPanel extends JPanel {
	private final JRadioButton debitRadioButton;
	private final JRadioButton creditRadioButton;
	private final JTextField cardNumberTextField;
	private final JTextField cardHolderNameTextField;
	private final JTextField cvvTextField;
	private final JPasswordField passwordField;
	private final JTextField signatureTextField;

	public PaymentCardPanel(
		    ActionListener cardTappedListener,
			ActionListener cardSwipedListener,
		    ActionListener cardInsertedListener,
		    ActionListener returnToOrderPaymentMenuListener) {
		setLayout(new GridLayout(8, 2, 0, 0));
		
		JLabel cardTypeLabel = new JLabel("Card Type (for simulation):");
		add(cardTypeLabel);
		
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new GridLayout(2, 1, 0, 0));
		
		debitRadioButton = new JRadioButton("Debit");
		panel.add(debitRadioButton);
		ButtonGroup cardTypeButtonGroup = new ButtonGroup();
		cardTypeButtonGroup.add(debitRadioButton);
		
		creditRadioButton = new JRadioButton("Credit");
		cardTypeButtonGroup.add(creditRadioButton);
		panel.add(creditRadioButton);
		
		JLabel cardNumberLabel = new JLabel("Card Number (for simulation):");
		add(cardNumberLabel);
		
		cardNumberTextField = new JTextField();
		add(cardNumberTextField);
		
		JLabel cardholderLabel = new JLabel("Cardholder Name (for simulation):");
		add(cardholderLabel);
		
		cardHolderNameTextField = new JTextField();
		add(cardHolderNameTextField);
		
		JLabel cvvLabel = new JLabel("CVV (if tapped or inserted):");
		add(cvvLabel);
		
		cvvTextField = new JTextField();
		add(cvvTextField);
		
		JLabel pinLabel = new JLabel("Pin number (if swiped or inserted):");
		add(pinLabel);
		
		passwordField = new JPasswordField();
		add(passwordField);
		
		JLabel signatureLabel = new JLabel("Signature (if swiped type name for simulation)");
		add(signatureLabel);
		
		signatureTextField = new JTextField();
		add(signatureTextField);
		
		JButton swipeCardButton = new JButton("Swipe Card");
		swipeCardButton.addActionListener(cardSwipedListener);
		add(swipeCardButton);
		
		JButton insertCardButton = new JButton("Insert Card");
		insertCardButton.addActionListener(cardInsertedListener);
		add(insertCardButton);
		
		JButton tapCardButton = new JButton("Tap Card");
		tapCardButton.addActionListener(cardTappedListener);
		add(tapCardButton);
		
		JButton returnToOrderPaymentMenuButton = new JButton("Return to Order Payment Menu");
		returnToOrderPaymentMenuButton.addActionListener(returnToOrderPaymentMenuListener);
		add(returnToOrderPaymentMenuButton);
	}

	public String getCardType() {
		if (debitRadioButton.isSelected()) {
			return "debit";
		} else if (creditRadioButton.isSelected()) {
			return "credit";
		} else {
			return null;
		}
	}

	public String getCardNumber() {
		return cardNumberTextField.getText();
	}

	public String getCardholderName() {
		return cardHolderNameTextField.getText();
	}

	public String getCVV() {
		return cvvTextField.getText();
	}

	public String getPin() {
		String pin = new String(passwordField.getPassword());
		passwordField.setText("");
		return pin;
	}

	public String getSignature() {
		return signatureTextField.getText();
	}



}
