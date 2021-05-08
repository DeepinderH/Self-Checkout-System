package selfcheckout.software.views.gui.pages.itemscanning;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.ItemScanningException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.AddItemGUIHandler;
import selfcheckout.software.views.gui.pages.AddItemPanel;

public class ItemScanningGUIHandler extends AddItemGUIHandler {

	public ItemScanningGUIHandler(
			ViewStateManager viewStateManager, SelfCheckoutController controller, JFrame frame) {
		super(viewStateManager, controller, frame);
		this.addItemPanel = new AddItemPanel(
			new PurchaseByBarcodeListener(),
			new DescriptionLookupListener(), new ReturnToMainMenuListener(),
			"Barcode");
		this.frame.setContentPane(this.addItemPanel);
		this.frame.pack();	
	}
	
	public void handleScanItem() {
		try {
			this.countDownLatch.await();
		} catch (InterruptedException e) {
			// ignored, either an actions has happened or this has been
			// interrupted, either way the expected action will occur
		}
	}
	
	private class PurchaseByBarcodeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			String barcodeString = addItemPanel.getEnteredItemCode();
			double scannerScaleWeight = addItemPanel.getScanningScaleWeight();
			try {
				controller.scanItem(barcodeString, scannerScaleWeight);
				// since code was valid, try to bag the item
				viewStateManager.setState(ViewStateEnum.ITEM_BAGGING);				
			} catch (ItemScanningException e) {
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
			String relevantBarcodedProducts = controller.lookUpAllBarcodedProductsByDescription(description);
			JOptionPane.showMessageDialog(frame, relevantBarcodedProducts);
			countDownLatch.countDown();
		}
	}
}
