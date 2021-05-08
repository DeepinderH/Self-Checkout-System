package selfcheckout.software.controllers;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;

import selfcheckout.software.controllers.exceptions.PaymentIncompleteException;

public class GiftCardAccount {
	private BigDecimal balance;						
	private final Card giftCardOnFile;
	private final CardData cardData;

	public GiftCardAccount(BigDecimal balance, Card card, CardData cardData) {
		this.balance = balance; 					
		this.giftCardOnFile = card;
		this.cardData = cardData;
	}

	public String getNumber() {
		return this.cardData.getNumber();
	}

	public String getCardholder() {
		return this.cardData.getCardholder();
	}

	public BigDecimal getBalance() {
		return this.balance;
	}

	/**
	 * Pays the full gift card value or the amountOwed, whichever is less
	 *
	 * @param amountOwed the amount of money owed
	 * @return The total amount deducted from the gift card balance
	 */
	public BigDecimal payUntilNoBalance(BigDecimal amountOwed) {
		if (amountOwed.compareTo(this.balance) <= 0) {
			// balance is greater to or equal to amountOwed
			this.balance = this.balance.subtract(amountOwed);
			return amountOwed;
		}
		// balance less than amountOwed
		BigDecimal amountPaid = this.balance;
		this.balance = new BigDecimal("0.00");
		return amountPaid;
	}
	
	public Card getCardOnFile() {
		return giftCardOnFile;
	}
	
	public CardData getCardData() {
		return cardData;
	}
}
