package selfcheckout.software.views.gui.pages.attendantrefillprinterink;

import org.junit.Before;
import org.junit.Test;
import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;
import selfcheckout.software.views.gui.pages.TextFieldButtonAttendantPanel;
import selfcheckout.software.views.gui.pages.attendantreceiptinkaddition.AttendantRefillReceiptInkHandler;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AttendantRefillReceiptInkHandlerTest {

	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private TextFieldButtonAttendantPanel refillInkPanel;
	AttendantDatabaseWrapper attendantDatabaseWrapper;

	private class AttendantProductLookUpSelfCheckoutController extends SelfCheckoutController {

		public AttendantProductLookUpSelfCheckoutController(
				ProductDatabasesWrapper productDatabaseWrapper) {
			super(new BasicSelfCheckoutStation(),
					productDatabaseWrapper, null, null, null,
				attendantDatabaseWrapper);
		}

		ControllerStateManager controllerStateManager = new ControllerStateManager(ControllerStateEnum.DISABLED);

		@Override
		public AttendantConsoleController getAttendantConsoleController() {
			return new AttendantConsoleController(
					new BasicSelfCheckoutStation(), null, null,
					controllerStateManager,
				attendantDatabaseWrapper) {
				@Override
				public void addInkToReceiptPrinter(int quantity) {
					throw new ExpectedThrownException();
				}
			};
		}
	}

	@Before
	public void setUp() {
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();

		ProductDatabasesWrapper.initializeDatabases();
		ProductDatabasesWrapper productDatabasesWrapper = new ProductDatabasesWrapper();

		this.scc = new AttendantProductLookUpSelfCheckoutController(productDatabasesWrapper);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				ControllerTestConstants.VALID_ATTENDANT_ID,
				ControllerTestConstants.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Should be able to login as attendant");
		}
		try {
			this.scc.getAttendantConsoleController().loginAsAttendant(
				ControllerTestConstants.VALID_ATTENDANT_ID,
				ControllerTestConstants.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Should be able to login as attendant");
		}
		this.frame = new JFrame();
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ATTENDANT_RECEIPT_INK_ADDITION);
		createRefillInkPanel();
	}

	private void createRefillInkPanel() {
		new AttendantRefillReceiptInkHandler(this.viewStateManager, this.scc, this.frame);
		this.refillInkPanel = (TextFieldButtonAttendantPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToAttendantMenuButton() {
		JTextField descriptionField = (JTextField) this.refillInkPanel.getComponents()[1];
		// even though an index is set, should not remove an item when
		// the return to main menu button is pressed
		descriptionField.setText("10");
		JButton returnToAttendantMenuButton = (JButton) this.refillInkPanel.getComponents()[3];
		returnToAttendantMenuButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testRefillReceiptInk() {
		JTextField descriptionField = (JTextField) this.refillInkPanel.getComponents()[1];
		descriptionField.setText("10");
		assertEquals(this.refillInkPanel.getInputText(), "10");
		JButton addInk = (JButton)  this.refillInkPanel.getComponents()[2];
		addInk.doClick();
	}

}