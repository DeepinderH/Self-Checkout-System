package selfcheckout.software.views.gui.pages;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

public class TextFieldButtonAttendantPanel extends JPanel {
	private final JTextField textField;

	public TextFieldButtonAttendantPanel(
			String labelText,
			String buttonText,
			ActionListener actionButtonListener,
			ActionListener returnToAttendantMenuListener) {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel productDescriptionLabel = new JLabel(labelText);
		add(productDescriptionLabel);

		textField = new JTextField();
		add(textField);
		textField.setColumns(10);

		JButton actionButton = new JButton(buttonText);
		actionButton.addActionListener(actionButtonListener);
		add(actionButton);

		JButton returnToAttendantMenuButton = new JButton("Return to Attendant Menu");
		returnToAttendantMenuButton.addActionListener(returnToAttendantMenuListener);
		add(returnToAttendantMenuButton);
	}

	public String getInputText() {
		return this.textField.getText();
	}
	
}
