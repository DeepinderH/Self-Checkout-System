package selfcheckout.software.views.gui.pages;

import javax.swing.JPanel;

import javax.swing.JTextPane;
import javax.swing.JTextField;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class AddItemPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final JTextField descriptionTextField;
	private final JTextField itemCodeField;
	private final JTextField scanningWeightTxt;

	public AddItemPanel(ActionListener purchaseListener,
	                    ActionListener descriptionListener,
	                    ActionListener mainMenuListener,
	                    String itemIdentifierString) {
 		GridBagLayout gridBagLayout = new GridBagLayout();
 		gridBagLayout.columnWidths = new int[]{197, 107, 12, 130, 0};
 		gridBagLayout.rowHeights = new int[]{35, 26, 16, 33, 39, 131, 0, 0};
 		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
 		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
 		setLayout(gridBagLayout);

		JTextField txtLookUpProduct = new JTextField();
 		txtLookUpProduct.setText("Look up product by description");
 		GridBagConstraints gbc_txtLookUpProduct = new GridBagConstraints();
 		gbc_txtLookUpProduct.anchor = GridBagConstraints.NORTH;
 		gbc_txtLookUpProduct.fill = GridBagConstraints.HORIZONTAL;
 		gbc_txtLookUpProduct.insets = new Insets(0, 0, 5, 5);
 		gbc_txtLookUpProduct.gridx = 0;
 		gbc_txtLookUpProduct.gridy = 1;
 		add(txtLookUpProduct, gbc_txtLookUpProduct);
 		txtLookUpProduct.setColumns(10);
 		
 		descriptionTextField = new JTextField();
 		GridBagConstraints gbc_descriptionTextField = new GridBagConstraints();
 		gbc_descriptionTextField.anchor = GridBagConstraints.NORTHWEST;
 		gbc_descriptionTextField.insets = new Insets(0, 0, 5, 5);
 		gbc_descriptionTextField.gridx = 1;
 		gbc_descriptionTextField.gridy = 1;
 		add(descriptionTextField, gbc_descriptionTextField);
 		descriptionTextField.setColumns(10);

		JButton descripSearchButton = new JButton("Search by description");
 		GridBagConstraints gbc_descripSearchButton = new GridBagConstraints();
 		gbc_descripSearchButton.fill = GridBagConstraints.BOTH;
 		gbc_descripSearchButton.insets = new Insets(0, 0, 5, 0);
 		gbc_descripSearchButton.gridx = 3;
 		gbc_descripSearchButton.gridy = 1;
 		add(descripSearchButton, gbc_descripSearchButton);
 		descripSearchButton.addActionListener(descriptionListener);
 		
 		JTextPane txtpnLookUpProduct = new JTextPane();
 		txtpnLookUpProduct.setText("Enter " + itemIdentifierString + " to purchase product");
 		GridBagConstraints gbc_txtpnLookUpProduct = new GridBagConstraints();
 		gbc_txtpnLookUpProduct.fill = GridBagConstraints.HORIZONTAL;
 		gbc_txtpnLookUpProduct.insets = new Insets(0, 0, 5, 5);
 		gbc_txtpnLookUpProduct.gridwidth = 2;
 		gbc_txtpnLookUpProduct.gridx = 0;
 		gbc_txtpnLookUpProduct.gridy = 2;
 		add(txtpnLookUpProduct, gbc_txtpnLookUpProduct);

		JTextPane txtpnPluCode = new JTextPane();
 		txtpnPluCode.setText(itemIdentifierString + ":");
 		GridBagConstraints gbc_txtpnPluCode = new GridBagConstraints();
 		gbc_txtpnPluCode.fill = GridBagConstraints.BOTH;
 		gbc_txtpnPluCode.insets = new Insets(0, 0, 5, 5);
 		gbc_txtpnPluCode.gridx = 0;
 		gbc_txtpnPluCode.gridy = 3;
 		add(txtpnPluCode, gbc_txtpnPluCode);
 		
 		itemCodeField = new JTextField();
 		GridBagConstraints gbc_pluTextField = new GridBagConstraints();
 		gbc_pluTextField.fill = GridBagConstraints.HORIZONTAL;
 		gbc_pluTextField.insets = new Insets(0, 0, 5, 5);
 		gbc_pluTextField.gridwidth = 2;
 		gbc_pluTextField.gridx = 1;
 		gbc_pluTextField.gridy = 3;
 		add(itemCodeField, gbc_pluTextField);
 		itemCodeField.setColumns(10);

		JTextPane txtpnforSimulationScanning = new JTextPane();
 		txtpnforSimulationScanning.setText("(For simulation) Scanning scale weight:");
 		GridBagConstraints gbc_txtpnforSimulationScanning = new GridBagConstraints();
 		gbc_txtpnforSimulationScanning.fill = GridBagConstraints.BOTH;
 		gbc_txtpnforSimulationScanning.insets = new Insets(0, 0, 5, 5);
 		gbc_txtpnforSimulationScanning.gridx = 0;
 		gbc_txtpnforSimulationScanning.gridy = 4;
 		add(txtpnforSimulationScanning, gbc_txtpnforSimulationScanning);
 		
 		scanningWeightTxt = new JTextField();
 		GridBagConstraints gbc_scanningWeightTxt = new GridBagConstraints();
 		gbc_scanningWeightTxt.fill = GridBagConstraints.HORIZONTAL;
 		gbc_scanningWeightTxt.insets = new Insets(0, 0, 5, 5);
 		gbc_scanningWeightTxt.gridwidth = 2;
 		gbc_scanningWeightTxt.gridx = 1;
 		gbc_scanningWeightTxt.gridy = 4;
 		add(scanningWeightTxt, gbc_scanningWeightTxt);
 		scanningWeightTxt.setColumns(10);

		 JButton purchaseButton = new JButton("Purchase");
 		GridBagConstraints gbc_purchaseButton = new GridBagConstraints();
 		gbc_purchaseButton.insets = new Insets(0, 0, 5, 0);
 		gbc_purchaseButton.fill = GridBagConstraints.HORIZONTAL;
 		gbc_purchaseButton.gridx = 3;
 		gbc_purchaseButton.gridy = 5;
 		add(purchaseButton, gbc_purchaseButton);
 		purchaseButton.addActionListener(purchaseListener);

		 JButton returnBtn = new JButton("Return to Main Menu");
 		GridBagConstraints gbc_returnBtn = new GridBagConstraints();
 		gbc_returnBtn.insets = new Insets(0, 0, 0, 5);
 		gbc_returnBtn.gridx = 1;
 		gbc_returnBtn.gridy = 6;
 		add(returnBtn, gbc_returnBtn);
 		returnBtn.addActionListener(mainMenuListener);
 	}
 	
 	public String getEnteredProductDescription() {
 		return descriptionTextField.getText();
 	}
 	
 	public String getEnteredItemCode() {
 		return itemCodeField.getText();
 	}
 	
 	public double getScanningScaleWeight() {
 		return Double.parseDouble(scanningWeightTxt.getText());
 	}
 
 }

