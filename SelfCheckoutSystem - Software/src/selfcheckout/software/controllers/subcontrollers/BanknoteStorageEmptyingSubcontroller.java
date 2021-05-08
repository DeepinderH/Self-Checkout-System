package selfcheckout.software.controllers.subcontrollers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class BanknoteStorageEmptyingSubcontroller {
	
	private final SelfCheckoutStation station;
	
	public BanknoteStorageEmptyingSubcontroller(SelfCheckoutStation station) {
		this.station = station;
	}

	/**
	 * Set the banknote storage unit and all dispensers to empty
	 */
	public BigDecimal emptyBanknoteStorage() {
		List<Banknote> banknotesRemoved = this.station.banknoteStorage.unload();
		int totalValue = 0;
		for (Banknote banknote : banknotesRemoved) {
			if (banknote != null) {
				totalValue += banknote.getValue();
			}
		}
		Collection<BanknoteDispenser> allDispensers = this.station.banknoteDispensers.values();
		for (BanknoteDispenser dispenser: allDispensers) {
			List<Banknote> dispenserBanknotes = dispenser.unload();
			for (Banknote banknote : dispenserBanknotes) {
				if (banknote != null) {
					totalValue += banknote.getValue();
				}
			}
		}

		return new BigDecimal(totalValue).setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
}
