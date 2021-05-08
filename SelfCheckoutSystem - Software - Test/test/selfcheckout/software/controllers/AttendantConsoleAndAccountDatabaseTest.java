package selfcheckout.software.controllers;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.exceptions.AttendantAccountAlreadyExistsException;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;

public class AttendantConsoleAndAccountDatabaseTest {
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
				1,
				1);
		this.attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(this.attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.controllerStateManager = new ControllerStateManager(ControllerStateEnum.ITEM_ADDITION);
		this.attendantConsoleController = new AttendantConsoleController(this.selfCheckoutStation, null, null, this.controllerStateManager, this.attendantDatabaseWrapper);

	}

	@Test (expected = ControlSoftwareException.class)
	public void testBlockStationWithoutAttendantAccess() throws ControlSoftwareException {
		this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
		this.attendantConsoleController.blockStation();
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
		this.attendantConsoleController.blockStation();
		this.controllerStateManager.setState(ControllerStateEnum.ORDER_PAYMENT);
		this.attendantConsoleController.blockStation();
	}
	
	@Test 
	public void testBlockStationWitAttendantAccess() {
		this.controllerStateManager.setState(ControllerStateEnum.ATTENDANT_ACCESS);
		this.attendantConsoleController.blockStation();
		assertEquals(ControllerStateEnum.DISABLED, this.controllerStateManager.getState());
	}
	
	@Test
	public void testUnblockStationWithCorrectInformation() {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (ControlSoftwareException | IncorrectAttendantLoginInformationException e) {
			fail("No exception should be thrown");
		}
	}
	
	@Test (expected = IncorrectAttendantLoginInformationException.class)
	public void testUnblockStationWithInCorrectUsername() throws IncorrectAttendantLoginInformationException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.WRONG_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (ControlSoftwareException e) {
			fail("Should not throw a ControlSoftwareException");
		}
	}
	
	@Test (expected = IncorrectAttendantLoginInformationException.class)
	public void testUnblockStationWithInCorrectPassword() throws IncorrectAttendantLoginInformationException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.WRONG_ATTENDANT_PASSWORD);
		} catch (ControlSoftwareException e) {
			fail("Should not throw a ControlSoftwareException");
		}
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testUnblockStationWithoutControllerStateATTENDANT_ACCESS() throws ControlSoftwareException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ATTENDANT_ACCESS);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Should not throw a IncorrectAttendantLoginInformationException");
		}
	}
	
	@Test
	public void testUnblockStationWithNewAttendantAccount() {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
			this.attendantDatabaseWrapper.clearAttendantDatabase();
			this.attendantDatabaseWrapper.initializeAttendantLoginInfo(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (ControlSoftwareException | IncorrectAttendantLoginInformationException | AttendantAccountAlreadyExistsException e) {
			fail("No exception should be thrown");
		} 
	}
	
	@Test (expected = AttendantAccountAlreadyExistsException.class)
	public void testUnblockStationWithNewExistingAttendantAccount() throws AttendantAccountAlreadyExistsException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
			this.attendantDatabaseWrapper.initializeAttendantLoginInfo(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (ControlSoftwareException | IncorrectAttendantLoginInformationException e) {
			fail("Only AttendantAccountAlreadyExistsException should be thrown");
		} 
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testUnblockStationWithoutControllerStateITEM_ADDITION() throws ControlSoftwareException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Should not throw a IncorrectAttendantLoginInformationException");
		}
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testUnblockStationWithControllerStateORDER_PAYMENT() throws ControlSoftwareException {
		try {
			this.controllerStateManager.setState(ControllerStateEnum.ORDER_PAYMENT);
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Should not throw a IncorrectAttendantLoginInformationException");
		}
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testShutdownStationWithControllerStateORDER_PAYMENT() throws ControlSoftwareException {
		this.controllerStateManager.setState(ControllerStateEnum.ORDER_PAYMENT);
		this.attendantConsoleController.shutdownStation();
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testShutdownStationWithControllerStateDISABLED() throws ControlSoftwareException {
		this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
		this.attendantConsoleController.shutdownStation();
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testShutdownStationWithControllerStateITEM_ADDITION() throws ControlSoftwareException {
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
		this.attendantConsoleController.shutdownStation();
	}
}
