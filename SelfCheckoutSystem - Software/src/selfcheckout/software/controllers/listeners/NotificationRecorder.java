package selfcheckout.software.controllers.listeners;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.listeners.AbstractDeviceListener;

import java.util.ArrayList;

abstract public class NotificationRecorder implements AbstractDeviceListener {

	private final ArrayList<AbstractDevice<? extends AbstractDeviceListener>> enabledNotifications;
	private final ArrayList<AbstractDevice<? extends AbstractDeviceListener>> disabledNotifications;

	public NotificationRecorder() {
		this.enabledNotifications = new ArrayList<>();
		this.disabledNotifications = new ArrayList<>();
	}

	public void clearNotifications() {
		this.enabledNotifications.clear();
		this.disabledNotifications.clear();
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		this.enabledNotifications.add(device);
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceListener> device) {
		this.disabledNotifications.add(device);
	}

	public ArrayList<AbstractDevice<? extends AbstractDeviceListener>> getEnabledNotifications() {
		return enabledNotifications;
	}

	public ArrayList<AbstractDevice<? extends AbstractDeviceListener>> getDisabledNotifications() {
		return disabledNotifications;
	}
}
