package selfcheckout.software.controllers.listeners;

import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.listeners.ReceiptPrinterListener;

public class ReceiptPrinterNotificationRecorder extends NotificationRecorder implements ReceiptPrinterListener {

	private boolean printerOutOfPaper = false;
	private boolean printerOutOfInk = false;
	
	public ReceiptPrinterNotificationRecorder() {
		super();
		this.printerOutOfPaper = false;
		this.printerOutOfInk = false;
	}
	
	@Override
	public void outOfPaper(ReceiptPrinter printer) {
		this.printerOutOfPaper = true;
	}

	@Override
	public void outOfInk(ReceiptPrinter printer) {
		this.printerOutOfInk = true;
	}

	@Override
	public void paperAdded(ReceiptPrinter printer) {
		this.printerOutOfPaper = false;
	}

	@Override
	public void inkAdded(ReceiptPrinter printer) {
		this.printerOutOfInk = false;
	}
	
	public boolean getPrinterOutOfPaper() {
		return this.printerOutOfPaper;
	}
	
	public boolean getPrinterOutOfInk() {
		return this.printerOutOfInk;
	}
}
