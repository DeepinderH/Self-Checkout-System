package selfcheckout.software.controllers.subcontrollers;

import org.junit.Before;
import org.junit.Test;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.*;

import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.exceptions.BanknoteRejectedException;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import selfcheckout.software.controllers.ControllerTestConstants;

import java.math.BigDecimal;

public class BanknoteInsertionSubcontrollerTest {

	private BanknoteSlot banknoteSlot;
	private BanknoteValidator banknoteValidator;
	private BidirectionalChannel<Banknote> slotValidatorChannel;
	private BanknoteInsertionSubcontroller bis;
	private PaymentManager paymentManager;

	@Before
	public void setUp() {
		this.banknoteSlot = new BanknoteSlot(false);
		this.banknoteValidator = new BanknoteValidator(
			ControllerTestConstants.CURRENCY,
			ControllerTestConstants.BANKNOTE_DENOMINATIONS
		);
		this.slotValidatorChannel = new BidirectionalChannel<>(
			this.banknoteSlot, this.banknoteValidator);
		this.banknoteSlot.connect(slotValidatorChannel);
		this.banknoteValidator.connect(
			slotValidatorChannel,
			new UnidirectionalChannel<>(new NoActionBanknoteSink()));
		this.paymentManager = new PaymentManager();
		this.bis = new BanknoteInsertionSubcontroller(
			this.banknoteSlot, banknoteValidator, paymentManager);
	}

	private static class NoActionBanknoteSink implements Acceptor<Banknote> {
		public NoActionBanknoteSink() {
		}

		@Override
		public void accept(Banknote thing) {
			// do nothing
		}

		@Override
		public boolean hasSpace() {
			return true;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgumentBanknote() {
		try {
			this.bis.insertBanknote(-1, null);
		} catch (BanknoteRejectedException | StorageUnitFullException e) {
			fail("should throw an " + IllegalArgumentException.class +
				" for programmatic misconfiguration");
		}
	}

	@Test(expected = BanknoteRejectedException.class)
	public void testDisabledBanknoteSlot() throws BanknoteRejectedException {
		this.banknoteSlot.disable();
		// when banknote slot is disabled, the insertBanknote function should
		// catch that and then throw a BanknoteRejectedException
		try {
			this.bis.insertBanknote(
				ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY);
		} catch (StorageUnitFullException e) {
			fail("storage unit should not be full with first banknote");
		}
	}

	@Test(expected = BanknoteRejectedException.class)
	public void testOverloadedBanknoteSlot() throws BanknoteRejectedException {
		// a value of 123
		Banknote invalidBanknote = new Banknote(
			ControllerTestConstants.INVALID_BANKNOTE_DENOMINATION,
			ControllerTestConstants.CURRENCY);
		// leave a dangling banknote
		try {
			this.banknoteSlot.emit(invalidBanknote);
		} catch (DisabledException e) {
			fail("banknote slot should not be disabled during this test");
		}
		// when there is a dangling banknote, an OverloadException
		// will be thrown in the BanknoteSlot
		// we are testing that this OverloadException is caught and
		// correctly handled by the SelfCheckoutController
		try {
			this.bis.insertBanknote(
				ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY);
		} catch (StorageUnitFullException e) {
			fail("storage unit should not be considered full when a banknote is dangling");
		}

	}

	private final int NUM_ITERATIONS = 10;

	@Test
	public void testValidBanknote() {
		int numBanknoteRejectedExceptionsThrown = 0;

		// insert a valid banknote multiple times
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.bis.insertBanknote(
					ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
					ControllerTestConstants.CURRENCY);
			} catch (BanknoteRejectedException e) {
				numBanknoteRejectedExceptionsThrown += 1;
				this.bis.removeDanglingBanknote();
			} catch (StorageUnitFullException e) {
				fail("Storage unit should not become full with only a small number of banknotes input");
			}
		}
		// assert at least one input was valid
		int numValidCoins = NUM_ITERATIONS - numBanknoteRejectedExceptionsThrown;
		assertTrue(numValidCoins > 1);
		// payment total should be equal to the input banknote denomination
		// multiplied by the number of successful insertions
		BigDecimal inputBanknoteValue = new BigDecimal(ControllerTestConstants.BANKNOTE_DENOMINATIONS[0]);
		BigDecimal expectedAmount = inputBanknoteValue.multiply(new BigDecimal(numValidCoins));
		assertEquals(expectedAmount.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}

	@Test
	public void testInvalidBanknote() {
		int numBanknoteRejectedExceptionsThrown = 0;
		// insert an invalid banknote multiple times
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.bis.insertBanknote(
					ControllerTestConstants.INVALID_BANKNOTE_DENOMINATION,
					ControllerTestConstants.CURRENCY);
			} catch (BanknoteRejectedException e) {
				numBanknoteRejectedExceptionsThrown += 1;
				this.bis.removeDanglingBanknote();
			} catch (StorageUnitFullException e) {
				fail("Storage unit should not become full when only invalid banknotes are input");
			}
		}
		// assert all banknotes were rejected
		assertEquals(numBanknoteRejectedExceptionsThrown, NUM_ITERATIONS);
		// payment total should be zero
		assertEquals(BigDecimal.ZERO.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}

	private static class FullBanknoteStorage implements Acceptor<Banknote> {

		@Override
		public void accept(Banknote thing) throws OverloadException {
			throw new OverloadException("Overloaded");
		}

		@Override
		public boolean hasSpace() {
			return false;
		}
	}

	@Test(expected = StorageUnitFullException.class)
	public void testStorageFull() throws StorageUnitFullException {
		// connect the banknote validator to a full storage unit
		this.banknoteValidator.connect(slotValidatorChannel,
			new UnidirectionalChannel<>(new FullBanknoteStorage()));
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			try {
				this.bis.insertBanknote(
					ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
					ControllerTestConstants.CURRENCY);
			} catch (BanknoteRejectedException e) {
				// the BanknoteValidator will sporadically validate
				// incorrectly. Simply remove dangling banknote when
				// this is the case as we are only testing that a
				// StorageUnitFullException is raised
				this.bis.removeDanglingBanknote();
			}
		}
	}

	@Test(expected = ControlSoftwareException.class)
	public void testProgrammaticErrorUnvalidatedBanknoteAccepted() {
		// connect the banknote slot to a sink that will simply ignore the banknote
		this.banknoteSlot.connect(
			new BidirectionalChannel<>(
				this.banknoteSlot, new NoActionBanknoteSink()));
		try {
			this.bis.insertBanknote(
				ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY);
		} catch (BanknoteRejectedException | StorageUnitFullException e) {
			fail("Expected ControlSoftwareException to be thrown, not a checked exception");
		}
	}

	private static class ReturningBanknoteSink implements Acceptor<Banknote> {
		BanknoteSlot banknoteSlot;

		public ReturningBanknoteSink(BanknoteSlot banknoteSlot) {
			this.banknoteSlot = banknoteSlot;
		}

		@Override
		public void accept(Banknote banknote) {
			try {
				// when we receive a banknote, simply return it to the
				// banknote slot
				banknoteSlot.emit(banknote);
			} catch (DisabledException e) {
				fail("unexpected disabled exception");
			}
		}

		@Override
		public boolean hasSpace() {
			return true;
		}
	}


	@Test(expected = ControlSoftwareException.class)
	public void testProgrammaticErrorUnvalidatedBanknoteRejected() {
		// connect the banknote slot to a sink that will simply return the banknote
		ReturningBanknoteSink returningBanknoteSink = new ReturningBanknoteSink(
			this.banknoteSlot);
		BidirectionalChannel<Banknote> bidirectionalChannel = new BidirectionalChannel<>(
			this.banknoteSlot, returningBanknoteSink);
		this.banknoteSlot.connect(bidirectionalChannel);

		try {
			this.bis.insertBanknote(
				ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
				ControllerTestConstants.CURRENCY);
		} catch (BanknoteRejectedException | StorageUnitFullException e) {
			fail("Expected ControlSoftwareException to be thrown, not a checked exception");
		}
	}
}
