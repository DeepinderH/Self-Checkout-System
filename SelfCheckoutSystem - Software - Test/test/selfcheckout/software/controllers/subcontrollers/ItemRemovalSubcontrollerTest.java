package selfcheckout.software.controllers.subcontrollers;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.AttendantConsoleController;
import selfcheckout.software.controllers.AttendantDatabase;
import selfcheckout.software.controllers.AttendantDatabaseWrapper;
import selfcheckout.software.controllers.ControllerStateEnum;
import selfcheckout.software.controllers.ControllerStateManager;
import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.PurchaseManager;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;


public class ItemRemovalSubcontrollerTest {

	// creating the everything required to test
	private AttendantDatabase attendantDatabase;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;
	private AttendantConsoleController attendantConsoleController;
	private ControllerStateManager controllerStateManager;
	private ProductDatabasesWrapper databaseWrapper;
	private ElectronicScale baggingScale;
	private ItemRemovalSubcontroller itemRemovalSubcontroller;
	private PurchaseManager purchaseManager;
	private SelfCheckoutStation selfCheckoutStation;
	
	private static final BarcodedItem item  = new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 1.0);
	private static final BarcodedItem item2 = new BarcodedItem(ControllerTestConstants.VALID_BARCODE, 1.0);

	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		
		this.attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.baggingScale = new ElectronicScale (10,1);
		this.controllerStateManager = new ControllerStateManager(ControllerStateEnum.ATTENDANT_ACCESS);
		this.databaseWrapper = new ProductDatabasesWrapper();
		this.purchaseManager = new PurchaseManager(new ProductDatabasesWrapper());
		this.itemRemovalSubcontroller = new ItemRemovalSubcontroller(baggingScale, purchaseManager);
		
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
	
	//Checking a if the first item is successfully removed
	@Test
	public void testAttendantRemovesFirstItem() throws NoSuchItemException, OverloadException {
		
		this.selfCheckoutStation.baggingArea.add(item);
		purchaseManager.addItem(item);
		
		this.selfCheckoutStation.baggingArea.add(item2);
		purchaseManager.addItem(item2);
		
		this.attendantConsoleController.removeItem(0);
		assertEquals(purchaseManager.getItems().size(), 1);
	}
	
	//Checking if an item can be removed when in the wrong state
	@Test (expected = ControlSoftwareException.class)
	public void testAttendantRemovesFirstItemWrongState() throws NoSuchItemException, OverloadException {
		
		this.selfCheckoutStation.baggingArea.add(item);
		purchaseManager.addItem(item);
		
		this.selfCheckoutStation.baggingArea.add(item2);
		purchaseManager.addItem(item2);

		this.controllerStateManager.setState(ControllerStateEnum.ITEM_ADDITION);
		
		this.attendantConsoleController.removeItem(0);
	}
	
	//Checking if an item can be removed when in the wrong state
	@Test 
	public void testAttendantRemovesLastItem() throws NoSuchItemException, OverloadException {
		
		this.selfCheckoutStation.baggingArea.add(item);
		purchaseManager.addItem(item);
		
		this.selfCheckoutStation.baggingArea.add(item2);
		purchaseManager.addItem(item2);
		
		this.attendantConsoleController.removeItem(purchaseManager.getProducts().size()-1);
		
		assertEquals(purchaseManager.getLastItem(), item);
	}
	
	
	//Tests an item being purchased and bagged
	@Test
	public void testremovePurchasedItems() throws NoSuchItemException, OverloadException {
		
		purchaseManager.addItem(item);
		baggingScale.add(item);
		
		purchaseManager.addItem(item2);
		baggingScale.add(item2);
		
		itemRemovalSubcontroller.removePurchasedItems(0);
		
		assertEquals(purchaseManager.getItems().size(), 1);
		assertTrue(baggingScale.getCurrentWeight() == 1);
	}
	
	//Tests an item being purchased (with an unpurchased item in the bagging area)
	@Test
	public void testRemovePurchasedItemsNotBagged() throws NoSuchItemException, OverloadException {

		purchaseManager.addItem(item);
		baggingScale.add(item2);

		itemRemovalSubcontroller.removePurchasedItems(0);
		
		assertEquals(purchaseManager.getItems().size(), 0);
		assertTrue(baggingScale.getCurrentWeight() == 1);
	}
	
	//Tests to see if an out of bound exception is thrown when no items in the purchase list with an item on bagging scale and an item is removed
	@Test (expected = RuntimeException.class)
	public void testremovePurchasedItemsOnlyBagged() throws NoSuchItemException, OverloadException {	
		itemRemovalSubcontroller.removePurchasedItems(0);

	}
	
	//Tests to see if an out of bound exception is thrown when no items in the purchase list and an item is removedD
	@Test (expected = RuntimeException.class)
	public void testremovePurchasedItemsException() throws NoSuchItemException, OverloadException {
		
		itemRemovalSubcontroller.removePurchasedItems(0);
		
	}

}