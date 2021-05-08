package selfcheckout.software.controllers.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.listeners.BanknoteDispenserListener;
/*
 * Listens for any events related to a banknote dispenser
 */
public class BanknoteDispenserNotificationRecorder extends NotificationRecorder implements BanknoteDispenserListener {
	
	//Track the dispensers and banknote(s) related to notifications
	private final ArrayList<BanknoteDispenser> banknotesFullNotification;
	private final ArrayList<BanknoteDispenser> banknotesEmptyNotification;
	private final Map<BanknoteDispenser, Banknote> banknoteAddedNotification;
	private final Map<BanknoteDispenser, Banknote> banknoteRemovedNotification;
	private final Map<BanknoteDispenser, Banknote[]> banknotesLoadedNotification;
	private final Map<BanknoteDispenser, Banknote[]> banknotesUnLoadedNotification;
	
	public BanknoteDispenserNotificationRecorder() {
		super();
		this.banknotesFullNotification = new ArrayList<>();
		this.banknotesEmptyNotification = new ArrayList<>();
		this.banknoteAddedNotification = new HashMap<>();
		this.banknoteRemovedNotification = new HashMap<>();
		this.banknotesLoadedNotification = new HashMap<>();
		this.banknotesUnLoadedNotification = new HashMap<>();
	
	}
	
	//Clear the notification trackers
	@Override
	public void clearNotifications() {
		super.clearNotifications();
		this.banknotesFullNotification.clear();
		this.banknotesEmptyNotification.clear();
		this.banknoteAddedNotification.clear();
		this.banknoteRemovedNotification.clear();
		this.banknotesLoadedNotification.clear();
		this.banknotesUnLoadedNotification.clear();
	}

	//Notifies that a banknote dispenser is full
	@Override
	public void banknotesFull(BanknoteDispenser dispenser) {
		this.banknotesFullNotification.add(dispenser);
		
	}

	//notifies that a banknote dispenser is empty
	//Is not used. Please see DispenserEmptyNotificationRecorder.java
	@Override
	public void banknotesEmpty(BanknoteDispenser dispenser) {
		this.banknotesEmptyNotification.add(dispenser);
		
	}

	/*
	 * Notifies that a banknote was added to the dispenser
	 * Only the most recently added banknote is kept track of.
	 * This event is never actually fired and so does not need to be tested.
	 */
	@Override
	public void banknoteAdded(BanknoteDispenser dispenser, Banknote banknote) {
		if(this.banknoteAddedNotification.containsKey(dispenser)) {
			this.banknoteAddedNotification.replace(dispenser, banknote);
		}else {
			this.banknoteAddedNotification.put(dispenser, banknote);
		}
		
	}

	/*
	 * Notifies that a banknote was removed from the dispenser
	 * Only the most recently removed banknote is kept track of
	 */
	@Override
	public void banknoteRemoved(BanknoteDispenser dispenser, Banknote banknote) {
		if(this.banknoteRemovedNotification.containsKey(dispenser)) {
			this.banknoteRemovedNotification.replace(dispenser, banknote);
		}else {
			this.banknoteRemovedNotification.put(dispenser, banknote);
		}
		
		
	}

	/*
	 * Notifies that a banknote dispenser was loaded
	 * Only the list of most recent loaded banknote(s) is kept track of
	 */
	@Override
	public void banknotesLoaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		if(this.banknotesLoadedNotification.containsKey(dispenser)) {
			this.banknotesLoadedNotification.replace(dispenser, banknotes);
		}else {
			this.banknotesLoadedNotification.put(dispenser, banknotes);
		}
	}

	/*
	 * Notifies that a banknote dispenser was unloaded
	 * Only the list of most recent unloaded banknote(s) is kept track of
	 */
	@Override
	public void banknotesUnloaded(BanknoteDispenser dispenser, Banknote... banknotes) {
		if(this.banknotesUnLoadedNotification.containsKey(dispenser)) {
			this.banknotesUnLoadedNotification.replace(dispenser, banknotes);
		}else {
			this.banknotesUnLoadedNotification.put(dispenser, banknotes);
		}
		
	}
	
	public ArrayList<BanknoteDispenser> getbanknotesFullNotification(){
		return this.banknotesFullNotification;
	}
	
	public ArrayList<BanknoteDispenser> getbanknotesEmptyNotification(){
		return this.banknotesEmptyNotification;
	}
	
	public Map<BanknoteDispenser, Banknote> getbanknoteAddedNotification(){
		return this.banknoteAddedNotification;
	}
	
	public Map<BanknoteDispenser, Banknote> getbanknoteRemovedNotification(){
		return this.banknoteRemovedNotification;
	}
	
	public Map<BanknoteDispenser, Banknote[]> getbanknotesLoadedNotification(){
		return this.banknotesLoadedNotification;
	}
	
	public Map<BanknoteDispenser, Banknote[]> banknotesUnLoadedNotification(){
		return this.banknotesUnLoadedNotification;
	}
}
