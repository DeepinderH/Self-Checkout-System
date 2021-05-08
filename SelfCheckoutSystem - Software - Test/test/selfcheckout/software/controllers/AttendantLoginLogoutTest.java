package selfcheckout.software.controllers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;

public class AttendantLoginLogoutTest {

	private AttendantDatabase attendantDatabase;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;
	private AttendantConsoleController attendantConsoleController;
	private ControllerStateManager controllerStateManager;
	private SelfCheckoutStation selfCheckoutStation;

	@Before
	public void setUp() {
		this.selfCheckoutStation = new SelfCheckoutStation(ControllerTestConstants.CURRENCY,
				ControllerTestConstants.BANKNOTE_DENOMINATIONS,
				ControllerTestConstants.COIN_DENOMINATIONS,
				ControllerTestConstants.SCALE_MAXIMUM_WEIGHT,
				ControllerTestConstants.SCALE_SENSITIVITY);
		this.attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(this.attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.controllerStateManager = new ControllerStateManager(ControllerStateEnum.ITEM_ADDITION);
		this.attendantConsoleController = new AttendantConsoleController(this.selfCheckoutStation, 
				null,
				null,
				this.controllerStateManager, 
				this.attendantDatabaseWrapper);
	}
	
	@Test 
	public void loginWithoutAttendantAccess() {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
			this.attendantConsoleController.loginAsAttendant(ControllerTestConstants.VALID_ATTENDANT_ID,
					ControllerTestConstants.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Should not throw IncorrectAttendantLoginInformationException");
		}
	}

	@Test (expected =  ControlSoftwareException.class)
	public void logoutWithoutAttendantAccess() throws ControlSoftwareException {
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
		this.attendantConsoleController.logoutAsAttendant();
	}

	@Test (expected =  ControlSoftwareException.class)
	public void loginWithAttendantAccess() throws ControlSoftwareException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ATTENDANT_ACCESS);
			this.attendantConsoleController.loginAsAttendant(ControllerTestConstants.VALID_ATTENDANT_ID,
					ControllerTestConstants.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Should not throw IncorrectAttendantLoginInformationException");
		}
	}

	@Test
	public void logoutWithAttendantAccess() {
		this.controllerStateManager.setState(ControllerStateEnum.ATTENDANT_ACCESS);
		this.attendantConsoleController.logoutAsAttendant();
	}

	@Test (expected =  IncorrectAttendantLoginInformationException.class)
	public void loginWithIncorrectID() throws IncorrectAttendantLoginInformationException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
			this.attendantConsoleController.loginAsAttendant(ControllerTestConstants.INVALID_ATTENDANT_ID,
					ControllerTestConstants.VALID_ATTENDANT_PASSWORD);
		} catch (ControlSoftwareException e) {
			fail("Should not throw ControlSoftwareException");
		}
	}

	@Test (expected =  IncorrectAttendantLoginInformationException.class)
	public void loginWithIncorrectPassword() throws IncorrectAttendantLoginInformationException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
			this.attendantConsoleController.loginAsAttendant(ControllerTestConstants.VALID_ATTENDANT_ID,
					ControllerTestConstants.INVALID_ATTENDANT_ID_PASSWORD);
		} catch (ControlSoftwareException e) {
			fail("Should not throw ControlSoftwareException");
		}
	}

	@Test  (expected =  IncorrectAttendantLoginInformationException.class)
	public void loginWithIncorrectIDAndPassword() throws IncorrectAttendantLoginInformationException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
			this.attendantConsoleController.loginAsAttendant(ControllerTestConstants.INVALID_ATTENDANT_ID,
					ControllerTestConstants.INVALID_ATTENDANT_ID_PASSWORD);
		} catch (ControlSoftwareException e) {
			fail("Should not throw ControlSoftwareException");
		}
	}
}
