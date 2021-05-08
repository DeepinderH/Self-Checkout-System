package selfcheckout.software.controllers.listeners;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.CoinTray;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.UnidirectionalChannel;

import selfcheckout.software.controllers.ControllerTestConstants;

public class CoinDispenserNotificationRecorderTest {
	
	private static final int DISPENSER_CAPACITY = 50;
	
	private CoinDispenserNotificationRecorder listener;
	private CoinDispenser dispenser;

	@Before
	public void setup() {
		
		listener = new CoinDispenserNotificationRecorder();
		dispenser = new CoinDispenser(DISPENSER_CAPACITY);

		// Dispenser dispenses into a banknote storage unit instead of a banknote slot
		// just because it doesn't matter and it was easy to implement
		dispenser.connect(new UnidirectionalChannel<>(new CoinTray(999)));

		dispenser.register(listener);
	}

	@Test
	public void testNothing() {
		assertEquals(0, listener.getcoinsLoadedNotification().size());
		assertEquals(0, listener.coinsUnLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(0, listener.getcoinRemovedNotification().size());
		assertEquals(0, listener.getcoinsEmptyNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
	}

	private void fill() throws SimulationException, OverloadException {
		Coin[] coins = new Coin[DISPENSER_CAPACITY];
		for (int i = 0; i < coins.length; i++) {
			coins[i] = new Coin(BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY);
		}
		dispenser.load(coins);
	}

	@Test
	public void testLoad() throws SimulationException, OverloadException {
		fill();
		assertEquals(0, listener.coinsUnLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(0, listener.getcoinRemovedNotification().size());
		assertEquals(0, listener.getcoinsEmptyNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
		assertEquals(1, listener.getcoinsLoadedNotification().size());
	}

	@Test
	public void testRemove() throws SimulationException, OverloadException, EmptyException, DisabledException {
		fill();
		dispenser.emit();
		assertEquals(1, listener.getcoinsLoadedNotification().size());
		assertEquals(0, listener.coinsUnLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(0, listener.getcoinsEmptyNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
		assertEquals(1, listener.getcoinRemovedNotification().size());
	}

	@Test
	public void testRemoveAll() throws SimulationException, OverloadException, EmptyException, DisabledException {
		dispenser.load(new Coin(BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY));
		dispenser.emit();
		assertEquals(1, listener.getcoinsLoadedNotification().size());
		assertEquals(0, listener.coinsUnLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(1, listener.getcoinRemovedNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
		assertEquals(1, listener.getcoinsEmptyNotification().size());
	}

	@Test
	public void testUnloaded() throws SimulationException, OverloadException {
		dispenser.load(new Coin(BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY));
		dispenser.unload();
		assertEquals(1, listener.getcoinsLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(0, listener.getcoinRemovedNotification().size());
		assertEquals(0, listener.getcoinsEmptyNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
		assertEquals(1, listener.coinsUnLoadedNotification().size());
	}
	
	@Test 
	public void testClear() throws SimulationException, OverloadException {
		testLoad();
		listener.clearNotifications();
		testNothing();
	}
	
	// The reason repeats are tested is because repeat notifications are supposed
	// to overwrite the previous one

	@Test 
	public void testRepeatLoad() throws SimulationException, OverloadException {
		dispenser.load(new Coin(BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY));
		dispenser.load(new Coin(BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY));
		assertEquals(0, listener.coinsUnLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(0, listener.getcoinRemovedNotification().size());
		assertEquals(0, listener.getcoinsEmptyNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
		assertEquals(1, listener.getcoinsLoadedNotification().size());
	}

	@Test 
	public void testRepeatUnload()  throws SimulationException, OverloadException {
		fill();
		dispenser.unload();
		fill();
		dispenser.unload();
		assertEquals(1, listener.getcoinsLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(0, listener.getcoinRemovedNotification().size());
		assertEquals(0, listener.getcoinsEmptyNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
		assertEquals(1, listener.coinsUnLoadedNotification().size());
	}

	@Test
	public void testRepeatRemove() throws SimulationException, OverloadException, EmptyException, DisabledException {
		fill();
		dispenser.emit();
		dispenser.emit();
		assertEquals(1, listener.getcoinsLoadedNotification().size());
		assertEquals(0, listener.coinsUnLoadedNotification().size());
		assertEquals(0, listener.getcoinAddedNotification().size());
		assertEquals(0, listener.getcoinsEmptyNotification().size());
		assertEquals(0, listener.getcoinsFullNotification().size());
		assertEquals(1, listener.getcoinRemovedNotification().size());
	}

}
