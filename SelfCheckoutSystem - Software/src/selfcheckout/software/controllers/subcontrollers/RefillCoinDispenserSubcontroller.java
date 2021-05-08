package selfcheckout.software.controllers.subcontrollers;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;

import selfcheckout.software.controllers.exceptions.RefillCoinException;
import selfcheckout.software.controllers.listeners.CoinDispenserNotificationRecorder;

/**
 * Attendant may refill the coins in a given coin dispenser
 *
 */
public class RefillCoinDispenserSubcontroller {
	
	private final CoinDispenser coinDispenser;
	private final CoinDispenserNotificationRecorder coinDispenserNotificationRecorder;
	private final BigDecimal coinDenomination;
	private final Currency currency;
	
	/**
	 * 
	 * @param coinDispenser
	 * 								the coin dispenser to be loaded
	 * @param coinDenomination
	 * 								the denomination of coins that the dispenser contains
	 * @param currency
	 * 								the currency of coins that the dispenser contains
	 */
	public RefillCoinDispenserSubcontroller(CoinDispenser coinDispenser, BigDecimal coinDenomination, Currency currency) {
		this.coinDispenser = coinDispenser;
		this.coinDenomination = coinDenomination;
		this.currency = currency;
		
		//register a coin dispenser listener to the coin Dispenser of interest
		this.coinDispenserNotificationRecorder = new CoinDispenserNotificationRecorder();
		this.coinDispenser.register(coinDispenserNotificationRecorder);
	}
	
	/**
	 * 
	 * @param coinValue
	 * 								the value of the coin(s) to be loaded into the dispenser
	 * @param coinCurrency
	 * 								the currency of the coin(s) to be loaded into the dispenser
	 * @param numAdditionalCoins
	 * 								the number of coin(s) to be loaded into the dispenser
	 * @throws RefillCoinException if the coins cannot be added to the dispenser
	 */
	public void refillCoinDispenser(BigDecimal coinValue, Currency coinCurrency, int numAdditionalCoins) throws RefillCoinException {
		
		//Check if the coins to be loaded are of the same denomination and currency as the dispenser
		if(!coinValue.equals(coinDenomination)) {
			throw new IllegalArgumentException("The coins are of the incorrect denomination for this dispenser");
		}
		
		if(!coinCurrency.equals(currency)) {
			throw new IllegalArgumentException("The coins are of the incorrect currency for this dispenser");
		}
		
		//Create the new coins to be loaded
		Coin[] coins = new Coin[numAdditionalCoins];
		for(int i = 0; i < numAdditionalCoins; i++) {
			coins[i] = new Coin(coinValue, coinCurrency);
		}
		
		this.coinDispenserNotificationRecorder.clearNotifications();
		
		// Try to load the coins
		try {
			coinDispenser.load(coins);
		} catch(SimulationException | OverloadException e) {
			throw new RefillCoinException(e.getLocalizedMessage());
		}
	}
}
