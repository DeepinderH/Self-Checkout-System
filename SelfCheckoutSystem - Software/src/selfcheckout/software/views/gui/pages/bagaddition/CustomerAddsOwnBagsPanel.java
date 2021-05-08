package selfcheckout.software.views.gui.pages.bagaddition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CustomerAddsOwnBagsPanel extends JPanel {

    static final long serialVersionUID = 1L;
    private final JTextField bagText;

    public CustomerAddsOwnBagsPanel(ActionListener enterEventListener){
        JLabel bagLabel = new JLabel("Enter Weight of Bags");
        bagText = new JTextField();
        JButton bagButton = new JButton("Accept");
        setLayout(new GridLayout(3,1, 0, 0));

        add(bagLabel);
        add(bagText);
        add(bagButton);
        bagButton.addActionListener(enterEventListener);
    }

    public String getWeight() {
        return bagText.getText();
    }
}

