package selfcheckout.software.controllers.listeners;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.FromStorageEmitter;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;
import org.lsmr.selfcheckout.devices.listeners.CoinDispenserListener;

import selfcheckout.software.controllers.exceptions.NoSuchDenominationException;

public class DispenserEmptyNotificationRecorder extends NotificationRecorder implements BanknoteDispenserListener, CoinDispenserListener {

	// A map of each dispenser and whether or not it's empty
	private final Map<FromStorageEmitter<?>, Boolean> dispensersEmpty = new HashMap<>();

	/**
	 * Creates a new instance of DispenserEmptyNotificationRecorder
	 * @param banknoteDispensers - The machine's banknote dispensers
	 * @param coinDispensers - The machine's coin dispensers
	 */
	public DispenserEmptyNotificationRecorder(Collection<BanknoteDispenser> banknoteDispensers, Collection<CoinDispenser> coinDispensers) {
		super();

		for (BanknoteDispenser dispenser : banknoteDispensers) {
			dispensersEmpty.put(dispenser, false);
			dispenser.register(this);
		}

		for (CoinDispenser dispenser : coinDispensers) {
			dispensersEmpty.put(dispenser, false);
			dispenser.register(this);
		}
	}

	/**
	 * Checks whether a certain dispenser is empty
	 * @param dispenser - The dispenser in question
	 * @return True if the dispenser is empty, false if not.
	 */
	public boolean isEmpty(FromStorageEmitter<?> dispenser) {
		if(!dispensersEmpty.containsKey(dispenser)) {
			throw new NoSuchDenominationException("Tried to check whether a non-existent dispenser was empty.");
		}

		return dispensersEmpty.get(dispenser);
	}

	// DO NOT THE BELOW METHODS YOURSELF. THEY ARE EVENT HANDLERS.

	// NOTE: The hardware never actually fires this event, ever, so it does not
	// need to be tested (nor is it possible to test it)
	@Override
	public void banknotesFull(BanknoteDispenser dispenser) {
		dispensersEmpty.put(dispenser, false);
	}

	@Override
	public void banknotesEmpty(BanknoteDispenser dispenser) {
		dispensersEmpty.put(dispenser, true);
	}

	// NOTE: The hardware never actually fires this event, ever so it does not
	// need to be tested (nor is it possible to test it)
	@Override
	public void banknoteAdded(BanknoteDispenser dispenser, Banknote banknote) {
		dispensersEmpty.put(dispenser, false);
	}

	@Override
	public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		if(banknotes.length > 0) {
			dispensersEmpty.put(dispenser, false);
		}
	}

	@Override
	public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		dispensersEmpty.put(dispenser, true);
	}

	@Override
	public void coinsFull(CoinDispenser dispenser) {
		dispensersEmpty.put(dispenser, false);
	}

	@Override
	public void coinsEmpty(CoinDispenser dispenser) {
		dispensersEmpty.put(dispenser, true);
	}

	@Override
	public void coinAdded(CoinDispenser dispenser, Coin coin) {
		dispensersEmpty.put(dispenser, false);
	}

	@Override
	public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
		if(coins.length > 0) {
			dispensersEmpty.put(dispenser, false);
		}
	}

	@Override
	public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
		dispensersEmpty.put(dispenser, true);
	}

	// UNUSED --------------

	@Override
	public void coinRemoved(CoinDispenser dispenser, Coin coin) { }

	@Override
	public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) { }
	
}
