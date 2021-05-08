package selfcheckout.software.views.gui.pages.finishpurchase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class FinishPurchasePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public FinishPurchasePanel(String receiptText, ActionListener finishActionListener) {
		setLayout(new GridLayout(1, 2, 0, 0));
		
		JTextPane receiptTextPane = new JTextPane();
		receiptTextPane.setText(receiptText);
		add(receiptTextPane);
		
		JButton finishButton = new JButton("Press here to remove your items and take your receipt");
		finishButton.addActionListener(finishActionListener);
		add(finishButton);
	}
}
