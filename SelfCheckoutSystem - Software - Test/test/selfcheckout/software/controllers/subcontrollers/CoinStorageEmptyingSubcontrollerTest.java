package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;

public class CoinStorageEmptyingSubcontrollerTest {
	private ProductDatabasesWrapper databasesWrapper;
	private SelfCheckoutController selfCheckoutController;
	
	@Before
	public void setUp() {
		SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(ControllerTestConstants.CURRENCY,
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
			selfCheckoutStation, this.databasesWrapper, null, null, null,
			attendantDatabaseWrapper);
	}

	@After
	public void teardown() {
		ProductDatabasesWrapper.resetDatabases();
	}

	@Test
	public void testEmptyCoinStorageCorrect() {
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
		BasicSelfCheckoutStation.insertCoinSuccessfully(this.selfCheckoutController, new BigDecimal("1.00"));
		try {
			this.selfCheckoutController.getAttendantConsoleController().loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Must be in ATTENDANT_ACCESS controller state to use this function");
		}
		BigDecimal expected = new BigDecimal("1.00");
		BigDecimal result = this.selfCheckoutController.getAttendantConsoleController().emptyCoinStorageUnit();
		assertEquals(expected.compareTo(result), 0);
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testEmptyCoinStorageException() {
		this.selfCheckoutController.getAttendantConsoleController().emptyCoinStorageUnit();
	}
}
