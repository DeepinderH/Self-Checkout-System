package selfcheckout.software.views.gui.pages.plasticbagaddition;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;

public class PlasticBagAdditionPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final JSpinner spinner;

	/**
	 * Create the panel.
	 */
	public PlasticBagAdditionPanel(ActionListener confirmButtonListener) {
		setLayout(new GridLayout(3, 1, 0, 0));

		JLabel SelectNumberOfBagsLabel = new JLabel("Please select the number of plastic bags used:");
		SelectNumberOfBagsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(SelectNumberOfBagsLabel);

		this.spinner = new JSpinner();
		this.spinner.setModel(new SpinnerNumberModel(0, 0, null, 1));
		add(this.spinner);

		JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(confirmButtonListener);
		add(confirmButton);
	}

	public int getNumberOfBagsUsed() {
		return (int) this.spinner.getValue();
	}
}
