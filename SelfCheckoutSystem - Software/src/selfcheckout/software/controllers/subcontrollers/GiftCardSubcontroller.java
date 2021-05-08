package selfcheckout.software.controllers.subcontrollers;

import java.io.IOException;
import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;

import selfcheckout.software.controllers.GiftCardAccount;
import selfcheckout.software.controllers.GiftCardDatabaseWrapper;
import selfcheckout.software.controllers.PaymentManager;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.InvalidCardException;
import selfcheckout.software.controllers.listeners.CardReaderListenerRecorder;

public class GiftCardSubcontroller {
	private final CardReader cardReader;
	private final CardReaderListenerRecorder cardReaderListenerRecorder;
	private final GiftCardDatabaseWrapper giftCardDatabaseWrapper;
	private final PaymentManager paymentManager;
	private final PurchaseManager purchaseManager;

	/*
	 * constructor
	 * 
	 * @param giftCardDatabaseWrapper
	 * 											wrapper with the information on gift cards
	 * @param reader
	 * 											CardReader that reads the card
	 * @param paymentManager
	 * 											paymentManager where the payment information is kept	
	 */
	public GiftCardSubcontroller(
			GiftCardDatabaseWrapper giftCardDatabaseWrapper, CardReader reader,
			PaymentManager paymentManager, PurchaseManager purchaseManager) {
		this.cardReader = reader;	
		this.giftCardDatabaseWrapper = giftCardDatabaseWrapper;	
		this.paymentManager = paymentManager;
		this.purchaseManager = purchaseManager;
		this.cardReaderListenerRecorder  = new CardReaderListenerRecorder();
		this.cardReader.register(cardReaderListenerRecorder);
	}

	/**
	 * Processes the card and completes the payments appropriately if the customer wants to pay with gift card with balance less than payment
	 *
	 * @throws InvalidCardException thrown when the Card information is incorrect
	 *
	 * @return the amount paid
	 */
	public BigDecimal processUntilNoBalanceWithCard(String giftCardNumber) throws InvalidCardException {
		this.swipeGiftCard(giftCardNumber);
		// swipe successful
		GiftCardAccount giftCardAccount = this.giftCardDatabaseWrapper.getAccount(giftCardNumber);
		BigDecimal amountOwed = purchaseManager.getTotalPrice().subtract(
									paymentManager.getCurrentPaymentTotal());
		BigDecimal amountPaid = giftCardAccount.payUntilNoBalance(amountOwed);
		this.paymentManager.addPayment(amountPaid);
		return amountPaid;
	}

	private void swipeGiftCard(String giftCardNumber) throws InvalidCardException {
		cardReaderListenerRecorder.clearNotifications();
		GiftCardAccount giftCardAccount;
		Card giftCard;
		try {
			giftCardAccount = this.giftCardDatabaseWrapper.getAccount(giftCardNumber);
			giftCard = giftCardAccount.getCardOnFile();
		} catch (NullPointerException e) {
			throw new InvalidCardException("We have no record of that gift card. Try again");
		}
		try {
			this.cardReader.swipe(giftCard, null);
		} catch (IOException e) {
			throw new InvalidCardException(e.getLocalizedMessage());
		}
		CardData actualCardData = this.cardReaderListenerRecorder.getCardData();
		CardData giftCardInDatabase = giftCardAccount.getCardData();
		if (!compareCardData(giftCardInDatabase, actualCardData)) {
			throw new InvalidCardException("Your card did not match what we have on file.");
		}
	}

	/**
	 * Checks if the information of the card that was read is the same as the information in the database
	 * @return true if the card data of the two cards is equal, false otherwise
	 */
	private Boolean compareCardData(CardData databaseGiftCardData, CardData readGiftCardData) {
		return (databaseGiftCardData.getCardholder().equals(readGiftCardData.getCardholder())
				&& databaseGiftCardData.getNumber().equals(readGiftCardData.getNumber())
				&& databaseGiftCardData.getType().equals(readGiftCardData.getType()));
	}
	
	public BigDecimal getCardBalance(String cardNumber) {
		return giftCardDatabaseWrapper.getCardBalance(cardNumber);
	}
}
