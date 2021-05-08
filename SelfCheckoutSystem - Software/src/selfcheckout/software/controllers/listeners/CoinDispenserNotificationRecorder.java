package selfcheckout.software.controllers.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.listeners.CoinDispenserListener;

/**
 * 
 * Listens for any events related to the a coin dispenser
 *
 */
public class CoinDispenserNotificationRecorder extends NotificationRecorder implements CoinDispenserListener {
	
	//keep track of the dispenser and/or coins for each notification
	private final ArrayList<CoinDispenser> coinsFullNotification;
	private final ArrayList<CoinDispenser> coinsEmptyNotification;
	private final Map<CoinDispenser, Coin> coinAddedNotification;
	private final Map<CoinDispenser, Coin> coinRemovedNotification;
	private final Map<CoinDispenser, Coin[]> coinsLoadedNotification;
	private final Map<CoinDispenser, Coin[]> coinsUnLoadedNotification;
	
	public CoinDispenserNotificationRecorder() {
		super();
		this.coinsFullNotification = new ArrayList<>();
		this.coinsEmptyNotification = new ArrayList<>();
		this.coinAddedNotification = new HashMap<>();
		this.coinRemovedNotification = new HashMap<>();
		this.coinsLoadedNotification = new HashMap<>();
		this.coinsUnLoadedNotification = new HashMap<>();
	
	}
	
	//Clear the notification trackers
	@Override
	public void clearNotifications() {
		super.clearNotifications();
		this.coinsFullNotification.clear();
		this.coinsEmptyNotification.clear();
		this.coinAddedNotification.clear();
		this.coinRemovedNotification.clear();
		this.coinsLoadedNotification.clear();
		this.coinsUnLoadedNotification.clear();
	}

	//Notify that the dispenser is full
	//This notification should never occur (is never called in the hardware)
	@Override
	public void coinsFull(CoinDispenser dispenser) {
		this.coinsFullNotification.add(dispenser);
		
	}

	//Notify that the dispenser is empty
	//This method is not used. See DispenserEmptyNotificationRecorder.java
	@Override
	public void coinsEmpty(CoinDispenser dispenser) {
		this.coinsEmptyNotification.add(dispenser);
		
	}

	/*
	 * Notifies that a coin was added to the dispenser
	 * Only the most recently added coin is kept track of
	 */
	@Override
	public void coinAdded(CoinDispenser dispenser, Coin coin) {
		if(this.coinAddedNotification.containsKey(dispenser)) {
			this.coinAddedNotification.replace(dispenser, coin);
		}else {
			this.coinAddedNotification.put(dispenser, coin);
		}
		
	}

	/*
	 * Notifies that a coin was removed from the dispenser
	 * Only the most recently removed coin is kept track of
	 */
	@Override
	public void coinRemoved(CoinDispenser dispenser, Coin coin) {
		if(this.coinRemovedNotification.containsKey(dispenser)) {
			this.coinRemovedNotification.replace(dispenser, coin);
		}else {
			this.coinRemovedNotification.put(dispenser, coin);
		}
		
		
	}

	/*
	 * Notifies that the dispenser was loaded with coins
	 * Only the list of most recently loaded coin(s) is kept track of
	 */
	@Override
	public void coinsLoaded(CoinDispenser dispenser, Coin... coins) {
		if(this.coinsLoadedNotification.containsKey(dispenser)) {
			this.coinsLoadedNotification.replace(dispenser, coins);
		}else {
			this.coinsLoadedNotification.put(dispenser, coins);
		}
	}

	/*
	 * Notifies that the dispenser was unloaded with coins
	 * Only the list of most recently unloaded coin(s) is kept track of
	 */
	@Override
	public void coinsUnloaded(CoinDispenser dispenser, Coin... coins) {
		if(this.coinsUnLoadedNotification.containsKey(dispenser)) {
			this.coinsUnLoadedNotification.replace(dispenser, coins);
		}else {
			this.coinsUnLoadedNotification.put(dispenser, coins);
		}
		
	}
	
	public ArrayList<CoinDispenser> getcoinsFullNotification(){
		return this.coinsFullNotification;
	}
	
	public ArrayList<CoinDispenser> getcoinsEmptyNotification(){
		return this.coinsEmptyNotification;
	}
	
	public Map<CoinDispenser, Coin> getcoinAddedNotification(){
		return this.coinAddedNotification;
	}
	
	public Map<CoinDispenser, Coin> getcoinRemovedNotification(){
		return this.coinRemovedNotification;
	}
	
	public Map<CoinDispenser, Coin[]> getcoinsLoadedNotification(){
		return this.coinsLoadedNotification;
	}
	
	public Map<CoinDispenser, Coin[]> coinsUnLoadedNotification(){
		return this.coinsUnLoadedNotification;
	}
}

