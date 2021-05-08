package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SimulationException;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.InvalidBarcodeException;
import selfcheckout.software.controllers.exceptions.InvalidWeightException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.NonexistentBarcodeException;
import selfcheckout.software.controllers.exceptions.WeightOverloadException;
import selfcheckout.software.controllers.listeners.BarcodeScannerNotificationRecorder;
import selfcheckout.software.controllers.listeners.ScaleNotificationRecorder;

public class ItemScanningSubcontroller {

	private final BarcodeScannerNotificationRecorder barcodeScannerNotificationRecorder;

	private final BarcodeScanner barcodeScanner;
	private final ElectronicScale scanningScale;
	private final PurchaseManager purchaseManager;
	private final ScaleNotificationRecorder scanningScaleNotificationRecorder;

	public ItemScanningSubcontroller(BarcodeScanner barcodeScanner, ElectronicScale scanningScale,
	                                 PurchaseManager purchaseManager) {
		this.barcodeScanner = barcodeScanner;
		this.scanningScale = scanningScale;
		this.purchaseManager = purchaseManager;

		// set up the listener for the barcode scanner
		this.barcodeScannerNotificationRecorder = new BarcodeScannerNotificationRecorder();
		this.barcodeScanner.register(barcodeScannerNotificationRecorder);
		this.scanningScaleNotificationRecorder = new ScaleNotificationRecorder();
		this.scanningScale.register(this.scanningScaleNotificationRecorder);
	}

	public void scanItem(String barcode, double weight) throws NonexistentBarcodeException, InvalidBarcodeException, InvalidWeightException, WeightOverloadException {
		Barcode code;
		BarcodedItem item;

		// Check if there is previously scanned item not in the bagging area
		if (scanningScaleNotificationRecorder.getCurrentWeight() > 0) {
			throw new WeightOverloadException("There is an item on the scanning area scale. Please move the item to the bagging area before scanning.");
		}

		// test if the barcode is valid
		try {
			code = new Barcode(barcode);
		} catch (SimulationException e) {
			throw new InvalidBarcodeException("That is not a valid barcode.");
		}

		try {
			item = new BarcodedItem(code, weight);
		} catch (SimulationException e) {
			throw new InvalidWeightException("You can only purchase items obeying the laws of physics. Please find an item with a positive weight.");
		}

		// add the item to the scanning area scale, which will record the weight on its corresponding listener
		this.scanningScale.add(item);
		if (this.scanningScaleNotificationRecorder.isOverloaded()) {
			this.scanningScale.remove(item);
			throw new WeightOverloadException("Scanning scale is overloaded. Item has been automatically removed from scale");
		}

		// reset the listener for a new scan
		this.barcodeScannerNotificationRecorder.clearNotifications();

		this.barcodeScanner.scan(item);
		int scans = barcodeScannerNotificationRecorder.getScannedBarcodes().size();
		if (scans > 0) {
			// since the scan was successful, see if the current transaction can be updated with the desired item
			try {
				purchaseManager.addItem(item);
			} catch (NoSuchItemException e) {
				this.scanningScale.remove(item);
				throw new NonexistentBarcodeException(e.getLocalizedMessage());
			}
		} else {
			this.scanningScale.remove(item);
			throw new InvalidBarcodeException("Barcode could not be scanned. Item has been automatically removed from scale.");
		}
	}
}
