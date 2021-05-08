package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.CoinDispenser;

import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.exceptions.RefillCoinException;

public class RefillCoinDispenserSubcontrollerTest {
	
	private static final int DISPENSER_CAPACITY = 50;

	private RefillCoinDispenserSubcontroller subcontroller;
	private CoinDispenser dispenser;

	@Before
	public void setup() {
		dispenser = new CoinDispenser(DISPENSER_CAPACITY);
		subcontroller = new RefillCoinDispenserSubcontroller(dispenser, BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY);
	}

	@Test
	public void testNothing() {
		assertEquals(0, dispenser.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongValue() throws RefillCoinException {
		subcontroller.refillCoinDispenser(BigDecimal.valueOf(0.69), ControllerTestConstants.CURRENCY, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongCurrency() throws RefillCoinException {
		subcontroller.refillCoinDispenser(BigDecimal.valueOf(0.05), Currency.getInstance("EGP"), 1);
	}

	@Test(expected = RefillCoinException.class)
	public void testTooMany() throws RefillCoinException {
		subcontroller.refillCoinDispenser(BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY, 999);
	}

	@Test
	public void testNormal() throws RefillCoinException {
		subcontroller.refillCoinDispenser(BigDecimal.valueOf(0.05), ControllerTestConstants.CURRENCY, 5);
		assertEquals(5, dispenser.size());
	}

}
