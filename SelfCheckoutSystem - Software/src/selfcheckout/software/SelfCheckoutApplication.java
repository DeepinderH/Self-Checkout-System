package selfcheckout.software;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.CoinDispenser;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import selfcheckout.software.controllers.*;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.views.SelfCheckoutView;
import selfcheckout.software.views.cliview.CLIView;
import selfcheckout.software.views.gui.GUIManager;

import javax.swing.JFrame;

/*
 * This project was made for SENG 300 in Winter 2021
 * This software is designed using a blended version of the
 * model-view-controller (MVC) design pattern originally invented by
 * Trygve Reenskaug and outlined in the book
 * "Working with Objects: The OOram Software Engineering Method"
 * and the Entity-Controller-Boundary (ECB) design pattern presented
 * in SENG 300 lecture.
 *
 * Our software structure is quite simple. We have three main parts:
 * - The hardware simulation (org.lsmr.selfcheckout package)
 * - The controller (selfcheckout.software.controllers package)
 * - The user interface (selfcheckout.software.views package)
 *
 * The hardware simulation is somewhat like the model in MVC
 * or entity in ECB
 * Note that:
 * - The hardware simulation does not interact with the user interface,
 * only the controller
 * - The hardware simulation has a smaller scope than a "typical" MVC model
 *     - note that our hardware simulation does not interact with the view,
 *       which is different than the "standard" MVC model
 * - The hardware simulation has a much larger scope than a "standard" ECB entity
 *
 * The controller is somewhat like the controller in MVC or
 * controls in ECB
 * - The controller receives method calls from the view and hides the
 * complexity of the hardware simulation from the view
 * - Our controller has a larger scope than a "standard" MVC controller
 * - Our controller has a smaller scope than "standard" ECB controls
 *
 * The view is somewhat like the view in MVC or boundary in ECB
 * - The view communications only with the controller
 * - Our view about the same scope as a "standard" MVC view
 *     - note that our view does not interact with the hardware simulation,
 *       which is different than the "standard" MVC view
 * - Our view has a about the same scope as a "standard" ECB boundary
 */

class SelfCheckoutApplication {

	private static final Currency CURRENCY = Currency.getInstance("CAD");
	private static final int[] BANKNOTE_DENOMINATIONS = new int[]{5, 10, 20, 50, 100};
	private static final BigDecimal[] COIN_DENOMINATIONS = new BigDecimal[]{
		new BigDecimal("0.05"),
		new BigDecimal("0.10"),
		new BigDecimal("0.25"),
		new BigDecimal("1.00"),
		new BigDecimal("2.00")
	};
	private static final int SCALE_MAXIMUM_WEIGHT= 10;
	private static final int SCALE_SENSITIVITY = 1;

	// Fills the change dispensers of a self-checkout station
	private static void fillChangeDispensers(SelfCheckoutStation scs) {

		// For each denomination of coin, load it to the max with coins of that
		// denomination
		for (final BigDecimal denomination : scs.coinDenominations) {
			final CoinDispenser dispenser = scs.coinDispensers.get(denomination);
			int numCoinsToAdd = Math.max(dispenser.getCapacity() - 10, 0);
			final Coin[] coins = new Coin[numCoinsToAdd];

			for (int i = 0; i < coins.length; i++) {
				coins[i] = new Coin(denomination, CURRENCY);
			}

			try {
				dispenser.load(coins);
			} catch (final SimulationException | OverloadException e) {
				// This should not be possible
				System.out.println("Could not fill coin dispenser");
				e.printStackTrace();
				System.exit(1);
			}
		}

		// And for each denomination of bills, fill it to the max with bills of
		// that denomination
		for (final int denomination : scs.banknoteDenominations) {
			final BanknoteDispenser dispenser = scs.banknoteDispensers.get(denomination);
			int numBanknotesToAdd = Math.max(dispenser.getCapacity() - 10, 0);
			final Banknote[] banknotes = new Banknote[numBanknotesToAdd];

			for (int i = 0; i < banknotes.length; i++) {
				banknotes[i] = new Banknote(denomination, CURRENCY);
			}

			try {
				dispenser.load(banknotes);
			} catch (final SimulationException | OverloadException e) {
				// This should not be possible
				System.out.println("Could not fill banknote dispenser");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public static void addCreditCard(CardIssuer cardIssuer) {
		Calendar futureExpiryDate = GregorianCalendar.getInstance();
		futureExpiryDate.add(Calendar.YEAR, 1);

		cardIssuer.addCardData(
			"123456789",
			"John Smith",
			futureExpiryDate,
			"999",
			new BigDecimal("2000"));
	}

	public static void addDebitCard(CardIssuer cardIssuer) {
		Calendar futureExpiryDate = GregorianCalendar.getInstance();
		futureExpiryDate.add(Calendar.YEAR, 1);

		cardIssuer.addCardData(
			"987654321",
			"John Smith",
			futureExpiryDate,
			"111",
			new BigDecimal("2000"));
	}

	public static void main(String[] args) {
		SelfCheckoutStation scs = new SelfCheckoutStation(
			SelfCheckoutApplication.CURRENCY,
			SelfCheckoutApplication.BANKNOTE_DENOMINATIONS,
			SelfCheckoutApplication.COIN_DENOMINATIONS,
			SelfCheckoutApplication.SCALE_MAXIMUM_WEIGHT,
			SelfCheckoutApplication.SCALE_SENSITIVITY
		);

		// Fill the change dispensers of the machine because they don't
		// start filled, and it'll throw an exception when dispensing change
		// if they're empty
		fillChangeDispensers(scs);

		// initialize the database(s)
		ProductDatabasesWrapper.initializeDatabases();
		MembershipDatabaseWrapper.initializeMembershipDatabase();
		GiftCardDatabaseWrapper.initializeGiftCardDatabase();
		// create an interface for the controller to interact with the database
		ProductDatabasesWrapper databaseWrapper = new ProductDatabasesWrapper();
		MembershipDatabaseWrapper members = new MembershipDatabaseWrapper();
		GiftCardDatabaseWrapper giftCardDatabaseWrapper = new GiftCardDatabaseWrapper();
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		CardIssuer cardIssuer = new CardIssuer("Credit and Debit Card Issuer");
		addCreditCard(cardIssuer);
		addDebitCard(cardIssuer);
		SelfCheckoutController controller = new SelfCheckoutController(scs, databaseWrapper, cardIssuer, members,
				giftCardDatabaseWrapper, attendantDatabaseWrapper);
		scs.printer.addPaper(15);
		scs.printer.addInk(500);

		SelfCheckoutView view;
		if (args.length == 0 || args[0].equals("--gui")) {
			JFrame frame = scs.screen.getFrame();
			scs.screen.setVisible(true);
			view = new GUIManager(controller, frame);
		} else if (args[0].equals("--nogui")) {
			view = new CLIView(controller, System.in, System.out);
		} else {
			throw new IllegalArgumentException("Invalid arguments");
		}
		view.run();
	}
}
