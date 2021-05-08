package selfcheckout.software.controllers.listeners;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.ElectronicScale;

import static org.junit.Assert.*;

public class ScaleNotificationRecorderTest {

	private ElectronicScale electronicScale;
	private ScaleNotificationRecorder scaleNotificationRecorder;

	@Before
	public void setUp() {
		this.electronicScale = new ElectronicScale(10, 1);
		this.scaleNotificationRecorder = new ScaleNotificationRecorder();
	}

	@Test
	public void testWeightChangedOnce() {
		this.scaleNotificationRecorder.weightChanged(this.electronicScale, 5.0);
		assertEquals(this.scaleNotificationRecorder.getLastWeightChange(), 5.0, 0.001);
		assertEquals(this.scaleNotificationRecorder.getCurrentWeight(), 5.0, 0.001);
	}

	@Test
	public void testWeightChangedMultipleIncrease() {
		this.scaleNotificationRecorder.weightChanged(this.electronicScale, 2.0);
		this.scaleNotificationRecorder.weightChanged(this.electronicScale, 3.0);
		this.scaleNotificationRecorder.weightChanged(this.electronicScale, 4.0);
		assertEquals(this.scaleNotificationRecorder.getLastWeightChange(), 1.0, 0.001);
		assertEquals(this.scaleNotificationRecorder.getCurrentWeight(), 4.0, 0.001);
	}

	@Test
	public void testWeightChangedMultipleDecrease() {
		this.scaleNotificationRecorder.weightChanged(this.electronicScale, 8.0);
		this.scaleNotificationRecorder.weightChanged(this.electronicScale, 7.0);
		this.scaleNotificationRecorder.weightChanged(this.electronicScale, 6.0);
		assertEquals(this.scaleNotificationRecorder.getLastWeightChange(), -1.0, 0.001);
		assertEquals(this.scaleNotificationRecorder.getCurrentWeight(), 6.0, 0.001);
	}

	@Test
	public void testInitiallyNotOverloaded() {
		assertFalse(this.scaleNotificationRecorder.isOverloaded());
	}

	@Test
	public void testBecomesOverloaded() {
		this.scaleNotificationRecorder.overload(this.electronicScale);
		assertTrue(this.scaleNotificationRecorder.isOverloaded());
	}

	@Test
	public void testMovesOutOfOverload() {
		this.scaleNotificationRecorder.overload(this.electronicScale);
		this.scaleNotificationRecorder.outOfOverload(this.electronicScale);
		assertFalse(this.scaleNotificationRecorder.isOverloaded());
	}

	@Test
	public void testClearNotifications() {
		this.scaleNotificationRecorder.enabled(this.electronicScale);
		this.scaleNotificationRecorder.disabled(this.electronicScale);
		this.scaleNotificationRecorder.clearNotifications();
		assertEquals(this.scaleNotificationRecorder.getEnabledNotifications().size(), 0);
		assertEquals(this.scaleNotificationRecorder.getDisabledNotifications().size(), 0);
	}
}
