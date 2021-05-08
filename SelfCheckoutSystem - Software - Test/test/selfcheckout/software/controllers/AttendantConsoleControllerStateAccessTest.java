package selfcheckout.software.controllers;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.InvalidPrinterRefillException;
import selfcheckout.software.controllers.exceptions.NoSuchItemException;
import selfcheckout.software.controllers.exceptions.RefillBanknoteException;
import selfcheckout.software.controllers.exceptions.RefillCoinException;
import selfcheckout.software.controllers.exceptions.WeightOverloadException;
import selfcheckout.software.controllers.subcontrollers.ProductLookupSubcontroller;

public class AttendantConsoleControllerStateAccessTest {
	private AttendantDatabaseWrapper attendantDatabaseWrapper;
	private AttendantConsoleController attendantConsoleController;
	private ControllerStateManager controllerStateManager;
	private SelfCheckoutStation selfCheckoutStation;
	private PurchaseManager purchaseManager;

	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		MembershipDatabaseWrapper.initializeMembershipDatabase();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(new AttendantDatabase());
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		ProductDatabasesWrapper databaseWrapper = new ProductDatabasesWrapper();
		this.purchaseManager = new PurchaseManager(new ProductDatabasesWrapper());
		this.selfCheckoutStation = new SelfCheckoutStation(ControllerTestConstants.CURRENCY,
				ControllerTestConstants.BANKNOTE_DENOMINATIONS,
				ControllerTestConstants.COIN_DENOMINATIONS,
				1,
				1);
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		new ProductLookupSubcontroller(databaseWrapper);
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.controllerStateManager = new ControllerStateManager(ControllerStateEnum.ITEM_ADDITION);
		this.attendantConsoleController = new AttendantConsoleController(this.selfCheckoutStation, databaseWrapper, this.purchaseManager, this.controllerStateManager, this.attendantDatabaseWrapper);
	}
	
	@After
	public void tearDown() {
		ProductDatabasesWrapper.resetDatabases();
		attendantDatabaseWrapper.clearAttendantDatabase();
	}
	
	@Test
	public void testLoginAsAttendantCorrectState() {
		try {
			this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
	}

	@Test (expected = ControlSoftwareException.class)
	public void testLoginAsAttendantInCorrectState() {
		try {
			this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
		try {
			this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
	}
	
	@Test
	public void testLogoutAsAttendantCorrectState() {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.logoutAsAttendant();
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testLogoutAsAttendantInCorrectState() {
		this.attendantConsoleController.logoutAsAttendant();
	}

	@Test
	public void testApproveLastItemWeightCorrectState() throws NoSuchItemException {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.selfCheckoutStation.scale.add(ControllerTestConstants.VALID_BARCODED_ITEM);
		this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		this.controllerStateManager.setState(ControllerStateEnum.ATTENDANT_ACCESS);
		this.controllerStateManager.setState(ControllerStateEnum.DISABLED);
		try {
			this.attendantConsoleController.approveLastItemWeight(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (WeightOverloadException | IncorrectAttendantLoginInformationException e) {
			 fail("Attendant could not login but credentials should be valid");
		}
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testApproveLastItemWeightIncorrectState() {
		try {
			this.attendantConsoleController.approveLastItemWeight(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (WeightOverloadException | IncorrectAttendantLoginInformationException e) {
			 fail("Attendant could not login but credentials should be valid");
		}
	}

	@Test
	public void testSkipBaggingLastItemCorrectState() throws NoSuchItemException {
		this.selfCheckoutStation.scale.add(ControllerTestConstants.VALID_BARCODED_ITEM);
		this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		try {
			this.attendantConsoleController.skipBaggingLastItem(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testSkipBaggingLastItemIncorrectState() throws NoSuchItemException {
		this.selfCheckoutStation.scale.add(ControllerTestConstants.VALID_BARCODED_ITEM);
		this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		this.controllerStateManager.setState(ControllerStateEnum.ATTENDANT_ACCESS);
		try {
			this.attendantConsoleController.skipBaggingLastItem(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
	}

	@Test
	public void testRemoveItemCorrectState() throws NoSuchItemException {
		this.selfCheckoutStation.scale.add(ControllerTestConstants.VALID_BARCODED_ITEM);
		this.purchaseManager.addItem(ControllerTestConstants.VALID_BARCODED_ITEM);
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.removeItem(0);
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testRemoveItemIncorrectState() {
		this.attendantConsoleController.removeItem(0);
	}

	@Test
	public void testLookUpProductbyDescriptionCorrectState() {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.lookUpProductByDescription("Cavendish Bananas");
	}
	
	@Test
	public void testLookUpProductbyDescriptionCorrectStateNull() {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.lookUpProductByDescription("");
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testLookUpProductbyDescriptionIncorrectState() {
		this.attendantConsoleController.lookUpProductByDescription("Cavendish Bananas");
	}

	@Test
	public void testAddPaperToReceiptPrinterCorrectState() throws InvalidPrinterRefillException {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.addPaperToReceiptPrinter(1);
	}
	
	@Test
	public void testAddPaperToReceiptPrinterCorrectStateElse() throws InvalidPrinterRefillException {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.addPaperToReceiptPrinter(1);
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testAddPaperToReceiptPrinterIncorrectState() throws InvalidPrinterRefillException {
		this.attendantConsoleController.addPaperToReceiptPrinter(1);
	}

	@Test
	public void testAddInkToReceiptPrinterCorrectState() throws InvalidPrinterRefillException {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.addInkToReceiptPrinter(1);
	}
	
	@Test
	public void testAddInkToReceiptPrinterCorrectStateElse() throws InvalidPrinterRefillException {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.addInkToReceiptPrinter(1);
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testAddInkToReceiptPrinterIncorrectState() throws InvalidPrinterRefillException {
		this.attendantConsoleController.addInkToReceiptPrinter(1);
	}

	@Test
	public void testEmptyCoinStorageUnitCorrectState() {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.emptyCoinStorageUnit();
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testEmptyCoinStorageUnitIncorrectState() {
		this.attendantConsoleController.emptyCoinStorageUnit();
	}

	@Test
	public void testEmptyBanknoteStorageUnitCorrectState() {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.emptyBanknoteStorageUnit();
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testEmptyBanknoteStorageUnitIncorrectState() {
		this.attendantConsoleController.emptyBanknoteStorageUnit();
	}

	@Test
	public void testRefillCoinDispenserCorrectState() throws RefillCoinException {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.refillCoinDispenser(ControllerTestConstants.COIN_DENOMINATIONS[0], ControllerTestConstants.CURRENCY, 1);
	}

	@Test(expected = RefillCoinException.class)
	public void testRefillCoinDispenserCorrectStateNoDispenser() throws RefillCoinException {
		try {
			this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.refillCoinDispenser(ControllerTestConstants.INVALID_COIN_DENOMINATION, ControllerTestConstants.CURRENCY, 1);
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testRefillCoinDispenserIncorrectState() throws RefillCoinException {
		this.attendantConsoleController.refillCoinDispenser(ControllerTestConstants.COIN_DENOMINATIONS[0], ControllerTestConstants.CURRENCY, 1);
	}

	@Test
	public void testRefillBanknoteDispenserCorrectState() throws RefillBanknoteException {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.refillBanknoteDispenser(ControllerTestConstants.BANKNOTE_DENOMINATIONS[0], ControllerTestConstants.CURRENCY, 1);
	}


	@Test(expected = RefillBanknoteException.class)
	public void testRefillBanknoteDispenserCorrectStateNoDispenser() throws RefillBanknoteException {
		try {
			this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.refillBanknoteDispenser(ControllerTestConstants.INVALID_BANKNOTE_DENOMINATION, ControllerTestConstants.CURRENCY, 1);
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testRefillBanknoteDispenserIncorrectState() throws RefillBanknoteException {
		this.attendantConsoleController.refillBanknoteDispenser(ControllerTestConstants.BANKNOTE_DENOMINATIONS[0], ControllerTestConstants.CURRENCY, 1);
	}

	@Test
	public void testBlockStationCorrectState() {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.blockStation();
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testBlockStationIncorrectState() {
		this.attendantConsoleController.blockStation();
	}

	@Test
	public void testUnblockStationCorrectState() {
		try {
		    this.attendantConsoleController.loginAsAttendant(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
		    fail("Attendant could not login but credentials should be valid");
		}
		this.attendantConsoleController.blockStation();
		try {
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testUnblockStationIncorrectState() {
		try {
			this.attendantConsoleController.unblockStation(AttendantConsoleConstant.VALID_ATTENDANT_ID, AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant could not login but credentials should be valid");
		}
	}
	
	@Test (expected = ControlSoftwareException.class)
	public void testShutdownStationIncorrectState() {
		this.attendantConsoleController.shutdownStation();
	}

}
