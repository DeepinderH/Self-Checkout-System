package selfcheckout.software.views.gui.pages.membership;

import javax.swing.JPanel;

import javax.swing.JTextPane;
import javax.swing.JTextField;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;

 public class MembershipGUIPanel extends JPanel {

 	private static final long serialVersionUID = 1L;

 	private JTextField textField;	
 	private ActionListener membershipCardListener;
 	private ActionListener backToMainListener;
 	private ActionListener membershipNumberListener;
 	
 	/**
 	 * Create the panel.
 	 */
 	public MembershipGUIPanel(ActionListener membershipCardListener, 
 							  ActionListener membershipNumberListener, 
 							  ActionListener backToMainListener) {
 		
 		setLayout(new GridLayout(3, 2, 0, 0));
 		
 		this.membershipCardListener = membershipCardListener;
 		this.membershipNumberListener = membershipNumberListener;
 		this.backToMainListener = backToMainListener;

 		JTextPane txtpnEnterYourMembership = new JTextPane();
 		txtpnEnterYourMembership.setText("Swipe/enter your membership number:");
 		add(txtpnEnterYourMembership);

 		textField = new JTextField();
 		add(textField);
 		textField.setColumns(10);

 		JButton confirmCardBtn = new JButton("Swipe card \n(Manually enter number for simulation)");
 		add(confirmCardBtn);
 		confirmCardBtn.addActionListener(membershipCardListener);
 		
 		JButton confirmNumberBtn = new JButton("Enter membership number");
 		add(confirmNumberBtn);
 		confirmNumberBtn.addActionListener(membershipNumberListener);
 		
 		JButton returnToMainBtn = new JButton("Return to Main Menu");
 		add(returnToMainBtn);
 		returnToMainBtn.addActionListener(backToMainListener);
 	}

 	public String getEnteredMembershipNumber() {
 		return textField.getText();
 	}

 }
