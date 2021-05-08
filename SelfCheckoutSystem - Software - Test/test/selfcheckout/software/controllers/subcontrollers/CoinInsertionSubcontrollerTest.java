package selfcheckout.software.controllers.subcontrollers;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.*;

import selfcheckout.software.controllers.ControllerTestConstants;

import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.exceptions.CoinRejectedException;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CoinInsertionSubcontrollerTest {

	private Acceptor<Coin> noActionCoinSink;
	private CoinSlot coinSlot;
	private CoinInsertionSubcontroller cis;
	private PaymentManager paymentManager;

	@Before
	public void setUp() {
		this.coinSlot = new CoinSlot();
		CoinValidator coinValidator = new CoinValidator(
			ControllerTestConstants.CURRENCY,
			Arrays.asList(ControllerTestConstants.COIN_DENOMINATIONS));
		this.coinSlot.connect(new UnidirectionalChannel<>(coinValidator));
		Map<BigDecimal, UnidirectionalChannel<Coin>> coinValidatorStandardSinks = new HashMap<>();
		this.noActionCoinSink = new NoActionCoinSink();
		for (BigDecimal coinDenomination : ControllerTestConstants.COIN_DENOMINATIONS) {
			coinValidatorStandardSinks.put(
				coinDenomination, new UnidirectionalChannel<>(this.noActionCoinSink)
			);
		}
		coinValidator.connect(
			new UnidirectionalChannel<>(this.noActionCoinSink),
			coinValidatorStandardSinks,
			new UnidirectionalChannel<>(this.noActionCoinSink));
		this.paymentManager = new PaymentManager();
		this.cis = new CoinInsertionSubcontroller(
			this.coinSlot, coinValidator, this.paymentManager);
	}


	private static class NoActionCoinSink implements Acceptor<Coin> {
		public NoActionCoinSink() {
		}

		@Override
		public void accept(Coin thing) {
			// do nothing
		}

		@Override
		public boolean hasSpace() {
			return true;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentCoin() {
		try {
			this.cis.insertCoin(ControllerTestConstants.INVALID_COIN_DENOMINATION, null);
		} catch (CoinRejectedException | StorageUnitFullException e) {
			fail("should throw an " + IllegalArgumentException.class +
				" for programmatic misconfiguration");
		}
	}

	@Test(expected = CoinRejectedException.class)
	public void testDisabledCoinSlot() throws CoinRejectedException {
		this.coinSlot.disable();
		// when coin slot is disabled, the insertCoin function should
		// catch that and then throw a CoinRejectedException
		try {
			this.cis.insertCoin(
				ControllerTestConstants.COIN_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY);
		} catch (StorageUnitFullException e) {
			fail("storage unit should not be full with first coin");
		}
	}

	private final int NUM_ITERATIONS = 10;

	@Test
	public void testValidCoin() {
		int numCoinRejectedExceptionsThrown = 0;

		// insert a valid coin multiple times
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.cis.insertCoin(
					ControllerTestConstants.COIN_DENOMINATIONS[0],
					ControllerTestConstants.CURRENCY);
			} catch (CoinRejectedException e) {
				numCoinRejectedExceptionsThrown += 1;
			} catch (StorageUnitFullException e) {
				fail("Storage unit should not become full with only a small number of coins input");
			}
		}
		// assert at least one input was valid
		int numAcceptedCoins = NUM_ITERATIONS - numCoinRejectedExceptionsThrown;
		assertTrue(numAcceptedCoins > 1);
		BigDecimal expectedAmount = ControllerTestConstants.COIN_DENOMINATIONS[0]
										.multiply(new BigDecimal(numAcceptedCoins));
		assertEquals(expectedAmount.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}

	@Test
	public void testInvalidCoin() {
		int numCoinRejectedExceptionsThrown = 0;
		// insert an invalid coin multiple times
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.cis.insertCoin(
					ControllerTestConstants.INVALID_COIN_DENOMINATION,
					ControllerTestConstants.CURRENCY);
			} catch (CoinRejectedException e) {
				numCoinRejectedExceptionsThrown += 1;
			} catch (StorageUnitFullException e) {
				fail("Storage unit should not become full when only invalid coins are input");
			}
		}
		// assert all coins were rejected
		assertEquals(numCoinRejectedExceptionsThrown, NUM_ITERATIONS);
		// payment total should be zero
		assertEquals(BigDecimal.ZERO.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}

	private static class FullCoinStorage implements Acceptor<Coin> {

		@Override
		public void accept(Coin thing) throws OverloadException {
			throw new OverloadException("Coin storage full");
		}

		@Override
		public boolean hasSpace() {
			return false;
		}
	}

	@Test(expected = StorageUnitFullException.class)
	public void testStorageFull() throws StorageUnitFullException {
		this.coinSlot.connect(new UnidirectionalChannel<>(new FullCoinStorage()));
		try {
			this.cis.insertCoin(
				ControllerTestConstants.COIN_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY);
		} catch (CoinRejectedException e) {
			fail("StorageUnitFullException should be thrown, no other exceptions");
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testProgrammaticErrorUnvalidatedCoinAccepted() {
		// connect the coin slot to a sink that will simply ignore the coin
		this.coinSlot.connect(new UnidirectionalChannel<>(this.noActionCoinSink));
		try {
			this.cis.insertCoin(
				ControllerTestConstants.COIN_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY);
		} catch (CoinRejectedException | StorageUnitFullException e) {
			fail("Expected ControlSoftwareException to be thrown, not a checked exception");
		}
	}
}
