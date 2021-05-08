package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinSlot;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.SimulationException;
import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.exceptions.CoinRejectedException;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;
import selfcheckout.software.controllers.listeners.CoinValidatorNotificationRecorder;

import java.math.BigDecimal;
import java.util.Currency;

public class CoinInsertionSubcontroller {

	private final CoinSlot coinSlot;
	private final PaymentManager paymentManager;
	private final CoinValidatorNotificationRecorder coinValidatorNotificationRecorder;

	public CoinInsertionSubcontroller(CoinSlot coinSlot, CoinValidator coinValidator,
	                                  PaymentManager paymentManager) {
		this.coinSlot = coinSlot;
		this.paymentManager = paymentManager;

		this.coinValidatorNotificationRecorder = new CoinValidatorNotificationRecorder();
		coinValidator.register(this.coinValidatorNotificationRecorder);
	}

	public void insertCoin(BigDecimal coinValue, Currency coinCurrency) throws CoinRejectedException, StorageUnitFullException {
		Coin coin;
		try {
			coin = new Coin(coinValue, coinCurrency);
		} catch (SimulationException e) {
			throw new IllegalArgumentException("insertCoin parameters are invalid");
		}
		this.coinValidatorNotificationRecorder.clearNotifications();
		try {
			this.coinSlot.accept(coin);
		} catch (DisabledException e) {
			throw new CoinRejectedException(
				"Coin acceptor is disabled. Please try a different payment method");
		} catch (SimulationException e) {
			throw new StorageUnitFullException(
				"Coin storage unit is full. Please try a different payment method");
		}
		if (this.coinValidatorNotificationRecorder.getInvalidCoinDetectedNotifications().size() > 0) {
			throw new CoinRejectedException("Invalid coin detected");
		} else if (this.coinValidatorNotificationRecorder.getValidCoinDetectedNotifications().size() == 0) {
			throw new ControlSoftwareException("System error. Please contact an employee.");
		}
		// otherwise, coin is valid
		// add value to total
		this.paymentManager.addPayment(coinValue);
	}
}
