package selfcheckout.software.controllers.listeners;

import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.listeners.CoinValidatorListener;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CoinValidatorNotificationRecorder extends NotificationRecorder implements CoinValidatorListener {

	private final ArrayList<CoinValidator> validCoinDetectedNotifications;
	private final ArrayList<CoinValidator> invalidCoinDetectedNotifications;

	public CoinValidatorNotificationRecorder() {
		super();
		this.validCoinDetectedNotifications = new ArrayList<>();
		this.invalidCoinDetectedNotifications = new ArrayList<>();
	}

	@Override
	public void clearNotifications() {
		super.clearNotifications();
		this.validCoinDetectedNotifications.clear();
		this.invalidCoinDetectedNotifications.clear();
	}

	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		this.validCoinDetectedNotifications.add(validator);
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		this.invalidCoinDetectedNotifications.add(validator);
	}

	public ArrayList<CoinValidator> getValidCoinDetectedNotifications() {
		return validCoinDetectedNotifications;
	}

	public ArrayList<CoinValidator> getInvalidCoinDetectedNotifications() {
		return invalidCoinDetectedNotifications;
	}
}
