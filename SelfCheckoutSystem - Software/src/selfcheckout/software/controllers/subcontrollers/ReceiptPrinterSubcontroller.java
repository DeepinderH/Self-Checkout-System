package selfcheckout.software.controllers.subcontrollers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SimulationException;

import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.controllers.listeners.ReceiptPrinterNotificationRecorder;

public class ReceiptPrinterSubcontroller {

	private final ReceiptPrinter receiptPrinter;
	private final ReceiptPrinterNotificationRecorder receiptPrinterNotificationRecorder;
	private final PurchaseManager purchaseManager;
	private final ProductDatabasesWrapper productDatabasesWrapper;
	// Note: BarcodedItem only allows us to get the Barcode.
	//		 BarcodedProduct allows us to get Description and Price (Through Product).
	//       We can get BarcodedProduct from ProductDatabasesWrapper.
	//       Though, we can get the price from ProductDatabasesWrapper itself.
	//		 Same idea for PLU coded items.
	
	public ReceiptPrinterSubcontroller(ReceiptPrinter receiptPrinter, PurchaseManager purchaseManager, ProductDatabasesWrapper productDatabasesWrapper) {
		this.receiptPrinterNotificationRecorder = new ReceiptPrinterNotificationRecorder();
		this.receiptPrinter = receiptPrinter;
		this.receiptPrinter.register(receiptPrinterNotificationRecorder);
		this.purchaseManager = purchaseManager;
		this.productDatabasesWrapper = productDatabasesWrapper;
	}
	
	public String printReceipt(BigDecimal totalPayment) throws OutOfInkException, OutOfPaperException {
		// Retrieves the current purchase.
		// Will allow us to look at further details of the purchased item.
		ArrayList<Item> purchasedItemDetails = this.purchaseManager.getLastPurchase().getCurrentPurchases();
		BigDecimal totalPrice = this.purchaseManager.getTotalPriceOfPurchasedItems(purchasedItemDetails);
		BarcodedItem accessItemBarcode; // Cast Item into BarcodedItem.
		Barcode barcodeOfItem; // This will give us access to the the purchased item's details which we'll print.
		PLUCodedItem accessPLUCode; // Cast Item into PLUcodedItem.
		PriceLookupCode priceLookupCodeOfItem; // This will give us access to the the purchased item's details which we'll print.
		
		String itemDescription;
		BigDecimal itemPrice;
		
		// Header
		String header = "============================\n";
		stringPrinter(header);
		
		// Receipt Labels
		String receiptLabels = "                          Receipt\n============================\nPurchased Items:\n\n";
		stringPrinter(receiptLabels);
		
		// ArrayList<Item> will contain Item objects. (Could be casted to BarcodedItem to get their details.)
		// Note: It also contains PLU coded items. This is of type Item as well so it could be casted.
		for (Item purchasedItem : purchasedItemDetails) {
			
			if (purchasedItem instanceof BarcodedItem) {
				accessItemBarcode = (BarcodedItem) purchasedItem;
				barcodeOfItem = accessItemBarcode.getBarcode();

				try {
					itemDescription = this.productDatabasesWrapper.getProductByBarcode(barcodeOfItem).getDescription();
				} catch (NonexistentBarcodeException e) {
					throw new ControlSoftwareException("product does not exist in database");
				}
				itemPrice = this.purchaseManager.getItemPrice(purchasedItem);
				itemPrice = itemPrice.setScale(2, RoundingMode.FLOOR);
				
				// Do your printing here. Print a String character by character.
				// Print Item Description.
				stringPrinter(itemDescription);
				
				// Put space in between the item description and the item price.
				String spacer = ": $";
				stringPrinter(spacer);
				
				// Print Item Price.
				stringPrinter(itemPrice.toPlainString());
				
				// Newline for the next item.
				stringPrinter("\n");
			} else if (purchasedItem instanceof PLUCodedItem) {
				// Note: It also be a PLU coded item as well! This is again of type Item so it could be casted.
				accessPLUCode = (PLUCodedItem) purchasedItem;
				priceLookupCodeOfItem = accessPLUCode.getPLUCode();

				try {
					itemDescription = this.productDatabasesWrapper.getProductByPLUCode(priceLookupCodeOfItem).getDescription();
				} catch (NonexistentPLUCodeException e) {
					throw new ControlSoftwareException("product does not exist in database");
				}
				itemPrice = this.purchaseManager.getItemPrice(purchasedItem);
				itemPrice = itemPrice.setScale(2, RoundingMode.FLOOR);
				
				// Do your printing here. Print a String character by character.
				// Print Item Description.
				stringPrinter(itemDescription);
				
				// Put space in between the item description and the item price.
				String spacer = ": $";
				stringPrinter(spacer);
				
				// Print Item Price.
				stringPrinter(itemPrice.toPlainString());
				
				// Newline for the next item.
				stringPrinter("\n");
			}
		}

		totalPayment = totalPayment.setScale(2, RoundingMode.HALF_DOWN);
		// Computing change before converting total amount into a String for printing.
		// Note:  Total payment comes from payment manager.
		BigDecimal totalChange = totalPayment.subtract(totalPrice);
		totalChange = totalChange.setScale(2, RoundingMode.HALF_DOWN);
		totalPrice = totalPrice.setScale(2, RoundingMode.HALF_DOWN);

		// Print total amount here.
		String totalPriceLabel = "\nTotal Price: $";
		stringPrinter(totalPriceLabel);
		stringPrinter(totalPrice.toPlainString());

		// Print total payment here.
		String totalPaymentLabel = "\nTotal Payment: $";
		stringPrinter(totalPaymentLabel);
		stringPrinter(totalPayment.toPlainString());

		// Print total change here.
		String totalChangeLabel = "\nTotal Change: $";
		stringPrinter(totalChangeLabel);
		stringPrinter(totalChange.toPlainString());
		
		// Footer
		String footer = "\n============================\n";
		stringPrinter(footer);
		
		// Customer automatically retrieves their receipt once the
		// receipt has printed out fully.
		this.receiptPrinter.cutPaper();
		return this.receiptPrinter.removeReceipt();
	}
	
	public void stringPrinter(String toPrint) throws OutOfInkException, OutOfPaperException {
		for (int i = 0; i < toPrint.length(); i++) {
			try {
				this.receiptPrinter.print(toPrint.charAt(i));
			} catch (SimulationException e) {
				// Line is too long. Add a new line.
				if (!this.receiptPrinterNotificationRecorder.getPrinterOutOfPaper()
					&& !this.receiptPrinterNotificationRecorder.getPrinterOutOfInk()) {
					try {
						this.receiptPrinter.print('\n');
					} catch (SimulationException e2) {
						// printer is out of paper or ink, will be captured
						// by one of conditions below
					}
					i = i - 1;
				}
				// Out of Paper
				if (this.receiptPrinterNotificationRecorder.getPrinterOutOfPaper()) {
					this.receiptPrinter.cutPaper();
					this.receiptPrinter.removeReceipt();
					throw new OutOfPaperException("The receipt printer is out of paper. Please ask an attendant to have it refilled.");
				}
				
				// Out of Ink
				if (this.receiptPrinterNotificationRecorder.getPrinterOutOfInk()) {
					this.receiptPrinter.cutPaper();
					this.receiptPrinter.removeReceipt();
					throw new OutOfInkException("The receipt printer is out of ink. Please ask an attendant to have it refilled.");
				}
			}
		}
	}
}
