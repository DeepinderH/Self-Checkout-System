package selfcheckout.software.controllers;

import org.lsmr.selfcheckout.Card;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class MembershipCardData implements Card.CardData {
	private final String number;
	private final String cardholder;

	public MembershipCardData(String number, String cardholder) {
		this.number = number;
		this.cardholder = cardholder;
	}

	@Override
	public String getType() {
		return "Membership Card";
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
		// swiped cards have no CVV anyway
		return null;
	}
}
