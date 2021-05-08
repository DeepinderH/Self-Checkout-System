package selfcheckout.software.controllers.listeners;

import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.listeners.BanknoteValidatorListener;

import java.util.ArrayList;
import java.util.Currency;

public class BanknoteValidatorNotificationRecorder extends NotificationRecorder implements BanknoteValidatorListener {

	private final ArrayList<BanknoteValidator> validBanknoteDetectedNotifications;
	private final ArrayList<BanknoteValidator> invalidBanknoteDetectedNotifications;

	public BanknoteValidatorNotificationRecorder() {
		super();
		this.validBanknoteDetectedNotifications = new ArrayList<>();
		this.invalidBanknoteDetectedNotifications = new ArrayList<>();
	}

	@Override
	public void clearNotifications() {
		super.clearNotifications();
		this.validBanknoteDetectedNotifications.clear();
		this.invalidBanknoteDetectedNotifications.clear();
	}

	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		this.validBanknoteDetectedNotifications.add(validator);
	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		this.invalidBanknoteDetectedNotifications.add(validator);
	}

	public ArrayList<BanknoteValidator> getValidBanknoteDetectedNotifications() {
		return validBanknoteDetectedNotifications;
	}

	public ArrayList<BanknoteValidator> getInvalidBanknoteDetectedNotifications() {
		return invalidBanknoteDetectedNotifications;
	}
}
