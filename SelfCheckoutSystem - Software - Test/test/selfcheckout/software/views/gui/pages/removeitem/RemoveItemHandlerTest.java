package selfcheckout.software.views.gui.pages.removeitem;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.junit.Before;
import org.junit.Test;

import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RemoveItemHandlerTest {

	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel removeItemPanel;
	AttendantDatabaseWrapper attendantDatabaseWrapper;

	private class RemoveItemSelfCheckoutController extends SelfCheckoutController {

		public RemoveItemSelfCheckoutController(
				ProductDatabasesWrapper productDatabaseWrapper) {
			super(new BasicSelfCheckoutStation(),
					productDatabaseWrapper, null, null, null,
				attendantDatabaseWrapper);
		}

		ControllerStateManager controllerStateManager = new ControllerStateManager(ControllerStateEnum.DISABLED);

		@Override
		public int getNumberOfItems() {
			return 10;
		}

		@Override
		public AttendantConsoleController getAttendantConsoleController() {
			return new AttendantConsoleController(
					new BasicSelfCheckoutStation(), null, null,
					controllerStateManager,
				attendantDatabaseWrapper) {
				@Override
				public void removeItem(int itemIndex) {
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

		this.scc = new RemoveItemSelfCheckoutController(productDatabasesWrapper);
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
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ITEM_REMOVAL);
		createRemoveItemPanel();
	}

	private void createRemoveItemPanel() {
		new RemoveItemHandler(this.viewStateManager, this.scc, this.frame);
		this.removeItemPanel = (JPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToAttendantMenuButton() {
		JPanel rightPanel = (JPanel) this.removeItemPanel.getComponents()[1];
		JTextField itemToRemove = (JTextField) rightPanel.getComponents()[1];
		// even though an index is set, should not remove an item when
		// the return to main menu button is pressed
		itemToRemove.setText("1");
		JButton returnToAttendantMenuButton = (JButton) rightPanel.getComponents()[2];
		returnToAttendantMenuButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testValidIndex() {
		JPanel rightPanel = (JPanel) this.removeItemPanel.getComponents()[1];
		JTextField itemToRemove = (JTextField) rightPanel.getComponents()[1];
		itemToRemove.setText("1");
		JButton removeItemButton = (JButton) rightPanel.getComponents()[3];
		removeItemButton.doClick();
	}

}