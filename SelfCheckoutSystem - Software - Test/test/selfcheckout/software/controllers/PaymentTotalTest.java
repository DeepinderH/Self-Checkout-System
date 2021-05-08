package selfcheckout.software.controllers;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.exceptions.BanknoteRejectedException;
import selfcheckout.software.controllers.exceptions.CoinRejectedException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PaymentTotalTest {

	private BasicSelfCheckoutStation station;
	private ProductDatabasesWrapper dbWrapper;
	private CardIssuer cardIssuer;
	private MembershipDatabaseWrapper memberDatabase;
	private SelfCheckoutController scc;
	private GiftCardDatabaseWrapper giftCardDatabaseWrapper;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;

	@Before
	public void setUp() {
		this.station = new BasicSelfCheckoutStation();
		this.dbWrapper = new ProductDatabasesWrapper();
		ProductDatabasesWrapper.initializeDatabases();
		this.cardIssuer = new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME);
		this.memberDatabase = new MembershipDatabaseWrapper();
		this.giftCardDatabaseWrapper = new GiftCardDatabaseWrapper();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new SelfCheckoutController(
			this.station, this.dbWrapper, cardIssuer, this.memberDatabase,
			giftCardDatabaseWrapper, attendantDatabaseWrapper);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant credentials should be valid during setup");
		}
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, dbWrapper);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);
		this.scc.goToOrderPaymentState();
	}

	private final int NUM_ITERATIONS = 10;

	@Test
	public void initializeZero() {
		assertEquals(BigDecimal.ZERO.compareTo(this.scc.getTotalPayment()), 0);
	}

	@Test
	public void testSingleCoinInput() {
		BigDecimal[] coin_options = ControllerTestConstants.COIN_DENOMINATIONS;
		for (BigDecimal coin_option : coin_options) {
			for (int i = 0; i < NUM_ITERATIONS; i++) {
				// for each type of coin, when a single coin is inserted,
				// the value should be just that coin
				SelfCheckoutController testscc = new SelfCheckoutController(
					this.station, this.dbWrapper, this.cardIssuer,
					this.memberDatabase, this.giftCardDatabaseWrapper,
					this.attendantDatabaseWrapper);
				try {
					testscc.getAttendantConsoleController().unblockStation(
						AttendantConsoleConstant.VALID_ATTENDANT_ID,
						AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
				} catch (IncorrectAttendantLoginInformationException e) {
					fail("Attendant credentials should be valid during setup");
				}
				BasicSelfCheckoutStation.scanOneItemSuccessfully(testscc, dbWrapper);
				BasicSelfCheckoutStation.bagOneItemSuccessfully(testscc);
				testscc.goToOrderPaymentState();
				try {
					testscc.insertCoin(coin_option, ControllerTestConstants.CURRENCY);
				} catch (StorageUnitFullException e) {
					fail("should not throw an error for first coin inserted");
				} catch (CoinRejectedException e) {
					// ignore rejected coins
					continue;
				}
				assertEquals(coin_option, testscc.getTotalPayment());
			}
		}
	}

	@Test
	public void testSingleBanknoteInput() {
		int[] banknote_options = ControllerTestConstants.BANKNOTE_DENOMINATIONS;
		for (int banknote_option : banknote_options) {
			for (int i = 0; i < NUM_ITERATIONS; i++) {
				// for each type of coin, when a single coin is inserted,
				// the value should be just that coin
				SelfCheckoutController testscc = new SelfCheckoutController(
					this.station, this.dbWrapper, this.cardIssuer,
					this.memberDatabase, this.giftCardDatabaseWrapper,
					this.attendantDatabaseWrapper);
				try {
					testscc.getAttendantConsoleController().unblockStation(
						AttendantConsoleConstant.VALID_ATTENDANT_ID,
						AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
				} catch (IncorrectAttendantLoginInformationException e) {
					fail("Attendant credentials should be valid during setup");
				}
				BasicSelfCheckoutStation.scanOneItemSuccessfully(testscc, dbWrapper);
				BasicSelfCheckoutStation.bagOneItemSuccessfully(testscc);
				testscc.goToOrderPaymentState();
				try {
					testscc.insertBanknote(banknote_option, ControllerTestConstants.CURRENCY);
				} catch (StorageUnitFullException e) {
					fail("should not throw an error for first coin inserted");
				} catch (BanknoteRejectedException e) {
					// ignore rejected banknotes
					continue;
				}
				assertEquals(banknote_option, testscc.getTotalPayment().intValue());
			}
		}
	}

	@Test
	public void testMultiCoinInput() {
		BigDecimal[] coin_options = ControllerTestConstants.COIN_DENOMINATIONS;
		BigDecimal expectedTotalPayment = new BigDecimal(0);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			// for each type of coin, when a single coin is inserted,
			// the value should be just that coin
			for (BigDecimal coin_option : coin_options) {
				try {
					this.scc.insertCoin(coin_option, ControllerTestConstants.CURRENCY);
				} catch (StorageUnitFullException e) {
					fail("should not throw an error for first coin inserted");
				} catch (CoinRejectedException e) {
					// ignore rejected coins
					continue;
				}
				expectedTotalPayment = expectedTotalPayment.add(coin_option);
			}
		}
		// ensure that not all payment was rejected
		assertNotEquals(expectedTotalPayment, new BigDecimal(0));
		// ensure that the total payment is correct
		assertEquals(expectedTotalPayment.compareTo(this.scc.getTotalPayment()), 0);
	}

	@Test
	public void testMultiBanknoteInput() {
		int[] banknote_options = ControllerTestConstants.BANKNOTE_DENOMINATIONS;
		int expectedTotalPayment = 0;
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			for (int banknote_option : banknote_options) {
				try {
					this.scc.insertBanknote(banknote_option, ControllerTestConstants.CURRENCY);
				} catch (StorageUnitFullException e) {
					fail("should not throw an error for first coin inserted");
				} catch (BanknoteRejectedException e) {
					// ignore rejected banknotes
					this.scc.removeDanglingBanknote();
					continue;
				}
				expectedTotalPayment += banknote_option;
			}
		}
		// ensure that not all payment was rejected
		assertNotEquals(expectedTotalPayment, 0);
		// ensure that the total payment is correct
		assertEquals(expectedTotalPayment, scc.getTotalPayment().intValue());
	}

	@Test
	public void testMixedInput() {
		BigDecimal[] coin_options = ControllerTestConstants.COIN_DENOMINATIONS;
		int[] banknote_options = ControllerTestConstants.BANKNOTE_DENOMINATIONS;
		BigDecimal expectedTotalPayment = new BigDecimal(0);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			// for each type of coin, when a single coin is inserted,
			// the value should be just that coin
			for (BigDecimal coin_option : coin_options) {
				try {
					this.scc.insertCoin(coin_option, ControllerTestConstants.CURRENCY);
				} catch (StorageUnitFullException e) {
					fail("should not throw an error for first coin inserted");
				} catch (CoinRejectedException e) {
					// ignore rejected coins
					continue;
				}
				expectedTotalPayment = expectedTotalPayment.add(coin_option);
			}
			for (int banknote_option : banknote_options) {
				try {
					this.scc.insertBanknote(banknote_option, ControllerTestConstants.CURRENCY);
				} catch (StorageUnitFullException e) {
					fail("should not throw an error for first coin inserted");
				} catch (BanknoteRejectedException e) {
					// ignore rejected banknotes
					continue;
				}
				expectedTotalPayment = expectedTotalPayment.add(new BigDecimal(banknote_option));
			}
		}
		// ensure that not all payment was rejected
		assertNotEquals(expectedTotalPayment, new BigDecimal(0));
		// ensure that the total payment is correct
		assertEquals(expectedTotalPayment.compareTo(this.scc.getTotalPayment()), 0);
	}
}
