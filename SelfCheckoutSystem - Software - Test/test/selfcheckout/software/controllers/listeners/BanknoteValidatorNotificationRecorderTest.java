package selfcheckout.software.controllers.listeners;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.*;
import selfcheckout.software.controllers.ControllerTestConstants;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BanknoteValidatorNotificationRecorderTest {

	private BanknoteValidator banknoteValidator;
	private BanknoteValidatorNotificationRecorder banknoteValidatorNotificationRecorder;

	@Before
	public void setUp() {
		this.banknoteValidator = new BanknoteValidator(
			ControllerTestConstants.CURRENCY, ControllerTestConstants.BANKNOTE_DENOMINATIONS);
		this.banknoteValidatorNotificationRecorder = new BanknoteValidatorNotificationRecorder();
	}

	private static final int NUM_ITERATIONS = 10;

	@Test
	public void testNoInvalidBanknotes() {
		ArrayList<BanknoteValidator> validNotifications = this.banknoteValidatorNotificationRecorder.getInvalidBanknoteDetectedNotifications();
		assertEquals(validNotifications.size(), 0);
	}

	@Test
	public void testInvalidBanknoteSingle() {
		this.banknoteValidatorNotificationRecorder.invalidBanknoteDetected(this.banknoteValidator);
		ArrayList<BanknoteValidator> invalidNotifications = this.banknoteValidatorNotificationRecorder.getInvalidBanknoteDetectedNotifications();
		assertEquals(invalidNotifications.size(), 1);
		assertEquals(invalidNotifications.get(0), this.banknoteValidator);
	}

	@Test
	public void testInvalidBanknoteMultiple() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			this.banknoteValidatorNotificationRecorder.invalidBanknoteDetected(this.banknoteValidator);
		}
		ArrayList<BanknoteValidator> invalidNotifications = this.banknoteValidatorNotificationRecorder.getInvalidBanknoteDetectedNotifications();
		assertEquals(invalidNotifications.size(), NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			assertEquals(invalidNotifications.get(i), this.banknoteValidator);
		}
	}

	@Test
	public void testNoValidBanknotes() {
		ArrayList<BanknoteValidator> validNotifications = this.banknoteValidatorNotificationRecorder.getValidBanknoteDetectedNotifications();
		assertEquals(validNotifications.size(), 0);
	}

	@Test
	public void testValidBanknoteSingle() {
		this.banknoteValidatorNotificationRecorder.validBanknoteDetected(
			this.banknoteValidator, ControllerTestConstants.CURRENCY,
			ControllerTestConstants.BANKNOTE_DENOMINATIONS[0]);
		ArrayList<BanknoteValidator> validNotifications = this.banknoteValidatorNotificationRecorder.getValidBanknoteDetectedNotifications();
		assertEquals(validNotifications.size(), 1);
		assertEquals(validNotifications.get(0), this.banknoteValidator);
	}

	@Test
	public void testValidBanknoteMultiple() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			this.banknoteValidatorNotificationRecorder.validBanknoteDetected(
				this.banknoteValidator, ControllerTestConstants.CURRENCY,
				ControllerTestConstants.BANKNOTE_DENOMINATIONS[0]);
		}
		ArrayList<BanknoteValidator> validNotifications = this.banknoteValidatorNotificationRecorder.getValidBanknoteDetectedNotifications();
		assertEquals(validNotifications.size(), NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			assertEquals(validNotifications.get(i), this.banknoteValidator);
		}
	}

	@Test
	public void testClearNotifications() {
		this.banknoteValidatorNotificationRecorder.invalidBanknoteDetected(this.banknoteValidator);
		this.banknoteValidatorNotificationRecorder.validBanknoteDetected(
			this.banknoteValidator, ControllerTestConstants.CURRENCY,
			ControllerTestConstants.BANKNOTE_DENOMINATIONS[0]);
		this.banknoteValidatorNotificationRecorder.enabled(this.banknoteValidator);
		this.banknoteValidatorNotificationRecorder.disabled(this.banknoteValidator);
		this.banknoteValidatorNotificationRecorder.clearNotifications();
		assertEquals(this.banknoteValidatorNotificationRecorder.getInvalidBanknoteDetectedNotifications().size(), 0);
		assertEquals(this.banknoteValidatorNotificationRecorder.getValidBanknoteDetectedNotifications().size(), 0);
		assertEquals(this.banknoteValidatorNotificationRecorder.getEnabledNotifications().size(), 0);
		assertEquals(this.banknoteValidatorNotificationRecorder.getDisabledNotifications().size(), 0);
	}
}
