package selfcheckout.software.controllers;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;

import java.math.BigDecimal;
import java.util.Currency;

public final class ControllerTestConstants {
	public static final Currency CURRENCY = Currency.getInstance("CAD");
	public static final int[] BANKNOTE_DENOMINATIONS = new int[]{5, 10, 20, 50, 100};
	public static final int INVALID_BANKNOTE_DENOMINATION = 123;
	public static final BigDecimal[] COIN_DENOMINATIONS = new BigDecimal[]{
		new BigDecimal("0.05"),
		new BigDecimal("0.10"),
		new BigDecimal("0.25"),
		new BigDecimal("1.00"),
		new BigDecimal("2.00")
	};
	public static final BigDecimal INVALID_COIN_DENOMINATION = new BigDecimal("4.25");
	public static final int SCALE_MAXIMUM_WEIGHT = 10;
	public static final int SCALE_SENSITIVITY = 1;
	public static final String VALID_BARCODE_STRING = "23578";
	public static final Barcode VALID_BARCODE = new Barcode(VALID_BARCODE_STRING);
	public static final Item VALID_BARCODED_ITEM = new BarcodedItem(VALID_BARCODE, 1.0);
	public static final String CARD_ISSUER_NAME = "Credit and Debit Card Issuer";
	public static final String VALID_MEMBERSHIP_NUMBER = "1234567890";
	public static final String VALID_CARDHOLDER_NAME = "Mr. Name";
	public static final int VALID_ATTENDANT_ID = 123456;
	public static final String VALID_ATTENDANT_PASSWORD = "Password";
	public static final int INVALID_ATTENDANT_ID = 654321;
	public static final String INVALID_ATTENDANT_ID_PASSWORD = "InvalidPassword";
	public static final String VALID_PLUCODE_STRING = "4131";
	public static final PriceLookupCode VALID_PLUCODE = new PriceLookupCode(VALID_PLUCODE_STRING);
	public static final Item VALID_PLUCODED_ITEM = new PLUCodedItem(VALID_PLUCODE, 1.0);
	public static final int MESSAGEBOX_SHOW_TIMEOUT = 200;
}
