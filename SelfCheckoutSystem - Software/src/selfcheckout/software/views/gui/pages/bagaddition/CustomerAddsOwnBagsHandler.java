package selfcheckout.software.views.gui.pages.bagaddition;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.BagAdditionException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class CustomerAddsOwnBagsHandler {
    private final ViewStateManager viewStateManager;
    private final SelfCheckoutController controller;
    private final JFrame frame;
    private final CustomerAddsOwnBagsPanel customerBagPanel;
    private final CountDownLatch countDownLatch;

    public CustomerAddsOwnBagsHandler(ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
        this.viewStateManager = viewStateManager;
        this.controller = controller;
        this.frame = frame;
        this.customerBagPanel = new CustomerAddsOwnBagsPanel(new acceptButtonEvent());
        this.countDownLatch = new CountDownLatch(1);
        this.frame.setContentPane(customerBagPanel);
        this.frame.pack();
    }

    public void handleCustomerBagging() {
        try {
            this.countDownLatch.await();
        } catch (InterruptedException e) {
            // ignored, either an actions has happened or this has been
            // interrupted, either way the expected action will occur
        }
    }

    public class acceptButtonEvent implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            String weightString = customerBagPanel.getWeight();
            double weight;
            try {
                weight = Double.parseDouble(weightString);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid weight!");
                return;
            }
            try {
                controller.addBags(weight);
            } catch (BagAdditionException bagAdditionException) {
                JOptionPane.showMessageDialog(frame,
                    bagAdditionException.getLocalizedMessage());
                return;
            }
            JOptionPane.showMessageDialog(frame, "Bags accepted!");
            viewStateManager.setState(ViewStateEnum.MAIN_MENU);
            countDownLatch.countDown();
        }
    }
}
