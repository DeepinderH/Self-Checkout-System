package selfcheckout.software.views.gui.pages.finishpurchase;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.OutOfInkException;
import selfcheckout.software.controllers.exceptions.OutOfPaperException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class FinishPurchaseHandler {
    private final ViewStateManager viewStateManager;
    private final SelfCheckoutController controller;
    private final CountDownLatch countDownLatch;

    public FinishPurchaseHandler(
            ViewStateManager viewStateManager,
            SelfCheckoutController controller,
            JFrame frame) {
        this.viewStateManager = viewStateManager;
        this.controller = controller;
        String receiptText;
        try {
            receiptText = this.controller.printReceipt();
        } catch (OutOfInkException | OutOfPaperException e) {
            receiptText = e.getLocalizedMessage();
        }
        FinishPurchasePanel finishPurchasePanel = new FinishPurchasePanel(
            receiptText, new FinishActionListener());
        this.countDownLatch = new CountDownLatch(1);
        frame.setContentPane(finishPurchasePanel);
        frame.pack();
    }

    public void handleFinishOrder(){
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            // ignored, either an actions has happened or this has been
            // interrupted, either way the expected action will occur
        }
    }

    // Button Listener For the Finish Button
    private class FinishActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.removeItemsPaidFor();
            viewStateManager.setState(ViewStateEnum.MAIN_MENU);
            countDownLatch.countDown();
        }
    }

}
