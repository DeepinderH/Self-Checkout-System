package selfcheckout.software.controllers.subcontrollers;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Item;

import selfcheckout.software.controllers.CardTypeEnum;
import selfcheckout.software.controllers.GiftCardAccount;
import selfcheckout.software.controllers.GiftCardData;

public final class PSTC {
	// This class is PaymentCardSubcontrollerTestConstants renamed as PSTC
	
	public static final String ISSUER_NAME = "MasterCard";
	public static final String CTYPE = CardTypeEnum.CREDIT.toString();
	public static final String DTYPE = CardTypeEnum.DEBIT.toString();
	public static final String GTYPE = CardTypeEnum.GIFTCARD.toString();

	public static final String RTYPE = "Rock and Roll";
	public static final String CARDNUMBER = "123456789";
	public static final String CARDNUMBER2 = "123456798";
	public static final String CARDNUMBER3 = "123456000";
	public static final String WRONGCARDNUMBER = "123456888";
	public static final String CARDHOLDER = "John Smith";
	public static final String WRONGCARDHOLDER = "Dave Smith";
	public static final String CARDPIN = "1111";
	public static final String WRONGCARDPIN = "8888";
	public static final String CARDCVV = "999";
	public static final String WRONGCARDCVV = "998";

	public static final String SWIPEGIFTCARDHOLDERNAME = "Dave Dacota";
	public static final String SWIPEGIFTCARDHOLDERNAME2 = "Henry Smith";
	public static final String WRONGSWIPEGIFTCARDHOLDERNAME = "Dave Junior Dacota";

	public static final String SWIPEGIFTCARDNUMBER = "777777777";
	public static final String SWIPEGIFTCARDNUMBER2 = "888888888";
	public static final String WRONGSWIPEGIFTCARDNUMBER = "877777777";

	
	public static final String GIFTCARDPIN = null;
	public static final String GIFTCARDCVV = null;	

	public static final Card.CardData CREDIT_CARD_DATA = new Card.CardData() {
		@Override
		public String getType() {
			return CTYPE;
		}

		@Override
		public String getNumber() {
			return CARDNUMBER;
		}

		@Override
		public String getCardholder() {
			return CARDHOLDER;
		}

		@Override
		public String getCVV() {
			return CARDCVV;
		}
	};

	public static final Card.CardData DEBIT_CARD_DATA = new Card.CardData() {
		@Override
		public String getType() {
			return DTYPE;
		}

		@Override
		public String getNumber() {
			return CARDNUMBER2;
		}

		@Override
		public String getCardholder() {
			return CARDHOLDER;
		}

		@Override
		public String getCVV() {
			return CARDCVV;
		}
	};

	public static final Card.CardData BAD_CARD_DATA = new Card.CardData() {
		@Override
		public String getType() {
			return "UNKNOWN";
		}

		@Override
		public String getNumber() {
			return "839897236482734623";
		}

		@Override
		public String getCardholder() {
			return "UNKNOWN";
		}

		@Override
		public String getCVV() {
			return "892498723648237";
		}
	};

	public static final Calendar CALENDAR = new GregorianCalendar(2025,1,15);
	
	public static final BufferedImage SIGNATURE = new BufferedImage(600,100,BufferedImage.TYPE_INT_RGB);
	public static final BufferedImage GIFTCARDSIGNATURE = null;
	
	public static final boolean NOTAP = false;
	public static final boolean TAP = true;
	public static final boolean NOCHIP = false;
	public static final boolean CHIP = true;
	
	public static final Card gSwipeCard = new Card(GTYPE, SWIPEGIFTCARDNUMBER, SWIPEGIFTCARDHOLDERNAME, GIFTCARDCVV, GIFTCARDPIN, NOTAP, NOCHIP);
	public static final Card gSwipeCard2 = new Card(GTYPE, SWIPEGIFTCARDNUMBER2, SWIPEGIFTCARDHOLDERNAME2, GIFTCARDCVV, GIFTCARDPIN, NOTAP, NOCHIP);
	
	public static final Card dTapCard = new Card(DTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, TAP, NOCHIP);
	public static final Card cTapCard = new Card(CTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, TAP, NOCHIP);
	public static final Card dNoTapCard = new Card(DTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, NOTAP, NOCHIP);
	public static final Card cNoTapCard = new Card(CTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, NOTAP, NOCHIP);
	public static final Card dSwipeCard = new Card(DTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, NOTAP, CHIP);
	public static final Card cSwipeCard = new Card(CTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, NOTAP, CHIP);
	public static final Card dNoSwipeCard = new Card(DTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, NOTAP, NOCHIP);
	public static final Card cNoSwipeCard = new Card(CTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, NOTAP, NOCHIP);
	public static final Card card = new Card(CTYPE, CARDNUMBER, CARDHOLDER, CARDCVV, CARDPIN, TAP, CHIP);
	public static final Card wrongCard = new Card(RTYPE, WRONGCARDNUMBER, WRONGCARDHOLDER, WRONGCARDCVV, CARDPIN, TAP, CHIP);

	public static final BigDecimal FULL_PAYMENT = new BigDecimal("20");
	public static final BigDecimal CREDITAMOUNT = new BigDecimal("2000");
	public static final BigDecimal DEBITBALANCE = new BigDecimal("1000");
	public static final BigDecimal OVERBALANCE = new BigDecimal("2500");
	public static final BigDecimal UNDERBALANCE = new BigDecimal("500");
	public static final BigDecimal GIFTCARDBALANCE = new BigDecimal("20");
	public static final BigDecimal GIFTCARDBALANCE2 = new BigDecimal("30");

	public static final GiftCardData giftCardData = new GiftCardData(SWIPEGIFTCARDNUMBER, SWIPEGIFTCARDHOLDERNAME);
	public static final GiftCardData giftCardData2 = new GiftCardData(SWIPEGIFTCARDNUMBER2, SWIPEGIFTCARDHOLDERNAME2);

	public static final GiftCardAccount giftcardAccount = new GiftCardAccount(GIFTCARDBALANCE, card, giftCardData);
	
	public static final GiftCardData giftWrongCardData = new GiftCardData(WRONGSWIPEGIFTCARDNUMBER, WRONGSWIPEGIFTCARDHOLDERNAME);

	public static final String INVALID_BARCODE_STRING = "29861259";
	public static final Barcode INVALID_BARCODE = new Barcode(INVALID_BARCODE_STRING);
	public static final Item INVALID_ITEM = new BarcodedItem(INVALID_BARCODE, 1.0);

}
