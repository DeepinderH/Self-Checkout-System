package selfcheckout.software.controllers.listeners;

import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.listeners.ElectronicScaleListener;


/**
 * Records all weights encountered by the scale used when customers are scanning purchases.
 * Data collected from this will serve to ensure that the same weight is registered on the bagging area scale
 * when the customer tries to bag their most recently scanned purchase
 */
public class ScaleNotificationRecorder extends NotificationRecorder implements ElectronicScaleListener {

	// flag for whether or not the scale can register weight in a meaningful way
	// assume scale does not start off overloaded
	private Boolean overloaded = false;
	// the current weight on the scale (if known)
	private Double currentWeight = 0.0;
	// the weight of the most recent item put on the scale
	private Double lastWeightChange = 0.0;

	/**
	 * Constructor
	 */
	public ScaleNotificationRecorder() {}

	/**
	 * Announces to the scale status monitor that the weight on the current scale has changed.
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 * @param weightInGrams
	 *            The new weight.
	 */
	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		this.lastWeightChange = weightInGrams - this.currentWeight;
		this.currentWeight = weightInGrams;
	}
	/**
	 * Flags the system that the scale can no longer weigh items due to being overloaded
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	@Override
	public void overload(ElectronicScale scale) {
		// record the fact that the scale is overloaded and so cannot weigh anything else
		overloaded = true;
	}

	/**
	 * Flags the system that the scale can again weigh items (no longer overloaded)
	 * 
	 * @param scale
	 *            The scale where the event occurred.
	 */
	@Override
	public void outOfOverload(ElectronicScale scale) {
		overloaded = false;
	}

	public Boolean isOverloaded() {
		return overloaded;
	}

	public Double getCurrentWeight() {
		return currentWeight;
	}

	public Double getLastWeightChange() {
		return lastWeightChange;
	}

	@Override
	public void clearNotifications() {
		super.clearNotifications();
	}

}
