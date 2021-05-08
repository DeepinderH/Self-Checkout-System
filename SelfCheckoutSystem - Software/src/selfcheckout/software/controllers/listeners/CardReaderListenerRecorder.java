package selfcheckout.software.controllers.listeners;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.listeners.CardReaderListener;

public class CardReaderListenerRecorder extends NotificationRecorder implements CardReaderListener {

	private boolean inserted = false;
	private boolean swiped = false;
	private boolean tapped = false;
	private boolean removed = false;
	private CardData cardData;

	public CardReaderListenerRecorder() {
		super();
		this.inserted = false;
		this.swiped = false;
		this.tapped = false;
		this.removed = false;
	}

	@Override
	public void cardInserted(CardReader reader) {
		this.inserted = true;
		this.swiped = false;
		this.tapped = false;
		this.removed = false;
	}

	@Override
	public void cardSwiped(CardReader reader) {
		this.inserted = false;
		this.swiped = true;
		this.tapped = false;
		this.removed = false;
	}

	@Override
	public void cardTapped(CardReader reader) {
		this.inserted = false;
		this.swiped = false;
		this.tapped = true;
		this.removed = false;
	}

	@Override
	public void cardRemoved(CardReader reader) {
		this.inserted = false;
		this.swiped = false;
		this.tapped = false;
		this.removed = true;
	}

	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		this.cardData = data;
	}

	@Override
	public void clearNotifications() {
		super.clearNotifications();
		this.inserted = false;
		this.swiped = false;
		this.tapped = false;
		this.removed = false;
		this.cardData = null;
	}

	public boolean getInserted() {
		return inserted;
	}

	public boolean getSwiped() {
		return swiped;
	}

	public boolean getTapped() {
		return tapped;
	}

	public boolean getRemoved() {
		return removed;
	}

	public CardData getCardData() {
		return cardData;
	}
}
