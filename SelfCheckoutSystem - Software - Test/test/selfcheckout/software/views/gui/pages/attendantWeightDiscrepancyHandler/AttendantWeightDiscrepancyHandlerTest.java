package selfcheckout.software.views.gui.pages.attendantWeightDiscrepancyHandler;

import javax.swing.*;

import org.junit.Before;
import org.junit.Test;

import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;
import selfcheckout.software.views.gui.pages.attendantapproveweight.AttendantWeightDiscrepancyHandler;

import static org.junit.Assert.fail;

public class AttendantWeightDiscrepancyHandlerTest {
	
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
				public void approveLastItemWeight(int attendantId, String attendantPassword) {
					throw new ExpectedThrownException();
				}
			};
		}
	}
	
	@Before
	public void setUp() {
		ProductDatabasesWrapper.initializeDatabases();
		ProductDatabasesWrapper products = new ProductDatabasesWrapper();
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ATTENDANT_WEIGHT_DISCREPANCY_APPROVAL);
		this.frame = new JFrame();
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new CheckoutController(products);
		createAttendantLoginPanel();
	}

	private void createAttendantLoginPanel() {
		new AttendantWeightDiscrepancyHandler(viewStateManager, this.scc, this.frame);
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
