package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SimulationException;

import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.listeners.ScaleNotificationRecorder;

public class ItemRemovalSubcontroller {
	
	// creating the listener and the bagging scale
	private final ScaleNotificationRecorder baggingScaleNotifier;
	private final ElectronicScale baggingScale;
	private final PurchaseManager purchaseManager;
	
	public ItemRemovalSubcontroller(ElectronicScale baggingScale, PurchaseManager purchaseManager) {
		this.baggingScale = baggingScale;
		
		this.purchaseManager = purchaseManager;

		// pass in the monitor to the listener so they are able to send information back to the monitor
		this.baggingScaleNotifier = new ScaleNotificationRecorder();

		// register the listeners for the scale
		this.baggingScale.register(baggingScaleNotifier);
	}
	
	/**
	 * Removes item from the purchase items list and the bagging area
	 * 
	 * @param itemIndex 
	 * 
	 */
	public void removePurchasedItems(int itemIndex) {
		Item itemToBeRemoved = this.purchaseManager.removeItem(itemIndex);

		try {
			this.baggingScale.remove(itemToBeRemoved);
			
			//doing nothing (in case the customer chose to not bag the item)
			// As this method is used by the attendant controller to primarily remove the item from the purchase manager,
			// it should also automatically remove items from bagging (if the customer bagged the item), if not and a simulation exception
			// is thrown, we simply ignore it for the functionality of this function
		} catch(SimulationException e) {}
	}	
	
}
