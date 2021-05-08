package selfcheckout.software.controllers.listeners;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.BanknoteSlot;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class BanknoteSlotNotificationRecorderTest {

	private BanknoteSlot banknoteSlot;
	private BanknoteSlotNotificationRecorder banknoteSlotNotificationRecorder;

	@Before
	public void setUp() {
		this.banknoteSlot = new BanknoteSlot(false);
		this.banknoteSlotNotificationRecorder = new BanknoteSlotNotificationRecorder();
	}

	@Test
	public void testBanknoteInsertion() {
		this.banknoteSlotNotificationRecorder.banknoteInserted(this.banknoteSlot);
		ArrayList<BanknoteSlot> slots = this.banknoteSlotNotificationRecorder.getBanknoteInsertedNotifications();
		assertEquals(slots.size(), 1);
		assertEquals(slots.get(0), this.banknoteSlot);
	}

	@Test
	public void testBanknoteEjected() {
		this.banknoteSlotNotificationRecorder.banknoteEjected(this.banknoteSlot);
		ArrayList<BanknoteSlot> slots = this.banknoteSlotNotificationRecorder.getBanknoteEjectedNotifications();
		assertEquals(slots.size(), 1);
		assertEquals(slots.get(0), this.banknoteSlot);
	}

	@Test
	public void testBanknoteRemoved() {
		this.banknoteSlotNotificationRecorder.banknoteRemoved(this.banknoteSlot);
		ArrayList<BanknoteSlot> secondNotificationList = this.banknoteSlotNotificationRecorder.getBanknoteRemovedNotifications();
		assertEquals(secondNotificationList.size(), 1);
		assertEquals(secondNotificationList.get(0), this.banknoteSlot);
	}

	@Test
	public void testClearNotifications() {
		this.banknoteSlotNotificationRecorder.banknoteInserted(this.banknoteSlot);
		this.banknoteSlotNotificationRecorder.banknoteEjected(this.banknoteSlot);
		this.banknoteSlotNotificationRecorder.banknoteRemoved(this.banknoteSlot);
		this.banknoteSlotNotificationRecorder.enabled(this.banknoteSlot);
		this.banknoteSlotNotificationRecorder.disabled(this.banknoteSlot);
		this.banknoteSlotNotificationRecorder.clearNotifications();
		assertEquals(this.banknoteSlotNotificationRecorder.getBanknoteInsertedNotifications().size(), 0);
		assertEquals(this.banknoteSlotNotificationRecorder.getBanknoteEjectedNotifications().size(), 0);
		assertEquals(this.banknoteSlotNotificationRecorder.getBanknoteRemovedNotifications().size(), 0);
		assertEquals(this.banknoteSlotNotificationRecorder.getEnabledNotifications().size(), 0);
		assertEquals(this.banknoteSlotNotificationRecorder.getDisabledNotifications().size(), 0);
	}
}
