package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.assertEquals;

import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.exceptions.RefillBanknoteException;

public class RefillBanknoteDispenserSubcontrollerTest {
	
	private static final int DISPENSER_CAPACITY = 50;

	private RefillBanknoteDispenserSubcontroller subcontroller;
	private BanknoteDispenser dispenser;

	@Before
	public void setup() {
		dispenser = new BanknoteDispenser(DISPENSER_CAPACITY);
		subcontroller = new RefillBanknoteDispenserSubcontroller(dispenser, 5, ControllerTestConstants.CURRENCY);
	}

	@Test
	public void testNothing() {
		assertEquals(0, dispenser.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongValue() throws RefillBanknoteException {
		subcontroller.refillBanknoteDispenser(4, ControllerTestConstants.CURRENCY, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongCurrency() throws RefillBanknoteException {
		subcontroller.refillBanknoteDispenser(5, Currency.getInstance("EGP"), 1);
	}

	@Test(expected = RefillBanknoteException.class)
	public void testTooMany() throws RefillBanknoteException {
		subcontroller.refillBanknoteDispenser(5, ControllerTestConstants.CURRENCY, 999);
	}

	@Test
	public void testNormal() throws RefillBanknoteException {
		subcontroller.refillBanknoteDispenser(5, ControllerTestConstants.CURRENCY, 5);
		assertEquals(5, dispenser.size());
	}

}
