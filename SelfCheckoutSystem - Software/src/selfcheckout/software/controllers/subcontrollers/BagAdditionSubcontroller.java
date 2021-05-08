package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.devices.ElectronicScale;
import selfcheckout.software.controllers.BagItem;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.controllers.listeners.ScaleNotificationRecorder;

/**
 * Keeps track of the weight of all items placed in the bagging area.
 * Also checks that weight of most recently added item matches that of the most recently scanned item.
 *
 */
public class BagAdditionSubcontroller {

	// creates listeners for both scales
	private final ScaleNotificationRecorder baggingScaleNotifier;
	private final ElectronicScale baggingScale;
	private final PurchaseManager purchaseManager;

	private final double INITIAL_MAX_BAG_WEIGHT = 2.0;
	private double maxBagWeight = INITIAL_MAX_BAG_WEIGHT;

	/**
	 * Constructor
	 *
	 * @param baggingScale the scale in the bagging area
	 * @param purchaseManager the manager for the order and its items
     */
	public BagAdditionSubcontroller(ElectronicScale baggingScale, PurchaseManager purchaseManager) {
		this.baggingScale = baggingScale;
		this.baggingScaleNotifier = new ScaleNotificationRecorder();

		// register the listeners for both of these scales
		this.baggingScale.register(baggingScaleNotifier);

		this.purchaseManager = purchaseManager;
	}

	/**
	 * Add a bag to the bagging area. Also add it to the purchase so that it can
	 * be tracked
	 */
	public void handleBagAddition(Double bagWeight) throws InvalidWeightException, WeightOverloadException {

		if (bagWeight <= 0) {
			throw new InvalidWeightException("Bag weight must be positive");
		}

		if (bagWeight + this.purchaseManager.getTotalBagWeight() > maxBagWeight) {
			throw new InvalidWeightException("Bag weight limit exceeded");
		}

		BagItem bagItem = new BagItem(bagWeight);

		this.baggingScale.add(bagItem);
		if (this.baggingScaleNotifier.isOverloaded()) {
			this.baggingScale.remove(bagItem);
			throw new WeightOverloadException(
				"Bagging area scale is overloaded. Bag has been removed from the bagging area");
		}

		// successfully added bag, include in purchase
		try {
			this.purchaseManager.addItem(bagItem);
		} catch (NoSuchItemException e) {
			// this should never happen
			throw new ControlSoftwareException("System error: cannot add a bag to the order");
		}
	}

	public double getCurrentBagWeight() {
		return this.purchaseManager.getTotalBagWeight();
	}

	public double getMaxBagWeight() {
		return this.maxBagWeight;
	}

	public void changeBagWeightLimit(double newMaxWeight) {
		this.maxBagWeight = newMaxWeight;
	}

	public void resetBagWeightLimit() {
		this.maxBagWeight = INITIAL_MAX_BAG_WEIGHT;
	}
}
