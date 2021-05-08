package selfcheckout.software.controllers.listeners;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.*;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.exceptions.NoSuchDenominationException;

import static org.junit.Assert.*;

public class DispenserEmptyNotificationRecorderTest {

	// Capacity of each dispenser
	private static final int DISPENSER_CAPACITY = 50;
	
	// Storage capacity of banknote storage and coin tray
	private static final int STORAGE_CAPACITY = 500;

	// Number of each type of dispensers (banknote and coin)
	private static final int DISPENSER_COUNT = 5;

	// The total number of dispensers, both banknote and coin
	private static final int TOTAL_DISPENSER_COUNT = DISPENSER_COUNT * 2;

	// References to all the dispensers and the notification recorder to be tested
	private ArrayList<FromStorageEmitter<?>> dispensers;
	private DispenserEmptyNotificationRecorder dispenserListener;

	@Before 
	public void setup() {

		// Make a list of all the coin and banknote dispensers separately because
		// the DispenserEmptyNotificationRecorder needs it that way
		final ArrayList<CoinDispenser> coinDispensers = new ArrayList<>();
		final ArrayList<BanknoteDispenser> banknoteDispensers = new ArrayList<>();

		// Make some hardware just so the machines will work
		final CoinTray coinTray = new CoinTray(STORAGE_CAPACITY);
		final BanknoteSlot banknoteSlot = new BanknoteSlot(false);
		final BanknoteStorageUnit banknoteStorage = new BanknoteStorageUnit(STORAGE_CAPACITY);
		banknoteSlot.connect(new BidirectionalChannel<>(banknoteSlot, banknoteStorage));

		// For DISPENSER_COUNT times...
		for (int i = 0; i < DISPENSER_COUNT; i++) {
			// Make a coin and banknote dispenser
			final CoinDispenser coinDispenser = new CoinDispenser(DISPENSER_CAPACITY);
			final BanknoteDispenser banknoteDispenser = new BanknoteDispenser(DISPENSER_CAPACITY);

			// Connect them to their respective outputs
			coinDispenser.connect(new UnidirectionalChannel<>(coinTray));
			banknoteDispenser.connect(new UnidirectionalChannel<>(banknoteSlot));

			// Create enough coins and banknotes to full them up to full capacity
			final Coin[] coins = new Coin[DISPENSER_CAPACITY];
			final Banknote[] banknotes = new Banknote[DISPENSER_CAPACITY];
			for (int j = 0; j < coins.length; j++) {
				coins[j] = new Coin(BigDecimal.valueOf(1.00), ControllerTestConstants.CURRENCY);
				banknotes[j] = new Banknote(5, ControllerTestConstants.CURRENCY);
			}

			// Load them with said coins and banknotes
			try {
				coinDispenser.load(coins);
				banknoteDispenser.load(banknotes);
			} catch (SimulationException | OverloadException e) {
				e.printStackTrace();
				fail("This should never happen");
			} 

			// And add them to their respective lists
			coinDispensers.add(coinDispenser);
			banknoteDispensers.add(banknoteDispenser);
		}

		// Create the DispenserEmptyNotificationRecorder
		this.dispenserListener = new DispenserEmptyNotificationRecorder(banknoteDispensers, coinDispensers);

		// And create the arraylist with references to all the dispensers
		this.dispensers = new ArrayList<>();
		this.dispensers.addAll(banknoteDispensers);
		this.dispensers.addAll(coinDispensers);
	}

	// Test when all dispensers are full to begin with
	@Test
	public void testFull() {
		for (int i = 0; i < TOTAL_DISPENSER_COUNT; i++) {
			assertFalse("Dispenser was empty", this.dispenserListener.isEmpty(this.dispensers.get(i)));
		}
	}

	// Test when all dispensers are emptied
	@Test
	public void testEmpty() {
		for (int i = 0; i < TOTAL_DISPENSER_COUNT; i++) {
			final FromStorageEmitter<?> dispenser = this.dispensers.get(i);
			for (int j = 0; j < DISPENSER_CAPACITY; j++) {
				try {
					dispenser.emit();
				} catch (DisabledException | EmptyException | OverloadException e) {
					e.printStackTrace();
					fail("This should never happen");
				}
			}
			assertTrue("Dispenser was not empty", this.dispenserListener.isEmpty(this.dispensers.get(i)));
		}
	}
	
	// Test when all dispensers are unloaded
	@Test
	public void testUnloaded() {
		for (int i = 0; i < TOTAL_DISPENSER_COUNT; i++) {
			final FromStorageEmitter<?> dispenser = this.dispensers.get(i);
			
			if(dispenser instanceof CoinDispenser) {
				((CoinDispenser) dispenser).unload();
			}
			else if(dispenser instanceof BanknoteDispenser) {
				((BanknoteDispenser) dispenser).unload();
			}
			
			assertTrue("Dispenser was not empty", this.dispenserListener.isEmpty(this.dispensers.get(i)));
		}
	}
	
	// Test when the dispensers are loaded, but loaded with nothing
	@Test
	public void testLoadNothing() {
		for (int i = 0; i < TOTAL_DISPENSER_COUNT; i++) {
			final FromStorageEmitter<?> dispenser = this.dispensers.get(i);

			try {
				if(dispenser instanceof CoinDispenser) {
					((CoinDispenser) dispenser).unload();
					((CoinDispenser) dispenser).load(new Coin[] { });
				}
				else if(dispenser instanceof BanknoteDispenser) {
					((BanknoteDispenser) dispenser).unload();
					((BanknoteDispenser) dispenser).load(new Banknote[] { });
				}
			} catch (SimulationException | OverloadException e) {
				e.printStackTrace();
				fail("This should never happen");
			}
			
			assertTrue("Dispenser was not empty", this.dispenserListener.isEmpty(this.dispensers.get(i)));
		}
	}
	
	// Test when all the dispensers are actually loaded
	@Test
	public void testLoad() {
		for (int i = 0; i < TOTAL_DISPENSER_COUNT; i++) {
			final FromStorageEmitter<?> dispenser = this.dispensers.get(i);

			try {
				if(dispenser instanceof CoinDispenser) {
					((CoinDispenser) dispenser).unload();
					((CoinDispenser) dispenser).load(new Coin[] { 
						new Coin(BigDecimal.valueOf(1.00), ControllerTestConstants.CURRENCY) 
					});
				}
				else if(dispenser instanceof BanknoteDispenser) {
					((BanknoteDispenser) dispenser).unload();
					((BanknoteDispenser) dispenser).load(new Banknote[] { 
						new Banknote(5, ControllerTestConstants.CURRENCY)
					});
				}
			} catch (SimulationException | OverloadException e) {
				e.printStackTrace();
				fail("This should never happen");
			}
			
			assertFalse("Dispenser was empty", this.dispenserListener.isEmpty(this.dispensers.get(i)));
		}
	}
	
	// Test when any coin dispensers have coins inserted into them, somehow
	@Test
	public void testAdd() {
		for (int i = 0; i < TOTAL_DISPENSER_COUNT; i++) {
			final FromStorageEmitter<?> dispenser = this.dispensers.get(i);

			try {
				if(dispenser instanceof CoinDispenser) {
					((CoinDispenser) dispenser).unload();
					((CoinDispenser) dispenser).accept(
						new Coin(BigDecimal.valueOf(1.00), ControllerTestConstants.CURRENCY));
				}
			} catch (SimulationException | OverloadException | DisabledException e) {
				e.printStackTrace();
				fail("This should never happen");
			}
			
			assertFalse("Dispenser was empty", this.dispenserListener.isEmpty(this.dispensers.get(i)));
		}
	}
	
	// Test when all the coin dispensers become filled all the way to the top
	@Test
	public void testAddFull() {
		for (int i = 0; i < TOTAL_DISPENSER_COUNT; i++) {
			final FromStorageEmitter<?> dispenser = this.dispensers.get(i);

			try {
				if(dispenser instanceof CoinDispenser) {
					((CoinDispenser) dispenser).unload();
					for(int j = 0; j < DISPENSER_CAPACITY; j++) {
						((CoinDispenser) dispenser).accept(
							new Coin(BigDecimal.valueOf(1.00), ControllerTestConstants.CURRENCY));
					}
				}
			} catch (SimulationException | OverloadException | DisabledException e) {
				e.printStackTrace();
				fail("This should never happen");
			}
			
			assertFalse("Dispenser was empty", this.dispenserListener.isEmpty(this.dispensers.get(i)));
		}
	}
	
	// Test when you try to see if a non-existent dispenser is empty
	@Test(expected = NoSuchDenominationException.class)
	public void testNonExistent() {
		this.dispenserListener.isEmpty(new CoinDispenser(5));
	}

	@Test
	public void testCoinDispenserFilled() {
		CoinDispenser existingCoinDispenser = null;
		for (FromStorageEmitter<?> emitter : this.dispensers) {
			if (emitter instanceof CoinDispenser) {
				existingCoinDispenser = (CoinDispenser) emitter;
				break;
			}
		}
		// assert we found a coin dispenser
		assertNotNull(existingCoinDispenser);
		this.dispenserListener.coinsEmpty(existingCoinDispenser);
		assertTrue(this.dispenserListener.isEmpty(existingCoinDispenser));
		this.dispenserListener.coinsFull(existingCoinDispenser);
		assertFalse(this.dispenserListener.isEmpty(existingCoinDispenser));
	}

	@Test
	public void testBanknoteDispenserFilled() {
		BanknoteDispenser existingBanknoteDispenser = null;
		for (FromStorageEmitter<?> emitter : this.dispensers) {
			if (emitter instanceof BanknoteDispenser) {
				existingBanknoteDispenser = (BanknoteDispenser) emitter;
				break;
			}
		}
		// assert we found a coin dispenser
		assertNotNull(existingBanknoteDispenser);
		this.dispenserListener.banknotesEmpty(existingBanknoteDispenser);
		assertTrue(this.dispenserListener.isEmpty(existingBanknoteDispenser));
		this.dispenserListener.banknotesFull(existingBanknoteDispenser);
		assertFalse(this.dispenserListener.isEmpty(existingBanknoteDispenser));
	}

	@Test
	public void testCoinAdded() {
		CoinDispenser existingCoinDispenser = null;
		for (FromStorageEmitter<?> emitter : this.dispensers) {
			if (emitter instanceof CoinDispenser) {
				existingCoinDispenser = (CoinDispenser) emitter;
				break;
			}
		}
		// assert we found a coin dispenser
		assertNotNull(existingCoinDispenser);
		this.dispenserListener.coinsEmpty(existingCoinDispenser);
		assertTrue(this.dispenserListener.isEmpty(existingCoinDispenser));
		this.dispenserListener.coinAdded(
			existingCoinDispenser,
			new Coin(ControllerTestConstants.COIN_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY));
		assertFalse(this.dispenserListener.isEmpty(existingCoinDispenser));
	}

	@Test
	public void testBanknoteAdded() {
		BanknoteDispenser existingBanknoteDispenser = null;
		for (FromStorageEmitter<?> emitter : this.dispensers) {
			if (emitter instanceof BanknoteDispenser) {
				existingBanknoteDispenser = (BanknoteDispenser) emitter;
				break;
			}
		}
		// assert we found a coin dispenser
		assertNotNull(existingBanknoteDispenser);
		this.dispenserListener.banknotesEmpty(existingBanknoteDispenser);
		assertTrue(this.dispenserListener.isEmpty(existingBanknoteDispenser));
		this.dispenserListener.banknoteAdded(
			existingBanknoteDispenser,
			new Banknote(ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY));
		assertFalse(this.dispenserListener.isEmpty(existingBanknoteDispenser));
	}


	// Test we don't crash for unused coinRemoved method
	@Test
	public void testNoCrashBanknoteRemoved() {
		this.dispenserListener.banknoteRemoved(
			new BanknoteDispenser(10),
			new Banknote(ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY));
	}

	// Test we don't crash for unused coinRemoved method
	@Test
	public void testNoCrashCoinRemoved() {
		this.dispenserListener.coinRemoved(
			new CoinDispenser(10),
			new Coin(ControllerTestConstants.COIN_DENOMINATIONS[0],
				     ControllerTestConstants.CURRENCY));
	}

}
