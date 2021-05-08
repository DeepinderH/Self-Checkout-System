package selfcheckout.software.controllers;

import java.math.BigDecimal;
import java.util.HashMap;

import org.lsmr.selfcheckout.Card;

import selfcheckout.software.controllers.exceptions.InvalidCardException;

public class GiftCardDatabaseWrapper {

	public GiftCardDatabaseWrapper() {}
	
	/**
	 * Populates the database of existing gift-cards
	 */
	public static void initializeGiftCardDatabase() {
		final String cardNumber = "1234567890";
		Card giftCard = new Card(CardTypeEnum.GIFTCARD.toString(), cardNumber, "Andrew Smith", null, null, false, false);
		Card.CardData giftCardData = new GiftCardData(cardNumber, "Andrew Smith");
		GiftCardAccount giftcardAccount = new GiftCardAccount(new BigDecimal("50.00"), giftCard, giftCardData);
		GiftCardDatabase.GIFT_CARD_DATABASE.put(cardNumber, giftcardAccount);
	}
	
	/**
	 *  Adds new gift card to the database
	 *  @param balance
	 *  						the balance of the gift card
	 *  @param card
	 *  						card information of gift card
	 *  @param cardHolder
	 *  						name of the cardHolder
	 *  @param cardNumber
	 *  						card Number of the card
	 * @throws InvalidCardException 
	 * 							thrown if the card is already in the database
	 */
	public void addGiftCardToDatabase(BigDecimal balance, Card card, String cardHolder, String cardNumber) throws InvalidCardException {
		if (giftCardIssued(cardNumber)) {
		    throw new InvalidCardException("Card already exists");
		}
		GiftCardData giftCardData = new GiftCardData(cardNumber, cardHolder);
		GiftCardAccount giftCardAccount = new GiftCardAccount(balance, card, giftCardData);
		GiftCardDatabase.GIFT_CARD_DATABASE.put(cardNumber, giftCardAccount);
	}

	/*
	 * Checks if the gift Card is in the database
	 * 
	 * @param cardNumber
	 * 							card number of the gift card
	 * 
	 * @returns 
	 * 							true if the card is in database, false otherwise
	 * 											
	 */
	public Boolean giftCardIssued(String cardNumber) {
		return GiftCardDatabase.GIFT_CARD_DATABASE.containsKey(cardNumber);
	}

	/*
	 * Get the balance of the gift card
	 * 
	 * @param cardNumber
	 * 							card number of the gift card
	 * 
	 * @returns 
	 * 							the balance of the first card
	 * 											
	 */
	public BigDecimal getCardBalance(String cardNumber) {
		GiftCardAccount giftCardAccount = GiftCardDatabase.GIFT_CARD_DATABASE.get(cardNumber);
		return giftCardAccount.getBalance();
	}

	/*
	 * Get the gift card's holder's name
	 * 
	 * @param cardNumber
	 * 							card number of the gift card
	 * 
	 * @returns 
	 * 							the name of the owner of the gift card with Card number "cardNumber"
	 * 											
	 */
	public String getCardHolderName(String cardNumber) {
		GiftCardAccount giftCardAccount = GiftCardDatabase.GIFT_CARD_DATABASE.get(cardNumber);
		return giftCardAccount.getCardholder();
	}

	/*
	 * Get the account of the holder of this gift card
	 * 
	 * @param cardNumber
	 * 							card number of the gift card
	 * 
	 * @returns 
	 * 							the account of the holder who had gift card with cardNumber "cardNumber"
	 * 											
	 */
	public GiftCardAccount getAccount(String cardNumber) {
		return GiftCardDatabase.GIFT_CARD_DATABASE.get(cardNumber);
	}

	/*
	 * Checks if the gift Card is in the database
	 * 
	 * @param cardNumber
	 * 							card number of the gift card
	 * 
	 * @returns 
	 * 							information of the gift card
	 * 											
	 */
	public static Card getGiftCard(String cardNumber) {
		GiftCardAccount giftCardAccount = GiftCardDatabase.GIFT_CARD_DATABASE.get(cardNumber);
		return giftCardAccount.getCardOnFile();		
	}

	/*
	 * Clears gift card database
	 * 				
	 */
	public static void clearGiftCardDatabase() {
		GiftCardDatabase.GIFT_CARD_DATABASE = new HashMap<>();
	}

}
