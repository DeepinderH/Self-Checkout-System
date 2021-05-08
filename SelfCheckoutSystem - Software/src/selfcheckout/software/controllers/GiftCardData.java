package selfcheckout.software.controllers;

import org.lsmr.selfcheckout.Card;

public class GiftCardData implements Card.CardData{
	private final String number;
	private final String cardholder;

	public GiftCardData(String number, String cardholder) {
		this.number = number;
		this.cardholder = cardholder;
	}

	@Override
	public String getType() {
		return CardTypeEnum.GIFTCARD.toString();
	}

	@Override
	public String getNumber() {
		return this.number;
	}

	@Override
	public String getCardholder() {
		return this.cardholder;
	}

	@Override
	public String getCVV() {
		return null;
	}
}
