package selfcheckout.software.views.gui.pages;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.SwingConstants;

public class AttendantLoginPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final JTextField attendantIdEntryField;
	private final JPasswordField attendantPasswordField;

	/**
	 * Create the panel.
	 */
	public AttendantLoginPanel(
			ActionListener processLoginListener,
			String labelText, String buttonText) {
		GridBagLayout gbl_centerPanel = new GridBagLayout();
		gbl_centerPanel.columnWidths = new int[]{225, 225};
		gbl_centerPanel.rowHeights = new int[]{100, 100, 100, 100};
		gbl_centerPanel.columnWeights = new double[]{1.0, 1.0};
		gbl_centerPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		setLayout(gbl_centerPanel);

		JLabel loginMenuLabel = new JLabel(labelText);
		GridBagConstraints gbc_loginMenuLabel = new GridBagConstraints();
		gbc_loginMenuLabel.gridwidth = 2;
		gbc_loginMenuLabel.insets = new Insets(0, 0, 5, 5);
		gbc_loginMenuLabel.gridx = 0;
		gbc_loginMenuLabel.gridy = 0;
		add(loginMenuLabel, gbc_loginMenuLabel);

		JLabel enterAttendantIDLabel = new JLabel("Enter Attendant ID Number:");
		enterAttendantIDLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_enterAttendantIDLabel = new GridBagConstraints();
		gbc_enterAttendantIDLabel.fill = GridBagConstraints.BOTH;
		gbc_enterAttendantIDLabel.insets = new Insets(0, 0, 5, 5);
		gbc_enterAttendantIDLabel.gridx = 0;
		gbc_enterAttendantIDLabel.gridy = 1;
		add(enterAttendantIDLabel, gbc_enterAttendantIDLabel);

		attendantIdEntryField = new JTextField();
		GridBagConstraints gbc_attendantIdEntryField = new GridBagConstraints();
		gbc_attendantIdEntryField.fill = GridBagConstraints.BOTH;
		gbc_attendantIdEntryField.insets = new Insets(0, 0, 5, 0);
		gbc_attendantIdEntryField.gridx = 1;
		gbc_attendantIdEntryField.gridy = 1;
		add(attendantIdEntryField, gbc_attendantIdEntryField);
		attendantIdEntryField.setColumns(10);

		JLabel enterAttendantPasscodeLabel = new JLabel("Enter Attendant Passcode:");
		enterAttendantPasscodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_enterAttendantPasscodeLabel = new GridBagConstraints();
		gbc_enterAttendantPasscodeLabel.fill = GridBagConstraints.BOTH;
		gbc_enterAttendantPasscodeLabel.insets = new Insets(0, 0, 5, 5);
		gbc_enterAttendantPasscodeLabel.gridx = 0;
		gbc_enterAttendantPasscodeLabel.gridy = 2;
		add(enterAttendantPasscodeLabel, gbc_enterAttendantPasscodeLabel);

		attendantPasswordField = new JPasswordField();
		attendantPasswordField.addActionListener(processLoginListener);
		GridBagConstraints gbc_attendantPasswordField = new GridBagConstraints();
		gbc_attendantPasswordField.fill = GridBagConstraints.BOTH;
		gbc_attendantPasswordField.insets = new Insets(0, 0, 5, 0);
		gbc_attendantPasswordField.gridx = 1;
		gbc_attendantPasswordField.gridy = 2;
		add(attendantPasswordField, gbc_attendantPasswordField);

		JButton loginButton = new JButton(buttonText);
		loginButton.addActionListener(processLoginListener);
		GridBagConstraints gbc_loginButton = new GridBagConstraints();
		gbc_loginButton.gridwidth = 2;
		gbc_loginButton.fill = GridBagConstraints.BOTH;
		gbc_loginButton.gridx = 0;
		gbc_loginButton.gridy = 3;
		add(loginButton, gbc_loginButton);
	}

	public String getAttendantID() {
		return attendantIdEntryField.getText();
	}

	public String getAttendantPasscode() {
		String password = new String(attendantPasswordField.getPassword());
		// clear password field
		attendantPasswordField.setText("");
		return password;
	}

}
