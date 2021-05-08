package selfcheckout.software.controllers.listeners;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.CardReader;
import selfcheckout.software.controllers.subcontrollers.PSTC;

public class CardReaderListenerRecorderTest {

	private CardReader cardReader;
	private CardReaderListenerRecorder cardReaderListenerRecorder;

	@Before
	public void setUp() {
		this.cardReader = new CardReader();
		this.cardReaderListenerRecorder = new CardReaderListenerRecorder();
	}

	@Test
	public void testClearNotifications() {	
		this.cardReaderListenerRecorder.clearNotifications();
		assertFalse(cardReaderListenerRecorder.getInserted());
		assertFalse(cardReaderListenerRecorder.getSwiped());
		assertFalse(cardReaderListenerRecorder.getRemoved());
		assertFalse(cardReaderListenerRecorder.getTapped());
		assertNull(cardReaderListenerRecorder.getCardData());
	}

	@Test
	public void testCardReaderDefaultState() {
		assertFalse(this.cardReaderListenerRecorder.getInserted());
		assertFalse(this.cardReaderListenerRecorder.getSwiped());
		assertFalse(this.cardReaderListenerRecorder.getRemoved());
		assertFalse(this.cardReaderListenerRecorder.getTapped());
	}

	@Test
	public void testCardInserted() {
		this.cardReaderListenerRecorder.cardInserted(cardReader);
		assertTrue(this.cardReaderListenerRecorder.getInserted());
		assertFalse(this.cardReaderListenerRecorder.getSwiped());
		assertFalse(this.cardReaderListenerRecorder.getTapped());
		assertFalse(this.cardReaderListenerRecorder.getRemoved());
	}

	@Test
	public void testCardSwiped() {
		this.cardReaderListenerRecorder.cardSwiped(cardReader);
		assertFalse(this.cardReaderListenerRecorder.getInserted());
		assertTrue(this.cardReaderListenerRecorder.getSwiped());
		assertFalse(this.cardReaderListenerRecorder.getTapped());
		assertFalse(this.cardReaderListenerRecorder.getRemoved());
	}

	@Test
	public void testCardTapped() {
		this.cardReaderListenerRecorder.cardTapped(cardReader);
		assertFalse(this.cardReaderListenerRecorder.getInserted());
		assertFalse(this.cardReaderListenerRecorder.getSwiped());
		assertTrue(this.cardReaderListenerRecorder.getTapped());
		assertFalse(this.cardReaderListenerRecorder.getRemoved());
	}

	@Test
	public void testCardRemoved() {
		this.cardReaderListenerRecorder.cardRemoved(cardReader);
		assertFalse(this.cardReaderListenerRecorder.getInserted());
		assertFalse(this.cardReaderListenerRecorder.getSwiped());
		assertFalse(this.cardReaderListenerRecorder.getTapped());
		assertTrue(this.cardReaderListenerRecorder.getRemoved());
	}

	@Test
	public void testCardDataRead() {
		this.cardReaderListenerRecorder.cardDataRead(cardReader, PSTC.CREDIT_CARD_DATA);
		assertEquals(this.cardReaderListenerRecorder.getCardData(), PSTC.CREDIT_CARD_DATA);
	}
	
	@Test
	public void testCardDataRead2() {
		this.cardReaderListenerRecorder.cardDataRead(cardReader, PSTC.DEBIT_CARD_DATA);
		assertEquals(this.cardReaderListenerRecorder.getCardData(), PSTC.DEBIT_CARD_DATA);
	}
}
