package selfcheckout.software.controllers.listeners;

import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.listeners.BanknoteSlotListener;

import java.util.ArrayList;

public class BanknoteSlotNotificationRecorder extends NotificationRecorder implements BanknoteSlotListener {

	private final ArrayList<BanknoteSlot> banknoteInsertedNotifications;
	private final ArrayList<BanknoteSlot> banknoteEjectedNotifications;
	private final ArrayList<BanknoteSlot> banknoteRemovedNotifications;

	public BanknoteSlotNotificationRecorder() {
		super();
		this.banknoteInsertedNotifications = new ArrayList<>();
		this.banknoteEjectedNotifications = new ArrayList<>();
		this.banknoteRemovedNotifications = new ArrayList<>();
	}

	@Override
	public void clearNotifications() {
		super.clearNotifications();
		this.banknoteInsertedNotifications.clear();
		this.banknoteEjectedNotifications.clear();
		this.banknoteRemovedNotifications.clear();
	}

	@Override
	public void banknoteInserted(BanknoteSlot slot) {
		this.banknoteInsertedNotifications.add(slot);
	}

	@Override
	public void banknoteEjected(BanknoteSlot slot) {
		this.banknoteEjectedNotifications.add(slot);
	}

	@Override
	public void banknoteRemoved(BanknoteSlot slot) {
		this.banknoteRemovedNotifications.add(slot);
	}

	public ArrayList<BanknoteSlot> getBanknoteInsertedNotifications() {
		return banknoteInsertedNotifications;
	}

	public ArrayList<BanknoteSlot> getBanknoteEjectedNotifications() {
		return banknoteEjectedNotifications;
	}

	public ArrayList<BanknoteSlot> getBanknoteRemovedNotifications() {
		return banknoteRemovedNotifications;
	}
}
