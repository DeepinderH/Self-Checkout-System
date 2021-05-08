package selfcheckout.software.controllers;

public enum CardTypeEnum {
	MEMBERSHIP("membership"),
	CREDIT("credit"),
	DEBIT("debit"),
	GIFTCARD("gift card");

	private final String cardType;

	private CardTypeEnum(String cardType) {
		this.cardType = cardType;
	}

	@Override
	public String toString() {
		return cardType;
	}
}
