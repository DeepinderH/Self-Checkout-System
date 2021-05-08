package selfcheckout.software.controllers.subcontrollers;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.*;
import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.exceptions.BanknoteRejectedException;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.NoDanglingBanknoteException;
import selfcheckout.software.controllers.exceptions.StorageUnitFullException;
import selfcheckout.software.controllers.listeners.BanknoteSlotNotificationRecorder;
import selfcheckout.software.controllers.listeners.BanknoteValidatorNotificationRecorder;

import java.math.BigDecimal;
import java.util.Currency;

public class BanknoteInsertionSubcontroller {

	private final BanknoteSlot banknoteSlot;
	private final PaymentManager paymentManager;

	private final BanknoteSlotNotificationRecorder banknoteSlotNotificationRecorder;
	private final BanknoteValidatorNotificationRecorder banknoteValidatorNotificationRecorder;

	public BanknoteInsertionSubcontroller(
			BanknoteSlot banknoteSlot, BanknoteValidator banknoteValidator,
			PaymentManager paymentManager) {
		this.banknoteSlot = banknoteSlot;
		this.paymentManager = paymentManager;

		this.banknoteSlotNotificationRecorder = new BanknoteSlotNotificationRecorder();
		this.banknoteSlot.register(this.banknoteSlotNotificationRecorder);

		this.banknoteValidatorNotificationRecorder = new BanknoteValidatorNotificationRecorder();
		banknoteValidator.register(this.banknoteValidatorNotificationRecorder);
	}

	public void insertBanknote(int banknoteValue, Currency banknoteCurrency) throws BanknoteRejectedException, StorageUnitFullException {
		Banknote banknote;
		try {
			banknote = new Banknote(banknoteValue, banknoteCurrency);
		} catch (SimulationException e) {
			throw new IllegalArgumentException("insertBanknote parameters are invalid");
		}
		this.banknoteSlotNotificationRecorder.clearNotifications();
		this.banknoteValidatorNotificationRecorder.clearNotifications();
		try {
			this.banknoteSlot.accept(banknote);
		} catch (DisabledException e) {
			throw new BanknoteRejectedException(
				"Banknote acceptor is disabled. Please try a different payment method");
		} catch (OverloadException e) {
			throw new BanknoteRejectedException(e.getLocalizedMessage());
		}

		if (this.banknoteSlotNotificationRecorder.getBanknoteEjectedNotifications().size() > 0) {
			if (this.banknoteValidatorNotificationRecorder.getInvalidBanknoteDetectedNotifications().size() > 0) {
				throw new BanknoteRejectedException("Invalid banknote detected");
			}
			if (this.banknoteValidatorNotificationRecorder.getValidBanknoteDetectedNotifications().size() >0) {
				throw new StorageUnitFullException(
					"Banknote storage unit is full. Please try a different payment method");
			}
			throw new ControlSoftwareException(
				"System error. Banknote has been ejected. Please contact an employee");
		} else if (this.banknoteValidatorNotificationRecorder.getValidBanknoteDetectedNotifications().size() == 0) {
			throw new ControlSoftwareException("System error. Please contact an employee.");
		}
		// otherwise successful insertion of a banknote
		// add value to total
		this.paymentManager.addPayment(new BigDecimal(banknoteValue));
	}

	public void removeDanglingBanknote() {
		this.banknoteSlotNotificationRecorder.clearNotifications();
		this.banknoteSlot.removeDanglingBanknote();
		if (this.banknoteSlotNotificationRecorder.getBanknoteRemovedNotifications().size() == 0) {
			throw new NoDanglingBanknoteException("There is no dangling banknote to be removed");
		}
	}
}
