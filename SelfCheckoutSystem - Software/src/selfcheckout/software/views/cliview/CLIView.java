package selfcheckout.software.views.cliview;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Scanner;

import org.lsmr.selfcheckout.products.BarcodedProduct;
import selfcheckout.software.controllers.ControllerStateEnum;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.*;

import selfcheckout.software.views.SelfCheckoutView;
import selfcheckout.software.views.StateChangeException;
import selfcheckout.software.views.ViewStateEnum;

public class CLIView extends SelfCheckoutView {

	private final Scanner scanner;
	private final PrintStream printStream;

	public CLIView(SelfCheckoutController selfCheckoutController, InputStream inputStream, PrintStream printStream) {
		super(selfCheckoutController);
		this.scanner = new Scanner(inputStream);
		this.printStream = printStream;
	}
	
	@Override
	protected void handleAttendantEmptyBanknotes() {
	    this.printStream.println("Bank note storage unit is being emptied.");
	    this.scanner.nextLine();
	    
	    this.controller.getAttendantConsoleController().emptyBanknoteStorageUnit();
	    this.printStream.println("Bank note storage unit is now empty.");
	    this.stateManager.setState(ViewStateEnum.ATTENDANT_MENU);
	}

	@Override
	protected void handleAttendantEmptyCoins() {
	    this.printStream.println("Coin storage unit is being emptied.");
	    this.scanner.nextLine();

	    this.controller.getAttendantConsoleController().emptyCoinStorageUnit();
	    this.printStream.println("Coin storage unit is now empty.");
	    this.stateManager.setState(ViewStateEnum.ATTENDANT_MENU);
	}

	@Override
	protected void handleBagAddition() {
		this.printBagWeightStatus();
		this.printStream.println("Please place the new bags in the bagging area.");
		double bagWeight;
		while (true) {
			try {
				this.printStream.println("Enter the weight of the new bags as registered by bagging area scale or type back to return to the main menu");
				String input = scanner.nextLine().trim();
				if (input.contentEquals("back")) {
					this.stateManager.setState(ViewStateEnum.MAIN_MENU);
					return;
				}
				bagWeight = Double.parseDouble(input);
				if (bagWeight <= 0) {
					this.printStream.println("Weight must be positive");
					continue;
				}
				break;
			} catch (NumberFormatException e) {
				this.printStream.println("Invalid weight");
			}
		}
		try {
			this.controller.addBags(bagWeight);
			double newBagWeight = this.controller.getCurrentBagWeight();
			this.printStream.println(
				"Bags successfully added!" + System.lineSeparator() +
				"The new total weight of the bags is " + newBagWeight);
		} catch (BagAdditionException e) {
			this.printStream.println(e.getLocalizedMessage());
			this.printStream.println("Bags could not be added and were removed from the bagging area");
		}
		this.printStream.println("Press enter to return to the main menu.");
		this.scanner.nextLine();
		this.stateManager.setState(ViewStateEnum.MAIN_MENU);
	}

	private void printBagWeightStatus() {
		double currentBagWeight = this.controller.getCurrentBagWeight();
		double maxBagWeight = this.controller.getMaxBagWeight();
		this.printStream.println(
			"The total weight of all existing bags is " + currentBagWeight +
			System.lineSeparator() + "The max weight of bags is " + maxBagWeight);
	}

	@Override
	protected void handlePaymentStart() {
		this.controller.goToOrderPaymentState();
		this.stateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
	}

	@Override
	protected void handleBanknotePayment() {
		while (true) {
			int banknoteValue;
			Currency banknoteCurrency;

			try {
				banknoteValue = this.inputBanknoteValue();
			} catch (StateChangeException e) {
				return;
			}

			try {
				banknoteCurrency = this.inputCurrency();
			} catch (StateChangeException e) {
				this.printStream.println("No banknote was input. Returning to order payment menu...");
				return;
			}
			try {
				this.controller.insertBanknote(banknoteValue, banknoteCurrency);
			} catch (BanknoteRejectedException e) {
				this.printStream.println(e.getLocalizedMessage());
				this.printStream.println("Press enter to remove the banknote");
				this.scanner.nextLine();
				this.controller.removeDanglingBanknote();
				this.printStream.println("Banknote successfully removed!");
				continue;
			} catch (StorageUnitFullException e) {
				this.printStream.println(e.getLocalizedMessage());
				this.printStream.println("Press enter to remove the banknote");
				this.scanner.nextLine();
				this.controller.removeDanglingBanknote();
				this.printStream.println("Press enter to return to the order payment menu");
				this.scanner.nextLine();
				this.stateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
				return;
			}
			this.printStream.println("Banknote accepted! New payment total is:");
			this.printTotalPayment();
		}
	}

	private int inputBanknoteValue() {
		int banknoteValue;
		while (true) {
			this.printStream.println("Please enter the banknote value or type 'back' to go back to the order payment menu:");
			String input = scanner.nextLine().trim();
			if(input.toLowerCase().contentEquals("back")) {
				this.stateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
				throw new StateChangeException();
			}
			try {
				banknoteValue = Integer.parseInt(input);
				if (banknoteValue <= 0) {
					this.printStream.println("The banknote value must be positive");
					continue;
				}
			} catch (NumberFormatException e) {
				this.printStream.println("The banknote value must be an integer");
				continue;
			}
			return banknoteValue;
		}
	}

	private Currency inputCurrency() {
		Currency currency;
		while (true) {
			this.printStream.println("Please enter the currency or type 'back' to go back to the order payment menu:");
			String input = scanner.nextLine().trim();
			if(input.toLowerCase().contentEquals("back")) {
				this.stateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
				throw new StateChangeException();
			}
			String possibleCurrency = input.toUpperCase();
			try {
				currency = Currency.getInstance(possibleCurrency);
			} catch (IllegalArgumentException e) {
				this.printStream.println("That is not a valid currency. Currencies must be three characters in length");
				continue;
			}
			return currency;
		}
	}

	@Override
	protected void handleCoinPayment() {
		while (true) {
			BigDecimal coinValue;
			Currency coinCurrency;

			try {
				coinValue = this.inputCoinValue();
			} catch (StateChangeException e) {
				return;
			}

			try {
				coinCurrency = this.inputCurrency();
			} catch (StateChangeException e) {
				this.printStream.println("No coin was input. Returning to order payment menu...");
				return;
			}
			try {
				this.controller.insertCoin(coinValue, coinCurrency);
			} catch (CoinRejectedException e) {
				this.printStream.println(e.getLocalizedMessage());
				this.printStream.println("Coin was delivered to rejection sink. Press enter to remove");
				this.scanner.nextLine();
				this.controller.emptyCoinTray();
				this.printStream.println("Coin successfully removed!");
				continue;
			} catch (StorageUnitFullException e) {
				this.printStream.println(e.getLocalizedMessage());
				this.printStream.println("Press enter to remove the coin");
				this.scanner.nextLine();
				this.controller.emptyCoinTray();
				this.printStream.println("Press enter to return to the order payment menu");
				this.scanner.nextLine();
				this.stateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
				return;
			}
			this.printStream.println("Coin accepted! New payment total is:");
			this.printTotalPayment();
		}
	}

	private void printTotalPayment() {
		BigDecimal total = this.controller.getTotalPayment();
		double value = total.doubleValue();
		this.printStream.format("$%2.2f" + System.lineSeparator(), value);
	}

	private BigDecimal inputCoinValue() {
		BigDecimal coinValue;
		while (true) {
			this.printStream.println("Please enter the coin value or type 'back' to go back to the order payment menu:");
			String input = scanner.nextLine().trim();
			if(input.toLowerCase().contentEquals("back")) {
				this.stateManager.setState(ViewStateEnum.ORDER_PAYMENT_MENU);
				throw new StateChangeException();
			}
			try {
				coinValue = new BigDecimal(input);
				if(coinValue.compareTo(BigDecimal.ZERO) <= 0) {
					this.printStream.println("The coin value must be positive");
					continue;
				}
			} catch (NumberFormatException e) {
				this.printStream.println("That coin value is not valid");
				continue;
			}
			return coinValue;
		}
	}

	@Override
	protected void handleFinishPurchase() {
		this.printStream.println("Please remove all your items. Press enter when complete");
		this.scanner.nextLine();
		BigDecimal changeDispensed;
		try {
			changeDispensed = this.controller.finishOrder();
		} catch (OrderIncompleteException e) {
			throw new RuntimeException("Order is incomplete");
		}
		this.printStream.println("Thank you for your purchase. (Gave " + changeDispensed.toPlainString() + "$ change)");
		//this.stateManager.setState(ViewStateEnum.MAIN_MENU);
		// receipt automatically gets printed after a successful purchase
		this.stateManager.setState(ViewStateEnum.PRINT_RECEIPT);
	}
	
	@Override
	protected void handlePrintReceipt() {
		try {
			this.printStream.println("\nPrinting Receipt...");
			this.printStream.println(this.controller.printReceipt());
			this.printStream.print("Press enter to go back to the main menu.\n");
			this.scanner.nextLine();
		} catch (OutOfInkException | OutOfPaperException e) {
			this.printStream.println(e.getMessage());
			this.printStream.println("Receipt could not be printed. Still starting a new order");
		}
		this.stateManager.setState(ViewStateEnum.MAIN_MENU);
	}
	
	@Override
	protected void handleAttendantReceiptInkAddition() {
		this.printStream.println("How much ink would you like to put in?\n");
		String quantity = scanner.nextLine().trim();
		try {
			this.controller.getAttendantConsoleController().addInkToReceiptPrinter(Integer.valueOf(quantity));
		} catch (NumberFormatException e) {
			this.printStream.println(e.getMessage());
		} catch (InvalidPrinterRefillException e) {
			this.printStream.println(e.getMessage());
		}

		this.stateManager.setState(ViewStateEnum.ATTENDANT_MENU);
	}
	
	@Override
	protected void handleAttendantReceiptPaperAddition() {
		this.printStream.println("How much paper would you like to put in?\n");
		String unit = scanner.nextLine().trim();
		try {
			this.controller.getAttendantConsoleController().addPaperToReceiptPrinter(Integer.valueOf(unit));
		} catch (NumberFormatException e) {
			this.printStream.println(e.getMessage());
		} catch (InvalidPrinterRefillException e) {
			this.printStream.println(e.getMessage());
		}

		this.stateManager.setState(ViewStateEnum.ATTENDANT_MENU);
	}

	@Override
	protected void handleItemBagging() {
		// now that item has been scanned, it must be placed in the bagging area
		double baggingAreaWeight;
		String checkToBag;
		
		while(true) {
			this.printStream.println(
				"Do you wish to bag the current item?" + System.lineSeparator() +
				"Enter 'Yes' to bag the item or 'No' to skip bagging: "
			);
			checkToBag = scanner.nextLine().trim();
			
			if(checkToBag.equals("Yes")|| checkToBag.equals("No")) {
				break;
			}
			
			this.printStream.println(System.lineSeparator() +"Invalid input. Please try again." + System.lineSeparator());	
		}
		
		if(checkToBag.equals("Yes")) {
			while (true) {
				try {
					this.printStream.println(
						"Place the item in the bagging area." + System.lineSeparator() +
						"Enter the weight of the individual item as registered by bagging area scale: ");
					baggingAreaWeight = Double.parseDouble(scanner.nextLine().trim());
					if (baggingAreaWeight <= 0) {
						this.printStream.println("Weight must be positive");
						continue;
					}
					break;
				} catch (NumberFormatException e) {
					this.printStream.println("Invalid weight");
				}
			}
			try {
				controller.bagLastItem(baggingAreaWeight);
				this.printStream.println("Item successfully placed in bag. You can now scan another item.");
			} catch (WeightOverloadException | InvalidWeightException e) {
				this.printStream.println(e.getMessage());
				// if this occurs, item must be scanned again since it has been removed from the transaction
				this.printStream.println("Last item was removed from order. Please try scanning the item again.");
			} catch (WeightMismatchException e) {
				while (true) {
					AttendantCredentials attendantCredentials = requestAttendantCredentials("Weight mismatch! Attendant required");
					try {
						this.controller.getAttendantConsoleController().approveLastItemWeight(
							attendantCredentials.getAttendantID(),
							attendantCredentials.getAttendantPassword());
					} catch (IncorrectAttendantLoginInformationException e2) {
						this.printStream.println(e2.getLocalizedMessage());
						continue;
					} catch (WeightOverloadException e2) {
						this.printStream.println(e2.getLocalizedMessage());
						break;
					}
					this.printStream.println("Item weight approved by attendant and has been kept in the order. Returning to main menu...");
					break;
				}
			}
		} 
		else {
			while (true) {
				try {
					this.printStream.println("Attendant required! Enter the attendant ID:");
					String attendantIDString = this.scanner.nextLine();
					int attendantID;
					try {
						attendantID = Integer.parseInt(attendantIDString);
					} catch (NumberFormatException e) {
						this.printStream.println("attendant Id must be an integer");
						continue;
					}
					this.printStream.println("Attendant required! Enter the attendant password:");
					String attendantPassword = this.scanner.nextLine();
					controller.getAttendantConsoleController().skipBaggingLastItem(attendantID, attendantPassword);
					this.printStream.println("You can now scan another item.");
					break;
				} catch (IncorrectAttendantLoginInformationException e) {
					this.printStream.println(e.getMessage());
					// if this occurs, item must be scanned again since it has been removed from the transaction
					this.printStream.println("Incorrect credentials, try logging in again");
				}
			}
		}
		// user is returned to main menu to make next choice
		this.stateManager.setState(ViewStateEnum.MAIN_MENU);
	}
	
	@Override
	protected void handleItemRemoval() {
		
		int itemIndex;
		printCurrentPurchaseList();
			
		while(true) {
			this.printStream.println(
					"Enter the index of the item you wish to remove (with 1 being the first item from the top): " + System.lineSeparator()
				);
			
			try {
				itemIndex = Integer.parseInt(scanner.nextLine().trim());

			} catch (NumberFormatException e) {
				this.printStream.println(System.lineSeparator() +"Invalid input. Please try again." + System.lineSeparator());
				continue;
			}
				
			if(itemIndex >= 1 && itemIndex <= this.controller.getCurrentProducts().size()) {
				break;
			}
			
			else {
				this.printStream.println(System.lineSeparator() +"Invalid input. Please try again." + System.lineSeparator());	
			}
				
		}
		
		this.controller.getAttendantConsoleController().removeItem(itemIndex - 1);
		
		this.printStream.println("Item removed successfully." + System.lineSeparator());
		
		this.stateManager.setState(ViewStateEnum.ATTENDANT_MENU);
		
	}

	@Override
	protected void handleItemScanning() {
		printCurrentPurchaseList();
		String availableProductsString = this.availableProductsString();
		availableProductsString += "Please enter a barcode or type 'back' to go back to the main menu:";

		while(true) {
			this.printStream.println(availableProductsString);

			String barcode;
			try {
				barcode = this.inputBarcode();
			} catch (StateChangeException e) {
				this.printStream.println("No item was scanned. Returning to main menu...");
				return;
			}

			double weight;
			try {
				weight = this.inputWeight();
			} catch (StateChangeException e) {
				this.printStream.println("No item was scanned. Returning to main menu...");
				return;
			}

			try {
				this.controller.scanItem(barcode, weight);
			} catch (ItemScanningException e) {
				this.printStream.println(e.getMessage());
				continue;
			}

			// since the scan was successful, update the customer about what they have currently purchased
			this.printCurrentPurchaseList();

			this.stateManager.setState(ViewStateEnum.ITEM_BAGGING);
			return;
		}
	}

	// print the names of purchased items
	// when no purchases have been made yet, list of items will be empty
	private void printCurrentPurchaseList() {
		String outputList = controller.getCustomerOrderSummary();
		this.printStream.println(outputList);
	}

	private String availableProductsString() {
		StringBuilder availableProductsSB = new StringBuilder();
		availableProductsSB.append("------------------").append(System.lineSeparator())
			.append("You can purchase: ").append(System.lineSeparator());

		for (BarcodedProduct product : controller.getFullBarcodedProductList()) {
			availableProductsSB.append(product.getDescription()).append(" (")
				.append(product.getBarcode()).append("): $").append(product.getPrice());
			availableProductsSB.append(System.lineSeparator());
		}
		availableProductsSB.append(System.lineSeparator());
		availableProductsSB.append("------------------");
		availableProductsSB.append(System.lineSeparator());
		return  availableProductsSB.toString();
	}
	
	private String availablePLUProductsString() {
		StringBuilder availableProductsSB = new StringBuilder();
		availableProductsSB.append("------------------").append(System.lineSeparator())
			.append("You can purchase: ").append(System.lineSeparator());

		for (PLUCodedProduct product : controller.getFullPLUCodedProductList()) {
			availableProductsSB.append(product.getDescription()).append(" (")
				.append(product.getPLUCode()).append("): $").append(product.getPrice()).append("/kg");
			availableProductsSB.append(System.lineSeparator());
		}
		availableProductsSB.append(System.lineSeparator());
		availableProductsSB.append("------------------");
		availableProductsSB.append(System.lineSeparator());
		return  availableProductsSB.toString();
	}

	private String inputBarcode() {
		String code = scanner.nextLine().trim();
		if(code.toLowerCase().contentEquals("back")) {
			this.stateManager.setState(ViewStateEnum.MAIN_MENU);
			throw new StateChangeException();
		}
		return code;
	}

	private double inputWeight() {
		while (true) {
			try {
				this.printStream.println("BE the scanning area scale - enter the weight of the item: ");
				String input = scanner.nextLine().trim();
				if(input.toLowerCase().contentEquals("back")) {
					this.stateManager.setState(ViewStateEnum.MAIN_MENU);
					throw new StateChangeException();
				}
				return Double.parseDouble(input);
			} catch (NumberFormatException e) {
				this.printStream.println("Weight must be a decimal number");
			}
		}
	}

	@Override 
	protected void handleAttendantLogout() {
		this.controller.getAttendantConsoleController().logoutAsAttendant();
		this.stateManager.setState(ViewStateEnum.MAIN_MENU);
	}
	
	@Override
	protected void handleMembershipCard() {
		String cardNumber = inputMembershipCardNumber();		
		try {
			this.controller.processMembershipCard(cardNumber);
			// if processing was successful, provide a personalized message for the customer
			String customerName = this.controller.getCurrentCustomerName();
			int points = this.controller.getCurrentAccountPoints();
			this.printStream.println("Hello, " + customerName + "! You currently have " + points + " points.\n");
		} catch (NotAMemberException | InvalidCardException e) {
			this.printStream.println(e.getLocalizedMessage());
		}

		// return to the main menu (either for customer to retry membership account or to start processing purchases)
		this.stateManager.setState(ViewStateEnum.MAIN_MENU);
	}

	private String inputMembershipCardNumber() {
		this.printStream.println("Insert (enter) your membership card number: ");
		String cardNumber = scanner.nextLine().trim();
		if(cardNumber.toLowerCase().contentEquals("back")) {
			this.stateManager.setState(ViewStateEnum.MAIN_MENU);
			throw new StateChangeException();
		}
		return cardNumber;
	}
	
	protected void handleCustomerProductLookup() {
		while (true) {
			this.printStream.println("Enter a price lookup code or product name, type back return to the main memu: ");
			String query;
			String message;
			try {
				query = this.inputBarcode();
				try {
					Integer.parseInt(query);
					message = "Barcode:" + System.lineSeparator() +
							this.controller.lookUpAllBarcodedProductsByDescription(query) +
							System.lineSeparator() +
							"PLU Code:" + System.lineSeparator() +
							this.controller.lookUpAllPLUProductsByDescription(query);
				} catch (NumberFormatException e) {
					message = "Barcode:" + System.lineSeparator() +
						this.controller.lookUpBarcodedProduct(query) +
						System.lineSeparator() +
						"PLU Code:" + System.lineSeparator() +
						this.controller.lookUpAllPLUProductsByDescription(query);
				}
				
				if (!message.isEmpty()) {
					this.printStream.println(message + System.lineSeparator());
				} else {
					this.printStream.println("Invalid barcode or PLU code entered.");
				}
			} catch (StateChangeException e) {
				this.printStream.println("No code was entered. Returning to main menu ...");
				return;
			}
		}
	}

	protected void handlePLUItemInput() {
		printCurrentPurchaseList();
		String availablePLUProductsString = this.availablePLUProductsString();
		availablePLUProductsString += "Please enter a price lookup code or type 'back' to go back to the main menu:";

		while(true) {
			this.printStream.println(availablePLUProductsString);

			String pluCode;
			try {
				pluCode = scanner.nextLine().trim();
				if(pluCode.toLowerCase().contentEquals("back")) {
					this.stateManager.setState(ViewStateEnum.MAIN_MENU);
					throw new StateChangeException();
				}
			} catch (StateChangeException e) {
				this.printStream.println("No price lookup code was entered. Returning to main menu...");
				return;
			}

			double weight;
			try {
				weight = this.inputWeight();
			} catch (StateChangeException e) {
				this.printStream.println("No price lookup code was entered. Returning to main menu...");
				return;
			}

			try {
				this.controller.inputPLUItem(pluCode, weight);
			} catch (InvalidPLUCodeException e) {
				this.printStream.println(e.getMessage());
				continue;
			}

			// since entering the code was successful, update the customer about what they have currently purchased
			this.printCurrentPurchaseList();

			this.stateManager.setState(ViewStateEnum.ITEM_BAGGING);
			return;
		}
	}

	private static class MenuOption {
		public String text;
		public ViewStateEnum selectedState;

		public MenuOption(String text, ViewStateEnum selectedState) {
			this.text = text;
			this.selectedState = selectedState;
		}
	}

	private final MenuOption startPaymentOption = new MenuOption(
		"Pay for items",
		ViewStateEnum.PAYMENT_START
	);

	private final MenuOption addBagsOption = new MenuOption(
		"Add own bags",
		ViewStateEnum.BAG_ADDITION
	);

	private final MenuOption scanItemOption = new MenuOption(
		"Scan an item",
		ViewStateEnum.ITEM_SCANNING
	);

	private final MenuOption insertBanknoteOption = new MenuOption(
		"Insert banknote",
		ViewStateEnum.BANKNOTE_PAYMENT
	);

	private final MenuOption insertCoinOption = new MenuOption(
		"Insert coins",
		ViewStateEnum.COIN_PAYMENT
	);

	private final MenuOption membershipCardOption = new MenuOption(
		"Insert membership card",
		ViewStateEnum.MEMBERSHIP_CARD
	);

	private final MenuOption customerProductLookupOption = new MenuOption(
		"Look up a product",
		ViewStateEnum.CUSTOMER_PRODUCT_LOOKUP
	);
	
	private final MenuOption pluItemInput = new MenuOption(
		"Enter price lookup code",
		ViewStateEnum.PLU_ITEM_INPUT
	);

	private final MenuOption finishPurchaseOption = new MenuOption(
		"Finish purchase",
		ViewStateEnum.FINISH_PURCHASE
	);
	
	private final MenuOption requestForAttendant = new MenuOption(
		"Request for an attendant",
		ViewStateEnum.REQUEST_ATTENDANT_ASSISTANCE
	);
	
	private final MenuOption attendantAddsPaperOption = new MenuOption(
		"Add paper to receipt printer",
		ViewStateEnum.ATTENDANT_RECEIPT_PAPER_ADDITION
	);
	
	private final MenuOption attendantAddsInkOption = new MenuOption(
		"Add ink to receipt printer",	
		ViewStateEnum.ATTENDANT_RECEIPT_INK_ADDITION
	);

	private final MenuOption attendantEmptiesBanknotes = new MenuOption(
	    "Empty banknote storage unit",	
	    ViewStateEnum.ATTENDANT_EMPTY_BANKNOTES
	);

	private final MenuOption attendantEmptiesCoins = new MenuOption(
	    "Empty coin storage unit",	
	    ViewStateEnum.ATTENDANT_EMPTY_COINS
	);
	
	private final MenuOption removeItemOption = new MenuOption(
		"Remove an item",
		ViewStateEnum.ITEM_REMOVAL
	);
	
	private final MenuOption logoutOption = new MenuOption(
		"Logout of Attendant Menu",	
		ViewStateEnum.ATTENDANT_LOGOUT
	);
	
	@Override
	protected void handleRequestAttendantAssistance() {
		while (true) {
			try {
				AttendantCredentials attendantCredentials = this.requestAttendantCredentials(
					"Attendant has been requested!" + System.lineSeparator() +
					"Attendant must enter credentials to continue");
				controller.getAttendantConsoleController().loginAsAttendant(
					attendantCredentials.getAttendantID(),
					attendantCredentials.getAttendantPassword());
				break;
			} catch (IncorrectAttendantLoginInformationException e) {
				this.printStream.println(e.getMessage());
				this.printStream.println("Incorrect credentials, try logging in again");
			}
		}
		this.stateManager.setState(ViewStateEnum.ATTENDANT_MENU);
	}
		    
	@Override
	protected void handleAttendantMenu() {
		List<MenuOption> availableMenuOptions = new ArrayList<>();
		// request attendant assistance state
		if (!this.controller.getCurrentProducts().isEmpty()) {
			availableMenuOptions.add(removeItemOption);
		}
		availableMenuOptions.add(attendantEmptiesBanknotes);
		availableMenuOptions.add(attendantEmptiesCoins);
		availableMenuOptions.add(attendantAddsPaperOption);
		availableMenuOptions.add(attendantAddsInkOption);
		availableMenuOptions.add(logoutOption);
		this.handleMenuOptions(availableMenuOptions);
	}

	@Override
	protected void handleMainMenu() {
		List<MenuOption> availableMenuOptions = new ArrayList<>();
		availableMenuOptions.add(membershipCardOption);
		availableMenuOptions.add(customerProductLookupOption);
		availableMenuOptions.add(addBagsOption);
		availableMenuOptions.add(scanItemOption);
		availableMenuOptions.add(requestForAttendant);
		availableMenuOptions.add(pluItemInput);
		if (!this.controller.getCurrentProducts().isEmpty()) {
			availableMenuOptions.add(startPaymentOption);
		}
		this.handleMenuOptions(availableMenuOptions);
	}

	private void handleMenuOptions(List<MenuOption> availableMenuOptions) {
		StringBuilder fullMenuSB = new StringBuilder();

		fullMenuSB.append("Please select a menu option or type 'exit' to close application:");
		fullMenuSB.append(System.lineSeparator());
		for(int optionNum = 0; optionNum < availableMenuOptions.size(); optionNum++) {
			MenuOption option = availableMenuOptions.get(optionNum);
			fullMenuSB.append(optionNum + 1);
			fullMenuSB.append(": ");
			fullMenuSB.append(option.text);
			fullMenuSB.append(System.lineSeparator());
		}

		while (true) {
			this.printStream.println(fullMenuSB.toString());
			String input = scanner.nextLine().trim();
			if(input.toLowerCase().contentEquals("exit")) {
				this.printStream.println("Exiting application!");
				this.stateManager.setState(ViewStateEnum.EXIT_APPLICATION);
				return;
			}
			int menuChoice;
			try {
				menuChoice = Integer.parseInt(input);
				if (menuChoice <= 0 || menuChoice > availableMenuOptions.size()) {
					this.printStream.println("Invalid choice! Must be an integer in the menu");
					continue;
				}
			} catch (NumberFormatException e) {
				this.printStream.println("Please enter an integer!");
				continue;
			}
			this.stateManager.setState(availableMenuOptions.get(menuChoice - 1).selectedState);
			return;
		}
	}

	@Override
	protected void handleOrderPaymentMenu() {
		List<MenuOption> availableMenuOptions = new ArrayList<>();
		// order payment state
		availableMenuOptions.add(insertBanknoteOption);
		availableMenuOptions.add(insertCoinOption);
		availableMenuOptions.add(finishPurchaseOption);
		this.handleMenuOptions(availableMenuOptions);
	}

	@Override
	protected void handleStationStartup() {
		this.unblockStation("To start station, enter attendant credentials");
	}

	void unblockStation(String message) {
		while (true) {
			try {
				AttendantCredentials credentials = this.requestAttendantCredentials(message);
				controller.getAttendantConsoleController().unblockStation(
					credentials.getAttendantID(), credentials.getAttendantPassword());
				break;
			} catch (IncorrectAttendantLoginInformationException e) {
				this.printStream.println(e.getMessage());
				this.printStream.println("Incorrect credentials, try logging in again");
			}
		}
		this.stateManager.setState(ViewStateEnum.MAIN_MENU);
	}

	private static class AttendantCredentials {
		private final int attendantID;
		private final String attendantPassword;

		public AttendantCredentials(int attendantID, String attendantPassword) {
			this.attendantID = attendantID;
			this.attendantPassword = attendantPassword;
		}

		public int getAttendantID() {
			return this.attendantID;
		}

		public String getAttendantPassword() {
			return  this.attendantPassword;
		}
	}

	private AttendantCredentials requestAttendantCredentials(String message) {
		int attendantID;
		String attendantPassword;
		while(true) {
			this.printStream.println(message);
			this.printStream.println("Please enter the attendant ID number:");
			String attendantIDString = this.scanner.nextLine();

			try {
				attendantID = Integer.parseInt(attendantIDString);
			} catch (NumberFormatException e) {
				this.printStream.println("Attendant ID must be an integer");
				continue;
			}
			break;
		}
		this.printStream.println("Enter the attendant password:");
		attendantPassword = this.scanner.nextLine();
		return new AttendantCredentials(attendantID, attendantPassword);
	}
}
