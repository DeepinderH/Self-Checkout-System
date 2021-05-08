package selfcheckout.software.controllers.subcontrollers;

import java.math.BigDecimal;
import java.util.List;

import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

public class CoinStorageEmptyingSubcontroller {
	
	private final SelfCheckoutStation station;
	
	public CoinStorageEmptyingSubcontroller(SelfCheckoutStation station) {
		this.station = station;
	}

	/**
	 *  Set its coin storage unit to empty
	 */
	public BigDecimal emptyCoinStorage() {
		BigDecimal totalValue = new BigDecimal("0.00");
		for (CoinDispenser coinDispenser: station.coinDispensers.values()) {
			List<Coin> coinsRemoved = coinDispenser.unload();
			for (Coin coin : coinsRemoved) {
				if (coin != null) {
					totalValue = totalValue.add(coin.getValue());
				}
			}
		}
		return totalValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	}
}
