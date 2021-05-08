package selfcheckout.software.controllers;

import org.lsmr.selfcheckout.Card;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.Card.CardSwipeData;

/**
 * More accessible class for retrieving data associated with members
 *
 */
public class MembershipAccount {

	private int points;						// (if this is a points system)
	private final Card cardOnFile;
	private final CardData cardData;

	public MembershipAccount(Card card, CardData cardData) {
		this.points = 0; 					// when new account is created, points at 0
		this.cardOnFile = card;
		this.cardData = cardData;
	}

	public String getNumber() {
		return this.cardData.getNumber();
	}

	public String getCardholder() {
		return this.cardData.getCardholder();
	}

	
	public int getPoints() {
		return points;
	}
	
	public void addPoints(int newPoints) {
		points += newPoints;
	}
	
	public Card getCardOnFile() {
		return cardOnFile;
	}
	
	public CardData getCardData() {
		return cardData;
	}
}
