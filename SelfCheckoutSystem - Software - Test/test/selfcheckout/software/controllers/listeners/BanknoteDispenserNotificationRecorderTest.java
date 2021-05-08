package selfcheckout.software.controllers.listeners;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.BanknoteStorageUnit;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.UnidirectionalChannel;

import selfcheckout.software.controllers.ControllerTestConstants;

public class BanknoteDispenserNotificationRecorderTest {
	
	private static final int DISPENSER_CAPACITY = 50;
	
	private BanknoteDispenserNotificationRecorder listener;
	private BanknoteDispenser dispenser;

	@Before
	public void setup() {
		
		listener = new BanknoteDispenserNotificationRecorder();
		dispenser = new BanknoteDispenser(DISPENSER_CAPACITY);

		// Dispenser dispenses into a banknote storage unit instead of a banknote slot
		// just because it doesn't matter and it was easy to implement
		dispenser.connect(new UnidirectionalChannel<>(new BanknoteStorageUnit(999)));

		dispenser.register(listener);
	}

	@Test
	public void testNothing() {
		assertEquals(0, listener.getbanknotesLoadedNotification().size());
		assertEquals(0, listener.banknotesUnLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknoteRemovedNotification().size());
		assertEquals(0, listener.getbanknotesEmptyNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
	}

	private void fill() throws SimulationException, OverloadException {
		Banknote[] banknotes = new Banknote[DISPENSER_CAPACITY];
		for (int i = 0; i < banknotes.length; i++) {
			banknotes[i] = new Banknote(5, ControllerTestConstants.CURRENCY);
		}
		dispenser.load(banknotes);
	}

	@Test
	public void testLoad() throws SimulationException, OverloadException {
		fill();
		assertEquals(0, listener.banknotesUnLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknoteRemovedNotification().size());
		assertEquals(0, listener.getbanknotesEmptyNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
		assertEquals(1, listener.getbanknotesLoadedNotification().size());
	}

	@Test
	public void testRemove() throws SimulationException, OverloadException, EmptyException, DisabledException {
		fill();
		dispenser.emit();
		assertEquals(1, listener.getbanknotesLoadedNotification().size());
		assertEquals(0, listener.banknotesUnLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknotesEmptyNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
		assertEquals(1, listener.getbanknoteRemovedNotification().size());
	}

	@Test
	public void testRemoveAll() throws SimulationException, OverloadException, EmptyException, DisabledException {
		dispenser.load(new Banknote(5, ControllerTestConstants.CURRENCY));
		dispenser.emit();
		assertEquals(1, listener.getbanknotesLoadedNotification().size());
		assertEquals(0, listener.banknotesUnLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknoteRemovedNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
		assertEquals(1, listener.getbanknotesEmptyNotification().size());
	}

	@Test
	public void testUnloaded() throws SimulationException, OverloadException {
		dispenser.load(new Banknote(5, ControllerTestConstants.CURRENCY));
		dispenser.unload();
		assertEquals(1, listener.getbanknotesLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknoteRemovedNotification().size());
		assertEquals(0, listener.getbanknotesEmptyNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
		assertEquals(1, listener.banknotesUnLoadedNotification().size());
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
		dispenser.load(new Banknote(5, ControllerTestConstants.CURRENCY));
		dispenser.load(new Banknote(5, ControllerTestConstants.CURRENCY));
		assertEquals(0, listener.banknotesUnLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknoteRemovedNotification().size());
		assertEquals(0, listener.getbanknotesEmptyNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
		assertEquals(1, listener.getbanknotesLoadedNotification().size());
	}

	@Test 
	public void testRepeatUnload()  throws SimulationException, OverloadException {
		fill();
		dispenser.unload();
		fill();
		dispenser.unload();
		assertEquals(1, listener.getbanknotesLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknoteRemovedNotification().size());
		assertEquals(0, listener.getbanknotesEmptyNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
		assertEquals(1, listener.banknotesUnLoadedNotification().size());
	}

	@Test
	public void testRepeatRemove() throws SimulationException, OverloadException, EmptyException, DisabledException {
		fill();
		dispenser.emit();
		dispenser.emit();
		assertEquals(1, listener.getbanknotesLoadedNotification().size());
		assertEquals(0, listener.banknotesUnLoadedNotification().size());
		assertEquals(0, listener.getbanknoteAddedNotification().size());
		assertEquals(0, listener.getbanknotesEmptyNotification().size());
		assertEquals(0, listener.getbanknotesFullNotification().size());
		assertEquals(1, listener.getbanknoteRemovedNotification().size());
	}

}
