package selfcheckout.software.controllers.listeners;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import selfcheckout.software.controllers.ControllerTestConstants;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class BarcodeScannerNotificationRecorderTest {

	private BarcodeScanner barcodeScanner;
	private BarcodeScannerNotificationRecorder barcodeScannerNotificationRecorder;

	@Before
	public void setUp() {
		this.barcodeScanner = new BarcodeScanner();
		this.barcodeScannerNotificationRecorder = new BarcodeScannerNotificationRecorder();
	}

	private static final int NUM_ITERATIONS = 10;

	@Test
	public void testNoScannedBarcodes() {
		ArrayList<Barcode> barcodes = this.barcodeScannerNotificationRecorder.getScannedBarcodes();
		assertEquals(barcodes.size(), 0);
	}

	@Test
	public void testSingleScannedBarcode() {
		this.barcodeScannerNotificationRecorder.barcodeScanned(
			this.barcodeScanner, ControllerTestConstants.VALID_BARCODE);
		ArrayList<Barcode> scans = this.barcodeScannerNotificationRecorder.getScannedBarcodes();
		assertEquals(scans.size(), 1);
		assertEquals(scans.get(0), ControllerTestConstants.VALID_BARCODE);
	}

	@Test
	public void testMultipleScannedBarcodes() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			this.barcodeScannerNotificationRecorder.barcodeScanned(
				this.barcodeScanner, ControllerTestConstants.VALID_BARCODE);
		}
		ArrayList<Barcode> scans = this.barcodeScannerNotificationRecorder.getScannedBarcodes();
		assertEquals(scans.size(), NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			assertEquals(scans.get(0), ControllerTestConstants.VALID_BARCODE);
		}
	}

	@Test
	public void testClearNotifications() {
		this.barcodeScannerNotificationRecorder.barcodeScanned(
			this.barcodeScanner, ControllerTestConstants.VALID_BARCODE);
		this.barcodeScannerNotificationRecorder.enabled(this.barcodeScanner);
		this.barcodeScannerNotificationRecorder.disabled(this.barcodeScanner);
		this.barcodeScannerNotificationRecorder.clearNotifications();
		assertEquals(this.barcodeScannerNotificationRecorder.getScannedBarcodes().size(), 0);
		assertEquals(this.barcodeScannerNotificationRecorder.getEnabledNotifications().size(), 0);
		assertEquals(this.barcodeScannerNotificationRecorder.getDisabledNotifications().size(), 0);
	}
}
