package selfcheckout.software.controllers.subcontrollers;

import java.io.IOException;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;

import selfcheckout.software.controllers.MembershipAccount;
import selfcheckout.software.controllers.MembershipDatabaseWrapper;
import selfcheckout.software.controllers.exceptions.InvalidCardException;
import selfcheckout.software.controllers.exceptions.NotAMemberException;
import selfcheckout.software.controllers.listeners.CardReaderListenerRecorder;

public class MembershipCardSubcontroller {

	private final CardReader cardReader;
	private final CardReaderListenerRecorder notificationRecorder;
	private final MembershipDatabaseWrapper members;
	private MembershipAccount activeMembershipAccount;  // card currently being used in the transaction

	/**
	 * Constructor
	 * @param reader
	 * 		The device used to read the card
	 */
	public MembershipCardSubcontroller(MembershipDatabaseWrapper members, CardReader reader) {
		this.cardReader = reader;	
		this.notificationRecorder  = new CardReaderListenerRecorder();
		this.cardReader.register(notificationRecorder);
		this.members = members;		
	}

	/**
	 * Read and process a membership card
	 * @param cardNumber
	 * 			number of card inserted by the customer
	 * @throws NotAMemberException
	 * 			thrown if the inserted card does not match one in the database of members
	 * @throws InvalidCardException
	 * 			thrown if the card could not be read correctly
	 */
	public void processCard(String cardNumber) throws NotAMemberException, InvalidCardException {	
		// clear notifications for new card validation
		notificationRecorder.clearNotifications();
		Boolean existingMember = members.alreadyAMember(cardNumber);
		if (!existingMember) {
			// alert the user that they are not a registered member
			throw new NotAMemberException("You are not one of us. Your membership card is not linked to an account.\n");
		} else {
			simulateCardSwipe(cardNumber);			
		}
		// check if most data from most recently swiped card is equal to what is on file
		CardData readData = notificationRecorder.getCardData();
		MembershipAccount linkedAccount = members.getLinkedAccount(cardNumber);
		CardData cardOnFile = linkedAccount.getCardData();
		if (compareCardData(readData, cardOnFile)) {
			this.activeMembershipAccount = linkedAccount;
		} else {
			throw new InvalidCardException("Your card did not match what we have on file.");
		}
	}

	/**
	 * Purely for simulation purposes: to simulate the customer swiping the card
	 * Card signature is null since signatures must be null for membership cards
	 * @param cardNumber
	 * 	the number that was read by the hardware when the card was swiped
	 * @throws InvalidCardException
	 * 	thrown if card could not be read properly by hardware
	 */
	private void simulateCardSwipe(String cardNumber) throws InvalidCardException {
		// retrieve card linked to scanned number and insert that object (purely for simulation purposes,
		// this would essentially be done in reverse if an actual physical card were being inserted)
		Card simulatedMembershipCard = members.getMembershipCard(cardNumber);
		// simulate the card reader receiving the card swipe
		try {
			// note that signature is always null for a membership card,
			// but we must include it as a parameter since we are
			// required to swipe (and not tap or insert) membership cards
			cardReader.swipe(simulatedMembershipCard, null);
		} catch (IOException e) {
			throw new InvalidCardException("Your card could not be read. Try again.");
		}
	}
	
	/**
	 * Validates manually entered membership number (no simulated card swipe)
	 * @param membershipNumber
	 * 	Number entered by the user
	 * @throws NotAMemberException
	 * 	Thrown if no matching account
	 */
	public void processNumber(String membershipNumber) throws NotAMemberException {	
		// clear notifications for new card validation
		notificationRecorder.clearNotifications();
		Boolean existingMember = members.alreadyAMember(membershipNumber);
		if (!existingMember) {
			// alert the user that they are not a registered member
			throw new NotAMemberException("You are not one of us. Your membership card is not linked to an account.\n");
		}
		this.activeMembershipAccount = members.getLinkedAccount(membershipNumber);
	}

	/**
	 * Utility method to compare equality of data retrieved from two swiped cards,
	 * comparing all fields for both (type, number, and card holder name)
	 * Note that since we are swiping cards, CVV data is not supported
	 * @param readData
	 * 	data from card swiped by customer
	 * @param referenceData
	 * 	data on file for customer's card
	 * @return
	 *  whether the two cards have the same cardholder, number, and type
	 */
	public Boolean compareCardData(CardData readData, CardData referenceData) {
		return readData.getCardholder().equals(referenceData.getCardholder())
				&& readData.getNumber().equals(referenceData.getNumber())
				&& readData.getType().equals(referenceData.getType());
	}
	/**
	 * Remove the membership account that is currently active
	 */
	public void removeActiveMembership() {
		this.activeMembershipAccount = null;
	}

	/**
	 * Retrieves the membership card being used for the current transaction
	 * @return
	 * 		the current membership card
	 */
	public MembershipAccount getActiveMembership() {
		return activeMembershipAccount;
	}

}
