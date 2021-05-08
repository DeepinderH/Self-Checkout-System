package selfcheckout.software.controllers.subcontrollers;

import java.util.Currency;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;

import selfcheckout.software.controllers.exceptions.RefillBanknoteException;
import selfcheckout.software.controllers.listeners.BanknoteDispenserNotificationRecorder;

/**
 * This class is responsible for letting an attendant refill a banknote dispenser
 */
public class RefillBanknoteDispenserSubcontroller {
	
	private final BanknoteDispenser banknoteDispenser;
	private final BanknoteDispenserNotificationRecorder banknoteDispenserNotificationRecorder;
	private final int banknoteDenomination;
	private final Currency currency;
	
	/**
	 * 
	 * @param banknoteDispenser
	 *								the banknote dispenser to be loaded
	 * @param denomination
	 * 								the denomination of the banknotes in the dispenser
	 * @param currency
	 * 								the currency of the banknotes in the dispenser
	 */
	public RefillBanknoteDispenserSubcontroller(BanknoteDispenser banknoteDispenser, int denomination, Currency currency){
		this.banknoteDispenser = banknoteDispenser;
		this.banknoteDenomination = denomination;
		this.currency = currency;
		//Register a listener to the banknote dispenser
		this.banknoteDispenserNotificationRecorder = new BanknoteDispenserNotificationRecorder();
		this.banknoteDispenser.register(banknoteDispenserNotificationRecorder);
	}
	
	/**
	 * 
	 * @param banknoteValue
	 * 									the value of the banknote(s) to be loaded into the dispenser
	 * @param banknoteCurrency
	 * 									the currency of the banknote(s) to be loaded into the dispenser
	 * @param numAdditionalBanknotes
	 * 									the number of banknotes to be loaded into the dispenser
	 * @throws RefillBanknoteException
	 * 									the exception thrown if there is an error in loading the banknotes
	 * 
	 */
	public void refillBanknoteDispenser(int banknoteValue, Currency banknoteCurrency, int numAdditionalBanknotes) throws RefillBanknoteException {
		
		//Check if the banknotes to be loaded are of the same value and currency as the banknote dispenser
		if(banknoteValue != banknoteDenomination) {
			throw new IllegalArgumentException("The banknote is of the incorrect denomination for this dispenser");
		}
		
		if(!banknoteCurrency.equals(currency)) {
			throw new IllegalArgumentException("The banknote is of the incorrect currency for this dispenser");
		}
		
		//Create the banknotes to be loaded into the dispenser
		Banknote[] banknotes = new Banknote[numAdditionalBanknotes];
		for(int i = 0; i < numAdditionalBanknotes; i++) {
			banknotes[i] = new Banknote(banknoteValue, banknoteCurrency);
		}
		
		this.banknoteDispenserNotificationRecorder.clearNotifications();
		
		//Try to load the banknote dispenser
		try {
			banknoteDispenser.load(banknotes);
		} catch(OverloadException e) {
			throw new RefillBanknoteException(e.getLocalizedMessage());
		}	

	}
	
}
