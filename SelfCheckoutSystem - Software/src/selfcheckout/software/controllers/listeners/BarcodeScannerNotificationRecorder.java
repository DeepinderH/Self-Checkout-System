package selfcheckout.software.controllers.listeners;

import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.listeners.BarcodeScannerListener;

/**
 * Listens for any events related to barcode scanning
 */
public class BarcodeScannerNotificationRecorder extends NotificationRecorder implements BarcodeScannerListener {

	// keep a record of barcodes which have been scanned
	private final ArrayList<Barcode> scannedBarcodes;

	public BarcodeScannerNotificationRecorder() {
		super();
		// no items have been scanned yet
		this.scannedBarcodes = new ArrayList<>();
	}

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		// record the fact that this barcode was successfully scanned
		scannedBarcodes.add(barcode);
	}

	public ArrayList<Barcode> getScannedBarcodes() {
		return scannedBarcodes;
	}

	// reset the listener
	@Override
	public void clearNotifications() {
		super.clearNotifications();
		scannedBarcodes.clear();
	}

}
