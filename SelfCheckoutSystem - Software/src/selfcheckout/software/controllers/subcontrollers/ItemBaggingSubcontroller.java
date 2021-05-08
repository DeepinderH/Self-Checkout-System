package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.ElectronicScale;

import org.lsmr.selfcheckout.devices.OverloadException;
import selfcheckout.software.controllers.BagItem;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.controllers.listeners.ScaleNotificationRecorder;

/**
 * Keeps track of the weight of all items placed in the bagging area.
 * Also checks that weight of most recently added item matches that of the most recently scanned item.
 *
 */
public class ItemBaggingSubcontroller {

	// creates listeners for both scales
	private final ScaleNotificationRecorder scanningScaleNotifier;
	private final ScaleNotificationRecorder baggingScaleNotifier;
	private final ElectronicScale baggingScale;
	private final ElectronicScale scanningScale;
	private final PurchaseManager purchaseManager;

	/**
	 * Constructor
	 * 
	 * @param baggingScale
	 * 				the scale in the bagging area
	 * @param scanningScale
	 * 				the scale in the scanning area
	 */
	public ItemBaggingSubcontroller(ElectronicScale baggingScale, ElectronicScale scanningScale, PurchaseManager purchaseManager) {
		this.baggingScale = baggingScale;
		this.scanningScale = scanningScale;
		// link to purchase manager
		this.purchaseManager = purchaseManager;

		// pass in the monitor to each listener so they are able to send information back to the monitor
		this.scanningScaleNotifier = new ScaleNotificationRecorder();
		this.baggingScaleNotifier = new ScaleNotificationRecorder();

		// register the listeners for both of these scales
		this.baggingScale.register(baggingScaleNotifier);
		this.scanningScale.register(scanningScaleNotifier);
	}

	/**
	 * Verify that the weight registered by the scanning scale and the bagging scale are identical if the customer chooses to bag
	 * the item. Otherwise, simply remove the item from the scanningScale.
	 */
	public void handleItemBagging(Item lastItem, Double actualItemWeight) throws InvalidWeightException, WeightMismatchException, WeightOverloadException {
		// Always first remove item from scanning scale
		this.scanningScale.remove(lastItem);

		purchaseManager.addItemTrackBagging(lastItem, true);

		if (actualItemWeight <= 0) {
			throw new InvalidWeightException("Item is missing from bagging area. Attendant has been notified.");
		}

		// once the item has been weighed, check if the two weights are equal
		if (actualItemWeight != lastItem.getWeight()) {
			throw new WeightMismatchException("Unexpected item in the bagging area. Please notify an attendant.");
		}

		this.baggingScale.add(lastItem);

		if (this.baggingScaleNotifier.isOverloaded()) {
			this.baggingScale.remove(lastItem);
			this.purchaseManager.removeLastItem();
			throw new WeightOverloadException("Bagging area scale is overloaded. Last item has been removed from the bagging area");
		}
	}

	public void skipBaggingItem(Item lastItem) {
		// Always first remove item from scanning scale
		this.scanningScale.remove(lastItem);
		purchaseManager.addItemTrackBagging(lastItem, false);
	}

	public void acceptWeightMismatch() throws WeightOverloadException {
		Item lastItem = this.purchaseManager.getLastItem();

		this.baggingScale.add(lastItem);

		if (this.baggingScaleNotifier.isOverloaded()) {
			this.baggingScale.remove(lastItem);
			this.purchaseManager.removeLastItem();
			throw new WeightOverloadException("Bagging area scale is overloaded. Last item has been removed from the bagging area");
		}
	}

	public void addPlasticBagsUsed(int numBagsUsed) throws WeightOverloadException {
		for (int bagNumber = 0; bagNumber < numBagsUsed; bagNumber++) {
			BagItem plasticBag = new BagItem(0.1);
			this.baggingScale.add(plasticBag);
			if (this.baggingScaleNotifier.isOverloaded()) {
				this.baggingScale.remove(plasticBag);
				for (int i = 0; i < bagNumber; i++) {
					Item removedBag = this.purchaseManager.removeLastItem();
					this.baggingScale.remove(removedBag);
				}
				throw new WeightOverloadException("That many plastic bags are not allowed");
			}
			try {
				this.purchaseManager.addItem(plasticBag);
			} catch (NoSuchItemException e) {
				throw new ControlSoftwareException("Could not add plastic bags to purchase");
			}
		}
	}
}
