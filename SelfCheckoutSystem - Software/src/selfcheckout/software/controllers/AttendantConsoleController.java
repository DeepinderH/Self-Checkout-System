package selfcheckout.software.controllers;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.controllers.subcontrollers.ProductLookupSubcontroller;
import selfcheckout.software.controllers.subcontrollers.ItemBaggingSubcontroller;
import selfcheckout.software.controllers.subcontrollers.ItemRemovalSubcontroller;
import selfcheckout.software.controllers.subcontrollers.PrinterRefillSubcontroller;
import selfcheckout.software.controllers.subcontrollers.BanknoteStorageEmptyingSubcontroller;
import selfcheckout.software.controllers.subcontrollers.CoinStorageEmptyingSubcontroller;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.RefillCoinException;
import selfcheckout.software.controllers.subcontrollers.RefillCoinDispenserSubcontroller;
import selfcheckout.software.controllers.exceptions.RefillBanknoteException;
import selfcheckout.software.controllers.subcontrollers.RefillBanknoteDispenserSubcontroller;
import java.math.BigDecimal;
import java.util.Currency;

public class AttendantConsoleController {

	private final SelfCheckoutStation station;
	private final ControllerStateManager controllerStateManager;
	private final PurchaseManager purchaseManager;
	private final ItemBaggingSubcontroller itemBaggingSubcontroller;
	private final PrinterRefillSubcontroller printerRefillSubcontroller;
	private final BanknoteStorageEmptyingSubcontroller banknoteStorageEmptyingSubcontroller;
	private final CoinStorageEmptyingSubcontroller coinStorageEmptyingSubcontroller;
	private final ItemRemovalSubcontroller itemRemovalSubcontroller;
	private final ProductLookupSubcontroller productLookupSubcontroller;

	private final AttendantDatabaseWrapper attendantDatabaseWrapper;

	private static final String NOT_ATTENDANT_ACCESS_ERROR_MSG = "Must be in ATTENDANT_ACCESS controller state to use this function";

	public AttendantConsoleController(
			SelfCheckoutStation station, ProductDatabasesWrapper databaseWrapper,
			PurchaseManager purchaseManager, ControllerStateManager controllerStateManager,
			AttendantDatabaseWrapper attendantDatabaseWrapper) {
		this.station = station;
		this.controllerStateManager = controllerStateManager;
		this.purchaseManager = purchaseManager;

		// construct any required subcontrollers here
		this.banknoteStorageEmptyingSubcontroller = new BanknoteStorageEmptyingSubcontroller(this.station);
		this.coinStorageEmptyingSubcontroller = new CoinStorageEmptyingSubcontroller(this.station);
		this.itemBaggingSubcontroller = new ItemBaggingSubcontroller(
			this.station.baggingArea, this.station.scale, this.purchaseManager);
		this.attendantDatabaseWrapper = attendantDatabaseWrapper;
		this.printerRefillSubcontroller = new PrinterRefillSubcontroller(this.station.printer);
		this.itemRemovalSubcontroller = new ItemRemovalSubcontroller(this.station.baggingArea, this.purchaseManager);
		this.productLookupSubcontroller = new ProductLookupSubcontroller(databaseWrapper);
	}
	
	private ControllerStateEnum getControllerStateEnumStatus() {
		return this.controllerStateManager.getState();
	}
	
	/**
	 * Function to simulate an attendant logging into the attendant console and
	 * verification the login credentials.
	 * 
	 * @param attendantId
	 * 		The ID of the attendant's account
	 * @param attendantPassword
	 * 		The password of the attendent's account
	 * @throws IncorrectAttendantLoginInformationException
	 * 		If the login credentials are invalid
	 */
	public void loginAsAttendant(int attendantId, String attendantPassword) throws IncorrectAttendantLoginInformationException {
		if (this.getControllerStateEnumStatus() == ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException("Already logged in as attendant");
		}
		this.attendantDatabaseWrapper.validateAttendantCredentials(attendantId, attendantPassword);
		this.controllerStateManager.setState(ControllerStateEnum.ATTENDANT_ACCESS);
	}
	
	/**
	 * Function to simulate attendant logging out of the attendant console.
	 */
	public void logoutAsAttendant() {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
	}

	public void approveLastItemWeight(int attendantId, String attendantPassword) throws IncorrectAttendantLoginInformationException, WeightOverloadException {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.DISABLED) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		this.attendantDatabaseWrapper.validateAttendantCredentials(attendantId, attendantPassword);
		this.itemBaggingSubcontroller.acceptWeightMismatch();
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
	}


	/**
	 * Once the customer has scanned an item and wants to skip bagging it,
	 * this method should be called to update the scanning scale and make sure the
	 * item is not added to the bagging area
	 *
	 * Since this can only be done by the attendant, we also take in an attendantId
	 * and attendantPassword
	 */
	public void skipBaggingLastItem(int attendantId, String attendantPassword) throws IncorrectAttendantLoginInformationException {
		if(getControllerStateEnumStatus() != ControllerStateEnum.ITEM_ADDITION) {
			throw new ControlSoftwareException(SelfCheckoutController.NOT_ITEM_ADDITION_STATE_ERROR);
		}
		this.attendantDatabaseWrapper.validateAttendantCredentials(attendantId, attendantPassword);

		Item lastItem = this.purchaseManager.getLastItem();
		this.itemBaggingSubcontroller.skipBaggingItem(lastItem);
	}

	/**
	 * Removes an item from the customer's purchase list and bagging area
	 * 
	 * @param itemIndex index of item to be removed
	 */
	public void removeItem(int itemIndex) {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		
		itemRemovalSubcontroller.removePurchasedItems(itemIndex);
		
	}

	public String lookUpProductByDescription(String description) {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		return this.productLookupSubcontroller.lookupProductByDescription(description);
	}

	public void addPaperToReceiptPrinter(int units) throws InvalidPrinterRefillException {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		// DONE: Create a PrinterRefillSubcontroller to add paper to printer
		// make sure to check that a paper refill notification is sent and
		// to catch any SimulationExceptions thrown and then throw a custom
		// checked exception
		this.printerRefillSubcontroller.attendantAddsPaper(units);
	}

	public void addInkToReceiptPrinter(int quantity) throws InvalidPrinterRefillException {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		// DONE: Create a PrinterRefillSubcontroller to add ink to printer
		// make sure to check that a paper refill notification is sent and
		// to catch any SimulationExceptions thrown and then throw a custom
		// checked exception
		this.printerRefillSubcontroller.attendantAddsInk(quantity);
	}

	public BigDecimal emptyCoinStorageUnit() {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		return this.coinStorageEmptyingSubcontroller.emptyCoinStorage();
	}

	public BigDecimal emptyBanknoteStorageUnit() {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		return this.banknoteStorageEmptyingSubcontroller.emptyBanknoteStorage();
	}

	/**
	 * Function called to simulate adding coins of a certain value to the
	 * appropriate coin dispenser
	 *
	 * @param coinValue
	 *     The value of the coins to add
	 * @param numAdditionalCoins
	 *     The number of additional coins that were added to the dispenser
	 */
	public void refillCoinDispenser(
			BigDecimal coinValue, Currency currency, int numAdditionalCoins
				) throws RefillCoinException {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		CoinDispenser dispenser = this.station.coinDispensers.get(coinValue);
		if(dispenser != null) {
			RefillCoinDispenserSubcontroller refillCoin = new RefillCoinDispenserSubcontroller(dispenser, coinValue, currency);
			refillCoin.refillCoinDispenser(coinValue, currency, numAdditionalCoins);
		} else {
			throw new RefillCoinException("There is not a dispenser that matches the coin value");
		}
	}

	/**
	 * Function called to simulate adding banknotes of a certain value to the
	 * appropriate banknote dispenser
	 *
	 * @param banknoteValue
	 *     The value of the coins to add
	 * @param currency
	 * 		The currency of the banknote dispenser
	 * @param numAdditionalBanknotes
	 *     The number of additional coins that were added to the dispenser
	 */
	public void refillBanknoteDispenser(
			int banknoteValue, Currency currency, int numAdditionalBanknotes)
				throws RefillBanknoteException {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		BanknoteDispenser dispenser = this.station.banknoteDispensers.get(banknoteValue);
		if (dispenser != null) {
			RefillBanknoteDispenserSubcontroller refillBanknote = new RefillBanknoteDispenserSubcontroller(dispenser, banknoteValue, currency);
			refillBanknote.refillBanknoteDispenser(banknoteValue, currency, numAdditionalBanknotes);
		} else {
			throw new RefillBanknoteException("There is not a dispenser that matches the banknote value");
		}
	}

	public void blockStation() {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
	}

	/**
	 * Function that unblocks station, if the Attendant chooses, once they have
	 * input their correct login information
	 * 
	 * @param attendantId
	 * 		the ID of the Attendant's account
	 * 
	 * @param attendantPassword
	 * 		the Password for the Attendants account
	 */
	public void unblockStation(int attendantId, String attendantPassword) throws IncorrectAttendantLoginInformationException {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.DISABLED) {
			throw new ControlSoftwareException("To unblock station, it must currently be blocked");
		}
		this.attendantDatabaseWrapper.validateAttendantCredentials(attendantId, attendantPassword);
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
	}

	public void shutdownStation() {
		if (this.getControllerStateEnumStatus() != ControllerStateEnum.ATTENDANT_ACCESS) {
			throw new ControlSoftwareException(NOT_ATTENDANT_ACCESS_ERROR_MSG);
		}
		// obviously this line cannot be tested as it would stop the tests from running!
		System.exit(0);
	}
}
