package selfcheckout.software.views.gui.pages.skipBagging;

import javax.swing.*;

import org.junit.Before;
import org.junit.Test;

import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.ControlSoftwareException;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;
import selfcheckout.software.views.gui.pages.skipbagging.SkipBaggingHandler;

import static org.junit.Assert.fail;

public class SkipBaggingHandlerTest {
	
	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel attendantLoginPanel;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;

	private class CheckoutController extends SelfCheckoutController {
		public CheckoutController(ProductDatabasesWrapper products) {
			super(new BasicSelfCheckoutStation(),
				products, null, null, null,
				attendantDatabaseWrapper);
		}

		ControllerStateManager controllerStateManager = new ControllerStateManager(ControllerStateEnum.DISABLED);

		@Override
		public AttendantConsoleController getAttendantConsoleController() {
			return new AttendantConsoleController(
					new BasicSelfCheckoutStation(), null, null,
					controllerStateManager, attendantDatabaseWrapper) {
				@Override
				public void skipBaggingLastItem(int attendantId, String attendantPassword) {
					throw new ExpectedThrownException();
				}
			};
		}
	}
	
	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		ProductDatabasesWrapper products = new ProductDatabasesWrapper();
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.viewStateManager = new ViewStateManager(ViewStateEnum.SKIP_BAGGING_ITEM);
		this.frame = new JFrame();
		this.scc = new CheckoutController(products);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("credentials should be valid");
		}

		createAttendantLoginPanel();
	}

	private void createAttendantLoginPanel() {
		new SkipBaggingHandler(viewStateManager, this.scc, this.frame);
		this.attendantLoginPanel = (JPanel) frame.getContentPane();
	}

	@Test (expected = ExpectedThrownException.class)
	public void testButton() {
		JTextField attendantIdTextArea = (JTextField) (this.attendantLoginPanel.getComponents()[2]);
		attendantIdTextArea.setText(String.valueOf(AttendantConsoleConstant.VALID_ATTENDANT_ID));
		JTextField attendantPasswordTextArea = (JTextField) (this.attendantLoginPanel.getComponents()[4]);
		attendantPasswordTextArea.setText(AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		JButton skipBaggingButton = (JButton) (this.attendantLoginPanel.getComponents()[5]);
		skipBaggingButton.doClick();
	}

}
