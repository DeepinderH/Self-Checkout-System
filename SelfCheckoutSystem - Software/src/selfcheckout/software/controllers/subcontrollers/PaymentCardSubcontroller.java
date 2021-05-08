package selfcheckout.software.controllers.subcontrollers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.external.CardIssuer;

import selfcheckout.software.controllers.CardTypeEnum;
import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.*;
import selfcheckout.software.controllers.listeners.CardReaderListenerRecorder;

public class PaymentCardSubcontroller {
	private final PaymentManager paymentManager;
	private final CardReaderListenerRecorder cardListenerRecorder;
	private final CardReader cardReader;
	private final CardIssuer cardIssuer;
	private final PurchaseManager purchaseManager;

	/**
	 * Constructor
	 * @param c
	 * 			CardReader
	 * @param p
	 * 			Payment Manager
	 * @param ci
	 * 			Card issuer
	 * @param pm
	 *          Payment manager
	 */
	public PaymentCardSubcontroller(CardReader c, PaymentManager p, CardIssuer ci, PurchaseManager pm) {
		this.cardReader = c;
		this.paymentManager = p;
		this.cardIssuer = ci;
		this.purchaseManager = pm;
		this.cardListenerRecorder = new CardReaderListenerRecorder();
		cardReader.register(this.cardListenerRecorder);
	}

	/**
	 * Read the card when it is inserted and charge it the full amount due
	 * @param type
	 * 			the type of card
	 * @param number
	 * 			the card number
	 * @param cardholder
	 * 			the number of the card holder
	 * @param cvv
	 * 			the cvv code at the back of the card
	 * @param pin
	 * 			the pin of this card
	 * @param isTapEnabled
	 * 			tells if the card can be used to tap
	 * @param hasChip
	 * 			tells if the card can be inserted or swiped
	 * @throws PaymentIncompleteException
	 * 			thrown if the payment could not be completed
	 * @throws WrongCardInfoException
	 * 			thrown if the information returned from the card was wrong
	 * @throws CannotReadCardException
	 * 			thrown if card cannot be read
	 */
	public void insertCard(
			String type, String number, String cardholder, String cvv,
			String pin, boolean isTapEnabled, boolean hasChip)
				throws PaymentIncompleteException, WrongCardInfoException, CannotReadCardException {
		this.validatePaymentRequired();
		this.cardListenerRecorder.clearNotifications();
		this.validateCardType(type);
		Card card = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);
		cardReaderInsert(card, pin);
		this.validateCardData(type, number, cardholder, cvv);
		this.holdAndPay(number);
	}

	public void removeCard() {
		this.cardReader.remove();
	}

	/**
	 * Checks if the action of inserting the card can occur
	 * @param card
	 * 			the Card
	 * @param pin
	 * 			the pin used to authorize the payment when inserting card
	 * @throws CannotReadCardException
	 * 			thrown if card cannot be read
	 */
	private void cardReaderInsert(Card card, String pin) throws CannotReadCardException {
		try {
			cardReader.insert(card, pin);
		} catch (IOException e) {
			throw new CannotReadCardException("Could not read the card, please try again");
		}
	}

	/**
	 * Read the card when it is swiped and charge it the full amount due
	 * @param type
	 * 			the type of card
	 * @param number
	 * 			the card number
	 * @param cardholder
	 * 			the number of the card holder
	 * @param cvv
	 * 			the cvv code at the back of the card
	 * @param pin
	 * 			the pin of this card
	 * @param isTapEnabled
	 * 			tells if the card can be used to tap
	 * @param hasChip
	 * 			tells if the card can be inserted or swiped
	 * @param image
	 * 			the signature in the back of the card
	 * @throws PaymentIncompleteException
	 * 			thrown if the payment could not be completed
	 * @throws WrongCardInfoException
	 * 			thrown if the information returned from the card was wrong
	 * @throws CannotReadCardException
	 * 			thrown if card cannot be read
	 */
	public void swipeCard(
			String type, String number, String cardholder, String cvv,
			String pin, boolean isTapEnabled, boolean hasChip, BufferedImage image)
				throws PaymentIncompleteException, WrongCardInfoException, CannotReadCardException {
		this.validatePaymentRequired();
		this.cardListenerRecorder.clearNotifications();
		this.validateCardType(type);
		
		Card card = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);
		cardReaderSwipe(card, image);
		this.validateCardData(type, number, cardholder, cvv);
		this.holdAndPay(number);
	}

	/**
	 * Checks if the action of swiping the card can occur
	 * @param card
	 * 			the Card
	 * @param image
	 * 			the signature in the back of the card
	 * @throws CannotReadCardException
	 * 			thrown if card cannot be read
	 */
	private void cardReaderSwipe(Card card, BufferedImage image) throws CannotReadCardException {
		try {
			cardReader.swipe(card, image);
		} catch (IOException e) {
			throw new CannotReadCardException("Could not read the card, please try again");
		}
	}
	/**
	 * Read the card when it is inserted and charge it the full amount due
	 * @param type
	 * 			the type of card
	 * @param number
	 * 			the card number
	 * @param cardholder
	 * 			the number of the card holder
	 * @param cvv
	 * 			the cvv code at the back of the card
	 * @param pin
	 * 			the pin of this card
	 * @param isTapEnabled
	 * 			tells if the card can be used to tap
	 * @param hasChip
	 * 			tells if the card can be inserted or swiped
	 * @throws PaymentIncompleteException
	 * 			thrown if the payment could not be completed
	 * @throws WrongCardInfoException 
	 * 			thrown if the information returned from the card was wrong
	 * @throws CannotReadCardException 
	 * 			thrown if card cannot be read
	 */
	public void tapCard(
			String type, String number, String cardholder, String cvv,
			String pin, boolean isTapEnabled, boolean hasChip)
				throws PaymentIncompleteException, WrongCardInfoException, CannotReadCardException{
		this.validatePaymentRequired();
		this.cardListenerRecorder.clearNotifications();
		this.validateCardType(type);
		Card card = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);
		cardReaderTap(card);
		this.validateCardData(type, number, cardholder, cvv);
		this.holdAndPay(number);
	}
	
	/**
	 * Checks if the action of tapping the card can occur
	 * @param card
	 * 			the Card to tap
	 * @throws CannotReadCardException
	 * 			thrown if card cannot be read
	 */
	private void cardReaderTap(Card card) throws CannotReadCardException {
		try {
			cardReader.tap(card);
		} catch (IOException e) {
			throw new CannotReadCardException("Could not read the card, please try again");
		}
	}
	
	/**
	 * Does the payment on the card given
	 * @param cardNumber
	 * 			gives the card number
	 * @throws PaymentIncompleteException
	 * 			thrown if the payment could not be completed
	 */
	private void holdAndPay(String cardNumber) throws PaymentIncompleteException {
		BigDecimal totalCost = this.purchaseManager.getTotalPrice();
		BigDecimal amountOwed = totalCost.subtract(this.paymentManager.getCurrentPaymentTotal());
		int holdNumber = this.cardIssuer.authorizeHold(cardNumber, amountOwed);
		if(holdNumber != -1) {
			this.cardIssuer.postTransaction(cardNumber, holdNumber, amountOwed);
			this.paymentManager.addPayment(amountOwed);
		} else {
			this.cardIssuer.releaseHold(cardNumber, holdNumber);
			throw new PaymentIncompleteException("Payment was not successful");
		}
	}

	private void validatePaymentRequired() {
		if (this.purchaseManager.getTotalPrice().compareTo(this.paymentManager.getCurrentPaymentTotal()) <= 0) {
			throw new PaymentNotRequiredException("Already have sufficient payment");
		}
	}

	/**
	 * Validates the card type (either it is credit or debit)
	 * @param type
	 * 			the type of the card used in payment
	 * @throws WrongCardInfoException
	 * 			thrown if the card type was not debit or credit
	 */
	private void validateCardType(String type) throws WrongCardInfoException {
		if (!CardTypeEnum.CREDIT.toString().equals(type)
				&& !CardTypeEnum.DEBIT.toString().equals(type)) {
			throw new WrongCardInfoException("type must be debit or credit");
		}
	}
	
	/**
	 * Validates the card data
	 * @param cardType
	 * 			the type of the card used in payment
	 * @param cardNumber
	 * 			the card number
	 * @param cardHolder
	 * 			the number of the card holder
	 * @param cardCVV
	 * 			the cvv code at the back of the card	 
	 * @throws WrongCardInfoException
	 * 			thrown if the card information was not correct
	 * @throws CannotReadCardException
	 * 			thrown if the reader could not read the card data
	 */
	private void validateCardData(
			String cardType, String cardNumber,
			String cardHolder, String cardCVV) throws CannotReadCardException, WrongCardInfoException {
		CardData cardListenerData = this.cardListenerRecorder.getCardData();
		if (cardListenerData == null) {
			throw new CannotReadCardException("Card was not read");
		}
		if (!cardListenerData.getType().equals(cardType) ||
				!cardListenerData.getNumber().equals(cardNumber) ||
				!cardListenerData.getCardholder().equals(cardHolder) ||
				(!this.cardListenerRecorder.getSwiped() &&
				!cardListenerData.getCVV().equals(cardCVV))) {
			throw new WrongCardInfoException("Input card information does not match actual card data");
		}
	}
}
