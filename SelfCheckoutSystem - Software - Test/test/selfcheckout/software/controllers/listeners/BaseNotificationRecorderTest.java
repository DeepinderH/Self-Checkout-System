package selfcheckout.software.controllers.listeners;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;

import static org.junit.Assert.*;

public class BaseNotificationRecorderTest {

	private AbstractDevice<? extends AbstractDeviceListener> device;
	private NotificationRecorder notificationRecorder;

	private static class BaseNotificationRecorder extends NotificationRecorder {}

	@Before
	public void setUp() {
		this.device = new BarcodeScanner();
		this.notificationRecorder = new BaseNotificationRecorder();
	}

	private static final int NUM_ITERATIONS = 10;

	@Test
	public void testEnabledEmpty() {
		assertEquals(this.notificationRecorder.getEnabledNotifications().size(), 0);
	}

	@Test
	public void testEnabledSingle() {
		this.notificationRecorder.enabled(this.device);
		assertEquals(this.notificationRecorder.getEnabledNotifications().size(), 1);
		assertEquals(this.notificationRecorder.getEnabledNotifications().get(0), this.device);
	}

	@Test
	public void testEnabledMultiple() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			this.notificationRecorder.enabled(this.device);
		}
		assertEquals(this.notificationRecorder.getEnabledNotifications().size(), NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			assertEquals(this.notificationRecorder.getEnabledNotifications().get(i), this.device);
		}
	}

	@Test
	public void testDisabledEmpty() {
		assertEquals(this.notificationRecorder.getDisabledNotifications().size(), 0);
	}

	@Test
	public void testDisabledSingle() {
		this.notificationRecorder.disabled(this.device);
		assertEquals(this.notificationRecorder.getDisabledNotifications().size(), 1);
		assertEquals(this.notificationRecorder.getDisabledNotifications().get(0), this.device);
	}

	@Test
	public void testDisabledMultiple() {
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			this.notificationRecorder.disabled(this.device);
		}
		assertEquals(this.notificationRecorder.getDisabledNotifications().size(), NUM_ITERATIONS);
		for (int i = 0; i < NUM_ITERATIONS; i++) {
			assertEquals(this.notificationRecorder.getDisabledNotifications().get(i), this.device);
		}
	}
	@Test
	public void testClearNotifications() {
		this.notificationRecorder.enabled(this.device);
		this.notificationRecorder.disabled(this.device);
		this.notificationRecorder.clearNotifications();
		assertEquals(this.notificationRecorder.getEnabledNotifications().size(), 0);
		assertEquals(this.notificationRecorder.getDisabledNotifications().size(), 0);
	}
}
