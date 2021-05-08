package selfcheckout.software.views.gui.pages;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.*;

public class SelfCheckoutComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public SelfCheckoutComponent(JPanel innerComponent) {
		setLayout(new BorderLayout(0, 0));

		JLabel label = new JLabel("Welcome to the Self Checkout Station!", null, JLabel.CENTER);
		add(label, BorderLayout.PAGE_START);

		add(innerComponent, BorderLayout.CENTER);
	}
}
