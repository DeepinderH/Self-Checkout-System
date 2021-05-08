package selfcheckout.software.controllers;

import java.util.HashMap;

import org.lsmr.selfcheckout.Card;

public class MembershipDatabaseWrapper {

	/**
	 * Constructor
	 */
	public MembershipDatabaseWrapper() {
	}

	/**
	 * Populates the database of existing card-bearing members
	 */
	public static void initializeMembershipDatabase() {
		Card card1 = new Card("Membership Card", "1234567890", "Mr. Name", "932", "0", true, true);
		Card.CardData card1Data = new MembershipCardData("1234567890", "Mr. Name");
		MembershipAccount member1 = new MembershipAccount(card1, card1Data);
		MembersDatabase.MEMBERSHIP_DATABASE.put("1234567890", member1);		 
	}

	/**
	 * Reports whether or not a card matching a given card exists in the database
	 * @param insertedCardNumber
	 * 		the number of the membership card to be verified
	 * @return
	 * 		true if the card matches one in the database, false otherwise
	 */
	public Boolean alreadyAMember(String insertedCardNumber) {
		return MembersDatabase.MEMBERSHIP_DATABASE.containsKey(insertedCardNumber);	
	}

	/**
	 * Gets the name associated with the membership account, useful for personalizing the UI
	 * @param insertedCardNumber
	 * 		the number of the membership card inserted by the customer
	 * @return
	 * 		the name associated with the account
	 */
	public String getCardHolderName(String insertedCardNumber) {
		// retrieve the data associated with that number
		MembershipAccount matchingAccount = MembersDatabase.MEMBERSHIP_DATABASE.get(insertedCardNumber);
		return matchingAccount.getCardholder();
	}

	/**
	 * Returns the membership account linked to a given membership card number
	 * @param cardNumber
	 * 		the number of the membership card inserted by the customer
	 * @return
	 *      the MembershipAccount object for the member
	 */
	public MembershipAccount getLinkedAccount(String cardNumber) {
		return MembersDatabase.MEMBERSHIP_DATABASE.get(cardNumber);
	}

	/**
	 * Retrieves the membership card object linked to a member's account
	 * @param cardNumber
	 * 	membership card number
	 * @return
	 * 	the card object on file for the member
	 */
	public Card getMembershipCard(String cardNumber) {
		MembershipAccount matchingAccount = MembersDatabase.MEMBERSHIP_DATABASE.get(cardNumber);
		return matchingAccount.getCardOnFile();		
	}

	/**
	 * Removes the current database
	 */
	public static void clearMembershipDatabase() {
		MembersDatabase.MEMBERSHIP_DATABASE = new HashMap<>();
	}

}
