package selfcheckout.software.controllers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;

public class SkipBaggingTest {
	
	// creating the everything required to test
	private AttendantDatabase attendantDatabase;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;
	private AttendantConsoleController attendantConsoleController;
	private ControllerStateManager controllerStateManager;
	private ProductDatabasesWrapper databaseWrapper;
	private PurchaseManager purchaseManager;
	private SelfCheckoutStation selfCheckoutStation;

	@Before
	public void setUp() {
		
		ProductDatabasesWrapper.initializeDatabases();
		
		this.attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.controllerStateManager = new ControllerStateManager(ControllerStateEnum.ATTENDANT_ACCESS);
		this.databaseWrapper = new ProductDatabasesWrapper();
		this.purchaseManager = new PurchaseManager(new ProductDatabasesWrapper());
		
		this.selfCheckoutStation = new SelfCheckoutStation(ControllerTestConstants.CURRENCY, ControllerTestConstants.BANKNOTE_DENOMINATIONS,
				ControllerTestConstants.COIN_DENOMINATIONS, 10, 1);
		this.attendantConsoleController  = new AttendantConsoleController(selfCheckoutStation, databaseWrapper, purchaseManager,
				controllerStateManager, attendantDatabaseWrapper);
	}

	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
		attendantDatabaseWrapper.clearAttendantDatabase();
	}

	//Checking a valid skipBaggingLastItem method 
	@Test
	public void testskipBaggingLastItem() throws NoSuchItemException, OverloadException {
		BarcodedItem item = new BarcodedItem(new Barcode("23578"), 1.0);
		
		this.selfCheckoutStation.scale.add(item);
		purchaseManager.addItem(item);
		
		
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
		try {
			this.attendantConsoleController.skipBaggingLastItem(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant has to log in with correct information before an item skip bagging");
		}
		
		assertEquals(this.purchaseManager.getItemsBagged().size(), 0);
	}
	
	//Confirming that no item is placed in the bagging area
	@Test (expected = SimulationException.class)
	public void testskipBaggingLastItemException() throws NoSuchItemException, OverloadException {
		BarcodedItem item = new BarcodedItem(new Barcode("23578"), 1.0);
		
		this.selfCheckoutStation.scale.add(item);
		purchaseManager.addItem(item);
		
		
		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
		try {
			this.attendantConsoleController.skipBaggingLastItem(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant has to log in with correct information before an item skip bagging");
		}
		this.selfCheckoutStation.baggingArea.remove(item);
	}
	
	//Checking if a valid skipBaggingLastItem method will throw an error in the wrong state
	@Test  (expected = ControlSoftwareException.class)
	public void testskipBaggingLastItemWrongState() throws NoSuchItemException, OverloadException {
		BarcodedItem item = new BarcodedItem(new Barcode("23578"), 1.0);
		
		this.selfCheckoutStation.scale.add(item);
		this.selfCheckoutStation.baggingArea.add(item);
		purchaseManager.addItem(item);
				
		try {
			this.attendantConsoleController.skipBaggingLastItem(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant has to log in with correct information before an item skip bagging");
		}
	}
	
	//Checking if the get total weight of bagging area is correct in purchase manager with some items not bagged
	@Test 
	public void testCustomerRemovesItemsWithSkippedBagging() throws NoSuchItemException, OverloadException {
		BarcodedItem item = new BarcodedItem(new Barcode("23578"), 3.1);
		BarcodedItem item2 = new BarcodedItem(new Barcode("791666190"), 2.3);
		
		purchaseManager.addItemTrackBagging(item, true);
		purchaseManager.addItem(item);
		this.selfCheckoutStation.baggingArea.add(item);
		
		purchaseManager.addItem(item2);
		purchaseManager.addItemTrackBagging(item2, false);

		assertEquals(this.purchaseManager.getItemsBagged().size(), 1);
	}

}