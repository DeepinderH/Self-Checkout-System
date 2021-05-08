package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.BidirectionalChannel;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.UnidirectionalChannel;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.CannotReturnChangeException;

public class DispenseChangeSubcontrollerTest {

	// Capacity of each dispenser
	private static final int DISPENSER_CAPACITY = 50; 

	// Storage capacity of coin tray and banknote storage
	private static final int STORAGE_CAPACITY = 500; 

	private final BanknoteSlotListener banknoteListener = new BanknoteSlotListener() {

		@Override
		public void banknoteEjected(BanknoteSlot slot) {
			dispensedBanknotes.add(slot.removeDanglingBanknote());
		}

		@Override
		public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) { }

		@Override
		public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) { }

		@Override
		public void banknoteInserted(BanknoteSlot slot) { }

		@Override
		public void banknoteRemoved(BanknoteSlot slot) { }
		
	};

	// References to software
	private PaymentManager paymentManager;
	private PurchaseManager purchaseManager;
	private DispenseChangeSubcontroller changeDispenser;

	// References to hardware
	private CoinTray coinTray;
	private BanknoteSlot banknoteSlot;
	private HashMap<BigDecimal, CoinDispenser> coinDispensers;
	private HashMap<Integer, BanknoteDispenser> banknoteDispensers;

	// The banknotes that were dispensed in any given test
	private ArrayList<Banknote> dispensedBanknotes;

	@Before
	public void setup() {

		// Instantiate everything
		this.dispensedBanknotes = new ArrayList<>();

		this.purchaseManager = new PurchaseManager(new ProductDatabasesWrapper());
		this.paymentManager = new PaymentManager();

		this.coinDispensers = new HashMap<>();
		this.banknoteDispensers = new HashMap<>();

		this.coinTray = new CoinTray(STORAGE_CAPACITY);
		this.banknoteSlot = new BanknoteSlot(true);

		// Create the banknote storage unit and connect it to the banknote slot
		final BanknoteStorageUnit banknoteStorage = new BanknoteStorageUnit(STORAGE_CAPACITY);
		this.banknoteSlot.connect(new BidirectionalChannel<>(this.banknoteSlot, banknoteStorage));

		// For each denomination, create a dispenser, fill it to max capacity with
		// coins, and add this dispenser to the coin dispenser list
		for (final BigDecimal denomination : ControllerTestConstants.COIN_DENOMINATIONS) {
			final CoinDispenser dispenser = new CoinDispenser(DISPENSER_CAPACITY);
			final Coin[] coins = new Coin[DISPENSER_CAPACITY];

			for (int i = 0; i < DISPENSER_CAPACITY; i++) {
				coins[i] = new Coin(denomination, ControllerTestConstants.CURRENCY);
			}

			try {
				dispenser.load(coins);
			} catch (SimulationException | OverloadException e) {
				e.printStackTrace();
				fail("This should never happen");
			}

			dispenser.connect(new UnidirectionalChannel<>(this.coinTray));
			this.coinDispensers.put(denomination, dispenser);
		}

		// For each denomination, create a dispenser, fill it to max capacity with
		// banknotes, and add this dispenser to the banknote dispenser list
		for (final int denomination : ControllerTestConstants.BANKNOTE_DENOMINATIONS) {
			final BanknoteDispenser dispenser = new BanknoteDispenser(DISPENSER_CAPACITY);
			final Banknote[] banknotes = new Banknote[DISPENSER_CAPACITY];

			for (int i = 0; i < DISPENSER_CAPACITY; i++) {
				banknotes[i] = new Banknote(denomination, ControllerTestConstants.CURRENCY);
			}

			try {
				dispenser.load(banknotes);
			} catch (SimulationException | OverloadException e) {
				e.printStackTrace();
				fail("This should never happen");
			}

			dispenser.connect(new UnidirectionalChannel<>(this.banknoteSlot));
			this.banknoteDispensers.put(denomination, dispenser);
		}

		// Create a banknote slot listener that will automatically take every
		// banknote that was dispensed and add it to a list for use in testing
		this.banknoteSlot.register(banknoteListener);

		// Instantiate the change dispenser
		this.changeDispenser = new DispenseChangeSubcontroller(this.coinDispensers, this.banknoteDispensers, this.banknoteSlot, this.paymentManager, this.purchaseManager);
		
	}

	// Passes the test when only the desired money get dispensed
	private void assertDispensed(Double... money) {

		// Make a list of "remaining money" items
		final ArrayList<Double> remaining = new ArrayList<>();
		remaining.addAll(Arrays.asList(money));

		// Get all the coins from the coin tray
		final ArrayList<Coin> collectedCoins = new ArrayList<>();
		collectedCoins.addAll(this.coinTray.collectCoins());

		// For some reason, coinTray.collectCoins() includes null, so
		// strip all those from the list
		for (int i = collectedCoins.size() - 1; i >= 0; i--) {
			if(collectedCoins.get(i) == null) {
				collectedCoins.remove(i);
			}
		}

		// Make sure the exact same number of banknotes+coins were dispensed as we need
		assertEquals(remaining.size(), collectedCoins.size() + this.dispensedBanknotes.size());

		// Go through each coin dispensed and remove it from the "remaining" list if
		// it's on there
		for (final Coin coin : collectedCoins) {
			for (int i = remaining.size() - 1; i >= 0; i--) {
				if(coin.getValue().compareTo(BigDecimal.valueOf(remaining.get(i))) == 0) {
					remaining.remove(i);
				}
			}
		}

		// Do the same as above but with banknotes
		for(final Banknote banknote : this.dispensedBanknotes) {
			for (int i = remaining.size() - 1; i >= 0; i--) {
				if(banknote.getValue() == remaining.get(i)) {
					remaining.remove(i);
				}
			}
		}

		// If there are no remaining money items left then we've passed
		assertEquals(0, remaining.size());
	}

	// Test when no change is required
	@Test 
	public void testNothing() throws CannotReturnChangeException {
		assertTrue(BigDecimal.valueOf(0.00).compareTo(changeDispenser.dispenseChange()) == 0);
		assertDispensed();
	}

	// Test when only coins are dispensed
	@Test 
	public void testCoinsOnly() throws CannotReturnChangeException {
		paymentManager.addPayment(BigDecimal.valueOf(1.29));
		assertTrue(BigDecimal.valueOf(1.30).compareTo(changeDispenser.dispenseChange()) == 0);
		assertDispensed(1.00, 0.25, 0.05);
	}

	// Test when only bills are dispensed
	@Test 
	public void testBillsOnly() throws CannotReturnChangeException {
		paymentManager.addPayment(BigDecimal.valueOf(35));
		assertTrue(BigDecimal.valueOf(35.00).compareTo(changeDispenser.dispenseChange()) == 0);
		assertDispensed(20.00, 10.00, 5.00);
	}

	// Test when both bills and coins are dispensed
	@Test
	public void testBillsAndCoins() throws CannotReturnChangeException {
		paymentManager.addPayment(BigDecimal.valueOf(237.88));
		assertTrue(BigDecimal.valueOf(237.90).compareTo(changeDispenser.dispenseChange()) == 0);
		assertDispensed(100.00, 100.00, 20.00, 10.00, 5.00, 2.00, 0.25, 0.25, 0.25, 0.1, 0.05);
	}

	// Test to make sure it throws an error when it can't give change
	@Test(expected = CannotReturnChangeException.class)
	public void testImpossible() throws CannotReturnChangeException {
		coinDispensers.get(BigDecimal.valueOf(0.05)).unload();
		paymentManager.addPayment(BigDecimal.valueOf(1.30));
		changeDispenser.dispenseChange();
	}

	// Test to make sure it dispenses change when possible, even when
	// some dispensers are empty
	@Test
	public void testMissingDenominationsCoinsOnly() throws CannotReturnChangeException {
		coinDispensers.get(new BigDecimal("0.10")).unload();
		coinDispensers.get(new BigDecimal("0.25")).unload();
		coinDispensers.get(new BigDecimal("1.00")).unload();
		coinDispensers.get(new BigDecimal("2.00")).unload();

		final double changeAmount = 1.95;
		final int amountOfCoins = (int) (changeAmount / 0.05);
		final Double[] coins = new Double[amountOfCoins];
		for (int i = 0; i < amountOfCoins; i++) {
			coins[i] = 0.05;
		}
		
		paymentManager.addPayment(BigDecimal.valueOf(changeAmount));
		assertTrue(BigDecimal.valueOf(changeAmount).compareTo(changeDispenser.dispenseChange()) == 0);
		assertDispensed(coins);
	}

	// Test to make sure it throws an exception when a machine is disabled
	// for some reason while change is being given
	@Test
	public void testMissingDenominationsBillsAndCoins() throws CannotReturnChangeException {
		coinDispensers.get(new BigDecimal("1.00")).disable();
		banknoteDispensers.get(10).disable();
		paymentManager.addPayment(new BigDecimal("11.30"));
		assertTrue(BigDecimal.valueOf(11.30).compareTo(changeDispenser.dispenseChange()) == 0);
		assertDispensed(5.00, 5.00, 0.25, 0.25, 0.25, 0.25, 0.25, 0.05);
	}

	// Test when both bills and coins are dispensed
	@Test
	public void testBillsFull() throws CannotReturnChangeException {
		banknoteSlot.deregister(this.banknoteListener);
		paymentManager.addPayment(BigDecimal.valueOf(15.00));
		assertTrue(BigDecimal.valueOf(15.00).compareTo(changeDispenser.dispenseChange()) == 0);
		assertTrue(changeDispenser.isInProgress());
	}

}
