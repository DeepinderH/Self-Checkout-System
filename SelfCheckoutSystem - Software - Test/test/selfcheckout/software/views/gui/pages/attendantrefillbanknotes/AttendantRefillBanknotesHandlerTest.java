package selfcheckout.software.views.gui.pages.attendantrefillbanknotes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

public class AttendantRefillBanknotesHandlerTest {

	private ViewStateManager viewStateManager;
	private JPanel refillCashPanel;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;

	@Before
	public void setup() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ATTENDANT_REFILL_BANKNOTES);
		JFrame frame = new JFrame();

		AttendantDatabase attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();

		SelfCheckoutController scc = new RefillBanknoteSelfCheckoutController();

		new AttendantRefillBanknotesHandler(this.viewStateManager, scc, frame);

		try {
			scc.getAttendantConsoleController().loginAsAttendant(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Incorrect attendant credentials");
		}

		refillCashPanel = (JPanel) frame.getContentPane();
	}

	private class RefillBanknoteSelfCheckoutController extends SelfCheckoutController {

		public RefillBanknoteSelfCheckoutController() {
			super(new BasicSelfCheckoutStation(),
				null, null, null, null,
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
				public void refillBanknoteDispenser(
						int banknoteValue, Currency currency, int numAdditionalBanknotes) {
					throw new ExpectedThrownException();
				}
			};
		}
	}

	@Test
	public void testReturnToAttendantMenuButton() {
		JButton returnToAttendantMenuButton = (JButton) this.refillCashPanel.getComponents()[6];
		returnToAttendantMenuButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_MENU);
	}

	@Test(expected = ExpectedThrownException.class)
	public void testNormal() {
		JTextField valueBox = (JTextField) refillCashPanel.getComponents()[1];
		valueBox.setText("5");
		JTextField currencyBox = (JTextField) refillCashPanel.getComponents()[3];
		currencyBox.setText("CAD");
		JSpinner spinner = (JSpinner) refillCashPanel.getComponents()[5];
		spinner.setValue(1);
		JButton confirmBtn = (JButton) refillCashPanel.getComponents()[7];
		confirmBtn.doClick();
	}

}
