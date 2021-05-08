package selfcheckout.software.views.gui.pages.attendantmenu;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.GridLayout;

public class AttendantMenuPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public AttendantMenuPanel(
			ActionListener logOutListener,
			ActionListener removeItemsFromOrderListener, ActionListener lookUpProductButtonsListener,
			ActionListener refillReceiptPaperListener, ActionListener refillReceiptInkListener,
			ActionListener emptyStationCoinsListener, ActionListener emptyStationBanknotesListener,
			ActionListener refillStationCoinsListener, ActionListener refillStationBanknotesListener,
			ActionListener blockStationListener, ActionListener shutDownStationListener) {
		setLayout(new GridLayout(6, 2, 0, 0));

		JButton logOutButton = new JButton("Log Out and Return to Order");
		logOutButton.addActionListener(logOutListener);
		add(logOutButton);

		Component emptySpaceHorizontalGlue = Box.createHorizontalGlue();
		add(emptySpaceHorizontalGlue);

		if (removeItemsFromOrderListener == null) {
			// if no listener, then there must be no items to remove
			Component removeItemsFromOrderHorizontalGlue = Box.createHorizontalGlue();
			add(removeItemsFromOrderHorizontalGlue);
		} else {
			JButton removeItemsFromOrderButton = new JButton("Remove Items From Order");
			removeItemsFromOrderButton.addActionListener(removeItemsFromOrderListener);
			add(removeItemsFromOrderButton);
		}

		JButton lookUpProductButtons = new JButton("Look Up Product");
		lookUpProductButtons.addActionListener(lookUpProductButtonsListener);
		add(lookUpProductButtons);

		JButton refillReceiptPaperButton = new JButton("Refill Receipt Paper");
		refillReceiptPaperButton.addActionListener(refillReceiptPaperListener);
		add(refillReceiptPaperButton);

		JButton refillReceiptInkButton = new JButton("Refill Receipt Ink");
		refillReceiptInkButton.addActionListener(refillReceiptInkListener);
		add(refillReceiptInkButton);

		JButton emptyStationCoinsButton = new JButton("Empty Station Coins");
		emptyStationCoinsButton.addActionListener(emptyStationCoinsListener);
		add(emptyStationCoinsButton);

		JButton emptyStationBanknotesButton = new JButton("Empty Station Banknotes");
		emptyStationBanknotesButton.addActionListener(emptyStationBanknotesListener);
		add(emptyStationBanknotesButton);

		JButton refillStationCoinsButton = new JButton("Refill Station Coins");
		refillStationCoinsButton.addActionListener(refillStationCoinsListener);
		add(refillStationCoinsButton);

		JButton refillStationBanknotesButton = new JButton("Refill Station Banknotes");
		refillStationBanknotesButton.addActionListener(refillStationBanknotesListener);
		add(refillStationBanknotesButton);

		JButton blockStationButton = new JButton("Block Station");
		blockStationButton.addActionListener(blockStationListener);
		add(blockStationButton);

		JButton shutDownStationButton = new JButton("Shut Down Station");
		shutDownStationButton.addActionListener(shutDownStationListener);
		add(shutDownStationButton);

	}

}
