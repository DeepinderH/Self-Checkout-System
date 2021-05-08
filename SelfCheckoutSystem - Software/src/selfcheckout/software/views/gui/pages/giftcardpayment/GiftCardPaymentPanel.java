package selfcheckout.software.views.gui.pages.giftcardpayment;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GiftCardPaymentPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

 	private ActionListener giftCardListener;
 	private ActionListener backToMainListener;
 	private JTextField giftCardNumberTxt;
 	
	public GiftCardPaymentPanel(ActionListener giftCardListener, 
			  ActionListener backToPaymentMenuListener) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{209, 64, 159, 0};
		gridBagLayout.rowHeights = new int[]{16, 26, 29, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JTextPane txtpnSwipeGiftCard = new JTextPane();
		txtpnSwipeGiftCard.setText("Swipe gift card to pay for transaction with remaining total on gift card");
		GridBagConstraints gbc_txtpnSwipeGiftCard = new GridBagConstraints();
		gbc_txtpnSwipeGiftCard.anchor = GridBagConstraints.NORTHWEST;
		gbc_txtpnSwipeGiftCard.insets = new Insets(0, 0, 5, 0);
		gbc_txtpnSwipeGiftCard.gridwidth = 3;
		gbc_txtpnSwipeGiftCard.gridx = 0;
		gbc_txtpnSwipeGiftCard.gridy = 0;
		add(txtpnSwipeGiftCard, gbc_txtpnSwipeGiftCard);
		
		JTextPane txtpnforSimulationEnter = new JTextPane();
		txtpnforSimulationEnter.setText("(For simulation) Enter gift card number:");
		GridBagConstraints gbc_txtpnforSimulationEnter = new GridBagConstraints();
		gbc_txtpnforSimulationEnter.anchor = GridBagConstraints.EAST;
		gbc_txtpnforSimulationEnter.insets = new Insets(0, 0, 5, 5);
		gbc_txtpnforSimulationEnter.gridwidth = 2;
		gbc_txtpnforSimulationEnter.gridx = 0;
		gbc_txtpnforSimulationEnter.gridy = 1;
		add(txtpnforSimulationEnter, gbc_txtpnforSimulationEnter);
		
		giftCardNumberTxt = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.anchor = GridBagConstraints.NORTHWEST;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 1;
		add(giftCardNumberTxt, gbc_textField);
		giftCardNumberTxt.setColumns(10);
		
		JButton paymentButton = new JButton("Pay with Gift Card");
		GridBagConstraints gbc_paymentButton = new GridBagConstraints();
		gbc_paymentButton.anchor = GridBagConstraints.NORTHEAST;
		gbc_paymentButton.insets = new Insets(0, 0, 0, 5);
		gbc_paymentButton.gridx = 0;
		gbc_paymentButton.gridy = 2;
		add(paymentButton, gbc_paymentButton);
		paymentButton.addActionListener(giftCardListener);
		
		JButton mainMenuButton = new JButton("Return to Order Payment Menu");
		GridBagConstraints gbc_mainMenuButton = new GridBagConstraints();
		gbc_mainMenuButton.anchor = GridBagConstraints.NORTHWEST;
		gbc_mainMenuButton.gridwidth = 2;
		gbc_mainMenuButton.gridx = 1;
		gbc_mainMenuButton.gridy = 2;
		add(mainMenuButton, gbc_mainMenuButton);
		mainMenuButton.addActionListener(backToPaymentMenuListener);
	}
	 	
	public String getGiftCardNumber() {
	 	return giftCardNumberTxt.getText();
	 }
	 		
}
