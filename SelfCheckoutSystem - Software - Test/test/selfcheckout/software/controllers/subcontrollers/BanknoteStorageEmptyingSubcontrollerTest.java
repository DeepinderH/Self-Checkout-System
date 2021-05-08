package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.BanknoteDispenser;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;

public class BanknoteStorageEmptyingSubcontrollerTest {
	private SelfCheckoutStation station;
	private SelfCheckoutController selfCheckoutController;
	private ProductDatabasesWrapper databasesWrapper;

	@Before
	public void setUp() {
		this.station = new SelfCheckoutStation(ControllerTestConstants.CURRENCY,
			ControllerTestConstants.BANKNOTE_DENOMINATIONS,
			ControllerTestConstants.COIN_DENOMINATIONS,
			1,
			1);
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.databasesWrapper = new ProductDatabasesWrapper();
		ProductDatabasesWrapper.initializeDatabases();
		this.selfCheckoutController = new SelfCheckoutController(
			this.station, this.databasesWrapper, null, null, null,
			attendantDatabaseWrapper);
	}

	@After
	public void teardown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test
	public void testEmptyBanknoteStorageUnit() {
		try {
			this.selfCheckoutController.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Must be able to login with attendant");
		}
		BasicSelfCheckoutStation.scanOneItemSuccessfully(
			this.selfCheckoutController, this.databasesWrapper);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(
			this.selfCheckoutController);
		this.selfCheckoutController.goToOrderPaymentState();
		BasicSelfCheckoutStation.insertBanknoteSuccessfully(this.selfCheckoutController, 5);
		try {
			this.selfCheckoutController.getAttendantConsoleController().loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("");
		}
		BigDecimal expected = new BigDecimal("5.00");
		BigDecimal result = this.selfCheckoutController.getAttendantConsoleController().emptyBanknoteStorageUnit();
		assertEquals(expected.compareTo(result), 0);
	}

	@Test
	public void testEmptyBanknoteStorageDispenser() {
		BanknoteDispenser dispenser =
			this.station.banknoteDispensers.get(
				ControllerTestConstants.BANKNOTE_DENOMINATIONS[0]);
		try {
			dispenser.load(
				new Banknote(ControllerTestConstants.BANKNOTE_DENOMINATIONS[0],
					ControllerTestConstants.CURRENCY));
		} catch (OverloadException e) {
			fail("should not be overloaded with a single banknote");
		}
		try {
			this.selfCheckoutController.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Must be able to login with attendant");
		}
		try {
			this.selfCheckoutController.getAttendantConsoleController().loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("");
		}
		BigDecimal expected = new BigDecimal(ControllerTestConstants.BANKNOTE_DENOMINATIONS[0]);
		BigDecimal result = this.selfCheckoutController.getAttendantConsoleController().emptyBanknoteStorageUnit();
		assertEquals(expected.compareTo(result), 0);
	}


	@Test (expected = ControlSoftwareException.class)
	public void testEmptyBanknoteStorageException() {
		this.selfCheckoutController.getAttendantConsoleController().emptyBanknoteStorageUnit();
	}

}
