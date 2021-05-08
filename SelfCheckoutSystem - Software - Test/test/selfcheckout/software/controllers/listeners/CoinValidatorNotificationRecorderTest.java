package selfcheckout.software.controllers.listeners;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.CoinValidator;
import selfcheckout.software.controllers.ControllerTestConstants;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CoinValidatorNotificationRecorderTest {

	private CoinValidator coinValidator;
	private CoinValidatorNotificationRecorder coinValidatorNotificationRecorder;

	@Before
	public void setUp() {
		this.coinValidator = new CoinValidator(
			ControllerTestConstants.CURRENCY, Arrays.asList(ControllerTestConstants.COIN_DENOMINATIONS));
		this.coinValidatorNotificationRecorder = new CoinValidatorNotificationRecorder();
	}

	private static final int NUM_ITERATIONS = 10;

	@Test
	public void testNoInvalidCoins() {
		ArrayList<CoinValidator> validNotifications = this.coinValidatorNotificationRecorder.getInvalidCoinDetectedNotifications();
		assertEquals(validNotifications.size(), 0);
	}

	@Test
	public void testInvalidCoinSingle() {
		this.coinValidatorNotificationRecorder.invalidCoinDetected(this.coinValidator);
		ArrayList<CoinValidator> invalidNotifications = this.coinValidatorNotificationRecorder.getInvalidCoinDetectedNotifications();
		assertEquals(invalidNotifications.size(), 1);
		assertEquals(invalidNotifications.get(0), this.coinValidator);
	}

	@Test
	public void testInvalidCoinMultiple() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			this.coinValidatorNotificationRecorder.invalidCoinDetected(this.coinValidator);
		}
		ArrayList<CoinValidator> invalidNotifications = this.coinValidatorNotificationRecorder.getInvalidCoinDetectedNotifications();
		assertEquals(invalidNotifications.size(), NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			assertEquals(invalidNotifications.get(i), this.coinValidator);
		}
	}

	@Test
	public void testNoValidCoins() {
		ArrayList<CoinValidator> validNotifications = this.coinValidatorNotificationRecorder.getValidCoinDetectedNotifications();
		assertEquals(validNotifications.size(), 0);
	}

	@Test
	public void testValidCoinSingle() {
		this.coinValidatorNotificationRecorder.validCoinDetected(
			this.coinValidator, ControllerTestConstants.COIN_DENOMINATIONS[0]);
		ArrayList<CoinValidator> validNotifications = this.coinValidatorNotificationRecorder.getValidCoinDetectedNotifications();
		assertEquals(validNotifications.size(), 1);
		assertEquals(validNotifications.get(0), this.coinValidator);
	}

	@Test
	public void testValidCoinMultiple() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			this.coinValidatorNotificationRecorder.validCoinDetected(
				this.coinValidator, ControllerTestConstants.COIN_DENOMINATIONS[0]);
		}
		ArrayList<CoinValidator> validNotifications = this.coinValidatorNotificationRecorder.getValidCoinDetectedNotifications();
		assertEquals(validNotifications.size(), NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			assertEquals(validNotifications.get(i), this.coinValidator);
		}
	}

	@Test
	public void testClearNotifications() {
		this.coinValidatorNotificationRecorder.invalidCoinDetected(this.coinValidator);
		this.coinValidatorNotificationRecorder.validCoinDetected(
			this.coinValidator, ControllerTestConstants.COIN_DENOMINATIONS[0]);
		this.coinValidatorNotificationRecorder.enabled(this.coinValidator);
		this.coinValidatorNotificationRecorder.disabled(this.coinValidator);
		this.coinValidatorNotificationRecorder.clearNotifications();
		assertEquals(this.coinValidatorNotificationRecorder.getInvalidCoinDetectedNotifications().size(), 0);
		assertEquals(this.coinValidatorNotificationRecorder.getValidCoinDetectedNotifications().size(), 0);
		assertEquals(this.coinValidatorNotificationRecorder.getEnabledNotifications().size(), 0);
		assertEquals(this.coinValidatorNotificationRecorder.getDisabledNotifications().size(), 0);
	}
}
