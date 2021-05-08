package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SimulationException;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.InvalidPLUCodeException;
import selfcheckout.software.controllers.exceptions.InvalidWeightException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.NonexistentPLUCodeException;
import selfcheckout.software.controllers.exceptions.WeightOverloadException;
import selfcheckout.software.controllers.listeners.ScaleNotificationRecorder;

public class PLUItemInputSubcontroller {

	private final ElectronicScale weighingScale;
	private final PurchaseManager purchaseManager;
	private final ScaleNotificationRecorder weighingScaleNotificationRecorder;

	public PLUItemInputSubcontroller(ElectronicScale scanningScale, PurchaseManager purchaseManager) {
		this.weighingScale = scanningScale;
		this.purchaseManager = purchaseManager;

		// set up the listener for the scale 
		this.weighingScaleNotificationRecorder = new ScaleNotificationRecorder();
		this.weighingScale.register(this.weighingScaleNotificationRecorder);
	}

	public void enterPLUCode(String pluCode, double weight) throws NonexistentPLUCodeException, InvalidPLUCodeException, InvalidWeightException, WeightOverloadException {
		PriceLookupCode code;
		PLUCodedItem item;

		// Check if there is previously scanned item not in the bagging area
		if (weighingScaleNotificationRecorder.getCurrentWeight() > 0) {
			throw new WeightOverloadException("There is an item on the scanning area scale. Please move the item to the bagging area before scanning.");
		}

		// test if the PLU code is valid
		try {
			code = new PriceLookupCode(pluCode);
		} catch (SimulationException e) {
			throw new InvalidPLUCodeException("That is not a valid price look up code");
		}

		try {
			item = new PLUCodedItem(code, weight);
		} catch (SimulationException e) {
			throw new InvalidWeightException("You can only purchase items obeying the laws of physics. Please find an item with a positive weight.");
		}

		// add the item to the weighing scale, which will record the weight on its corresponding listener
		this.weighingScale.add(item);
		if (this.weighingScaleNotificationRecorder.isOverloaded()) {
			this.weighingScale.remove(item);
			throw new WeightOverloadException("Scanning scale is overloaded. Item has been automatically removed from scale");
		}
		
		// Since plu item seems to be valid, see if the current transaction can be updated with the desired item
		try {
			purchaseManager.addItem(item);
		} catch (NoSuchItemException e) {
			this.weighingScale.remove(item);
			throw new NonexistentPLUCodeException(e.getLocalizedMessage());
		}
	}
}
