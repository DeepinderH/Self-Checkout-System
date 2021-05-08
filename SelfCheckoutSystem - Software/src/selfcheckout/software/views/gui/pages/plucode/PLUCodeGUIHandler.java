package selfcheckout.software.views.gui.pages.plucode;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.InvalidPLUCodeException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AddItemGUIHandler;
import selfcheckout.software.views.gui.pages.AddItemPanel;

public class PLUCodeGUIHandler extends AddItemGUIHandler {

	public PLUCodeGUIHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		super(viewStateManager, controller, frame);
		this.addItemPanel = new AddItemPanel(new PurchaseByPLUListener(),
			new DescriptionLookupListener(), new ReturnToMainMenuListener(),
			"PLU code");
		this.frame.setContentPane(this.addItemPanel);
		this.frame.pack();	
	}
	
	public void handlePLUItemInput() {		
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}
	
	private class PurchaseByPLUListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String pluCode = addItemPanel.getEnteredItemCode();
			double scannerScaleWeight = addItemPanel.getScanningScaleWeight();
			try {
				controller.inputPLUItem(pluCode, scannerScaleWeight);
				// since code was valid, try to bag the item
				viewStateManager.setState(ViewStateEnum.ITEM_BAGGING);				
			} catch (InvalidPLUCodeException e) {
				// inform user that PLU code was not registered properly
				JOptionPane.showMessageDialog(frame, e.getMessage());
			}
			countDownLatch.countDown();
		}
	}

	
	private class DescriptionLookupListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String description = addItemPanel.getEnteredProductDescription();
			String relevantPLUProducts = controller.lookUpAllPLUProductsByDescription(description);
			JOptionPane.showMessageDialog(frame, relevantPLUProducts);
			countDownLatch.countDown();
		}
	}
}
