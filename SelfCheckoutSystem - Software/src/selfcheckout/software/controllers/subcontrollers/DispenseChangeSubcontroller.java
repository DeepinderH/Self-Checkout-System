/*
	THE ALGORITHM:
	1. Start from highest denomination
	2. While the machine still owes the customer money
		1. Give the customer some bills/coins of that denomination until
			a smaller denomination is now needed, or until bills/coins
			of that denomination have run out*
			* If the lowest denomination runs out and the machine still owes
			  money, then there are no more coins left to give and an exception
			  is thrown
		2. Move to the next lowest denomination
*/

package selfcheckout.software.controllers.subcontrollers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.FromStorageEmitter;
import org.lsmr.selfcheckout.devices.OverloadException;

import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.CannotReturnChangeException;
import selfcheckout.software.controllers.exceptions.NoDanglingBanknoteException;
import selfcheckout.software.controllers.listeners.BanknoteSlotNotificationRecorder;
import selfcheckout.software.controllers.listeners.DispenserEmptyNotificationRecorder;

/**
 * Dispenses change to the customer when dispenseChange() is called
 */
public class DispenseChangeSubcontroller {

	/**
	 * A class to track whether or not a certain banknote slot has a dangling bill
	 * or not. DO NOT manually register this listener with a banknote slot. This is done
	 * automatically in the constructor.
	 */
	private static class BanknoteSlotDispenseNextListener extends BanknoteSlotNotificationRecorder {
	
		private final DispenseChangeSubcontroller changeAlgorithm;
		private boolean isFull = false;
		
		/**
		 * Constructs a new instance of BanknoteSlotFullNotificationRecorder
		 * @param slot -- the banknote slot to listen for
		 * @param dispenseChangeSubcontroller -- the change dispensing algorithm to cooperate with
		 */
		public BanknoteSlotDispenseNextListener(BanknoteSlot slot, DispenseChangeSubcontroller dispenseChangeSubcontroller) {
			super();
			this.changeAlgorithm = dispenseChangeSubcontroller;
			slot.register(this);
		}
	
		// If a banknote has been ejected then the slot is now full
		@Override
		public void banknoteEjected(BanknoteSlot slot) {
			this.isFull = true;
		}
	
		// If the change algorithm is in progress, dispense the next piece(s) of change
		// whenever a banknote is taken from the slot
		@Override
		public void banknoteRemoved(BanknoteSlot slot) { 
			this.isFull = false;
			if(this.changeAlgorithm != null && this.changeAlgorithm.isInProgress()) {
				try {
					this.changeAlgorithm.dispenseNext();
				} catch (CannotReturnChangeException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		}
	
		/**
		 * Returns whether or not the banknote slot is full
		 */
		public boolean isFull() {
			return this.isFull;
		}

	}

	// References to the money dispensers
	private final Map<BigDecimal, CoinDispenser> coinDispensers;
	private final Map<BigDecimal, BanknoteDispenser> banknoteDispensers;

	// Reference to the payment and purchase managers
	private final PaymentManager paymentManager;
	private final PurchaseManager purchaseManager;

	// Reference to the banknote slot through which we output change
	private final BanknoteSlot banknoteSlot;

	// Listener to listen for when a dispenser becomes empty or not empty
	private final DispenserEmptyNotificationRecorder dispenserListener;

	// Listener to listen for when the banknote slot becomes full or not full
	private final BanknoteSlotDispenseNextListener banknoteSlotListener;

	// The denominations of currency this machine uses, in ascending order
	private final BigDecimal[] denominations;

	// The threshold at which if the amount owed is less than this much,
	// terminate the algorithm anyway. Equal to (lowest denomination) / 2.
	// For example: in Canada this value would be equal to 0.025$, so
	// if a customer is owed 1.02$, the algorithm would terminate at 1.00$ but
	// if the customer is owed 1.03$, the machine would dispense another 0.05$
	// thereby rounding up.
	private final BigDecimal roundingThreshold;

	// How many steps down from the highest denomination we're dispensing
	private int grain;

	// How much is left owed still
	private BigDecimal amountOwed;

	// How much change has been given up to this point
	private BigDecimal changeGiven;

	// Whether or not the change-dispensing algorithm is still in progress
	// (it waits for the user to pick up a bill before dispensing the next one
	// if multiple bills need to be dispensed)
	private boolean isInProgress = false;

	/**
	 * Constructs an instance of DispenseChangeSubcontroller
	 * @param coinDispensers The machine's coin dispensers
	 * @param banknoteDispensers The machine's banknote dispensers
	 * @param paymentManager The software's payment manager
	 * @param purchaseManager The software's purchase manager
	 */
	public DispenseChangeSubcontroller(
		Map<BigDecimal, CoinDispenser> coinDispensers,
		Map<Integer, BanknoteDispenser> banknoteDispensers,
		BanknoteSlot banknoteSlot,
		PaymentManager paymentManager, 
		PurchaseManager purchaseManager
	) {
		// Assign references
		this.coinDispensers = coinDispensers;
		this.paymentManager = paymentManager;
		this.purchaseManager = purchaseManager;
		this.banknoteSlot = banknoteSlot;

		// Convert the integer keys to BigDecimal keys to make implementation simpler
		this.banknoteDispensers = new HashMap<BigDecimal, BanknoteDispenser>();
		for (Integer denomination : banknoteDispensers.keySet()) {
			this.banknoteDispensers.put(BigDecimal.valueOf(denomination), banknoteDispensers.get(denomination));
		}

		// Create the listener
		this.dispenserListener = new DispenserEmptyNotificationRecorder(this.banknoteDispensers.values(), this.coinDispensers.values());
	
		// Combine coin and banknote denominations into a single collection and sort them
		// in ascending order
		final Set<BigDecimal> denominationsUnordered = new HashSet<BigDecimal>(this.coinDispensers.keySet());
		denominationsUnordered.addAll(this.banknoteDispensers.keySet());
		this.denominations = denominationsUnordered.toArray(new BigDecimal[0]);
		Arrays.sort(this.denominations);

		// See comments above the declaration of roundingThreshold in this class
		this.roundingThreshold = this.denominations[0].divide(BigDecimal.valueOf(2.0));

		// Instantiate the listener that will check whether or not the banknote slot is full
		this.banknoteSlotListener = new BanknoteSlotDispenseNextListener(this.banknoteSlot, this);
	}

	/**
	 * Dispenses change, rounded to the nearest unit of the lowest denomination.
	 * @return How much money the algorithm is going to dispense, after rounding
	 * @throws CannotReturnChangeException When change cannot be returned for any reason
	 */
	public BigDecimal dispenseChange() throws CannotReturnChangeException {

		// Start at the highest denomination
		this.grain = denominations.length - 1;

		// amountOwed = paid - cost
		this.amountOwed = paymentManager.getCurrentPaymentTotal().subtract(purchaseManager.getTotalPrice());
		
		// Self explanatory
		this.changeGiven = BigDecimal.ZERO;
		this.isInProgress = true;
		
		// Round the amount owed. This is the amount that will be dispensed
		// Equal to: round(owed / lowest_denomination) * lowest_denomination
		BigDecimal amountOwedRounded = this.amountOwed;
		amountOwedRounded = amountOwedRounded.divide(denominations[0]);
		amountOwedRounded = amountOwedRounded.setScale(0, RoundingMode.HALF_UP);
		amountOwedRounded = amountOwedRounded.multiply(denominations[0]);

		// Begin the change dispensing operation
		dispenseNext();

		// And return the rounded amount
		return amountOwedRounded;
	}

	/**
	 * DO NOT CALL THIS MANUALLY. Called by a BanknoteSlotListener whenever
	 * a banknote is taken from the slot, signals to the algorithm that it
	 * should continue dispensing change. If called by accident for whatever reason
	 * when there is no change dispensing operation in progress, it will start one.
	 * @throws CannotReturnChangeException
	 */
	private void dispenseNext() throws CannotReturnChangeException {

		// If called by accident for whatever reason
		// when there is no change dispensing operation in progress, then start one.
		if(!isInProgress()) {
			dispenseChange();
			return;
		}

		// While we still owe anything...
		while(this.amountOwed.compareTo(roundingThreshold) > 0) {

			final BigDecimal denomination = denominations[this.grain];

			// Get the dispenser that should be used for this denomination
			final FromStorageEmitter<?> dispenser = getDispenser(denomination);

			// Dispense the current denomination until we owe the 
			// customer less than the denomination's value, or until
			// something goes wrong or the machine runs out
			while(!dispenserListener.isEmpty(dispenser) && (denomination.subtract(roundingThreshold)).compareTo(this.amountOwed) < 0) {
				
				// And if we need to dispense a banknote while the slot is already full, pause
				// the algorithm for now and wait for this method to be called again when
				// the bill is removed
				if(dispenser instanceof BanknoteDispenser && this.banknoteSlotListener.isFull()) {
					return;
				}	

				try {
					// The amount owed and change given must be updated *before* it's emitted
					// because certain event handlers will fire immediately, and call this method
					// immediately with the same amount owed, even though money has been dispensed.
					// However, if the emit() method fails, it will still have updated the amount owed
					// even though nothing was dispensed. That's why in the catch block it undoes
					// these two lines
					this.amountOwed = this.amountOwed.subtract(denomination);
					this.changeGiven = this.changeGiven.add(denomination);

					dispenser.emit();
				} 
				catch(DisabledException | EmptyException | OverloadException e) {

					// The only way this could happen is if the dispenser was disabled (which should
					// not be the case under normal usage), or if there is a discrepancy between
					// dispenserListener.isEmpty() and whether the dispenser is actually empty
					// (which can only occur if the dispenserListener was instantiated with empty
					// dispensers and never filled up, since it assumes dispensers are full to begin with)
					// These are rare scenarios but in the event that this dispenser is unusable for 
					// whatever reason, break the loop and move to the next denomination

					// Read the comment in the try block in this try-catch statement to learn
					// why these lines are here
					this.amountOwed = this.amountOwed.add(denomination);
					this.changeGiven = this.changeGiven.subtract(denomination);

					break;
				}
			}

			// If we owe no more, then just terminate immediately
			if(this.amountOwed.compareTo(roundingThreshold) <= 0) {
				this.amountOwed = BigDecimal.ZERO;
				this.isInProgress = false;
				return;
			}
			// Until we're at the lowest denomination, keep moving down the denominations
			else if(this.grain > 0) {
				this.grain--;
			}
			// However if we're already at the lowest denomination and the lowest
			// denomination has run out, then throw an exception
			else {
				throw new CannotReturnChangeException(this.amountOwed, "Was not able to return change (without giving more than we owe, at least).");
			}
		}
	}

	/**
	 * Returns true if the change-dispensing process is still in progress
	 */
	public boolean isInProgress() {
		return this.isInProgress;
	}

	// Given a certain denomination, returns the dispenser that dispenses it
	private FromStorageEmitter<?> getDispenser(BigDecimal denomination) {
		if(banknoteDispensers.containsKey(denomination)) {
			return banknoteDispensers.get(denomination);
		}
		else {
			return coinDispensers.get(denomination);
		}
	}

	/**
	 * Removes any dangling banknotes from the banknote slot
	 */
	public Banknote removeDanglingBanknote() {
		if(!this.banknoteSlotListener.isFull()) {
			throw new NoDanglingBanknoteException("There is no dangling banknote to be removed");
		}
		return this.banknoteSlot.removeDanglingBanknote();
	}
}
