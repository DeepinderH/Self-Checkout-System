package selfcheckout.software.views.gui.pages.bagging;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import java.awt.FlowLayout;
import javax.swing.JButton;

public class GUIBaggingPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private final JTextField baggingWeightTxt;
	
	public GUIBaggingPanel(ActionListener baggingWeightListener, ActionListener noBagListener) {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JTextPane txtpnDoYouWant = new JTextPane();
		txtpnDoYouWant.setText("If you would like to bag this item, enter the weight of the item registered by the bagging area scale.");
		add(txtpnDoYouWant);
		
		JTextPane txtpnWeight = new JTextPane();
		txtpnWeight.setText("Weight:");
		add(txtpnWeight);
		
		baggingWeightTxt = new JTextField();
		add(baggingWeightTxt);
		baggingWeightTxt.setColumns(10);
		
		JButton bagBtn = new JButton("Place item in bag");
		add(bagBtn);
		bagBtn.addActionListener(baggingWeightListener);

		JButton noBagBtn = new JButton("Skip item bagging");
		add(noBagBtn);
		noBagBtn.addActionListener(noBagListener);
	}
 	
 	public String getBaggingScaleWeight() {
 		return baggingWeightTxt.getText();
 	}
}
