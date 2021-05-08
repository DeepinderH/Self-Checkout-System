package selfcheckout.software.views.gui.pages.attendantrefillcoins;

import org.junit.Before;
import org.junit.Test;
import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;
import selfcheckout.software.views.gui.pages.attendantrefillcoins.AttendantRefillCoinsHandler;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AttendantRefillCoinsHandlerTest {

	private ViewStateManager viewStateManager;
	private JPanel refillCashPanel;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;

	@Before
	public void setup() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ATTENDANT_REFILL_COINS);
		JFrame frame = new JFrame();

		AttendantDatabase attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();

		SelfCheckoutController scc = new RefillCoinSelfCheckoutController();

		new AttendantRefillCoinsHandler(this.viewStateManager, scc, frame);

		try {
			scc.getAttendantConsoleController().loginAsAttendant(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Incorrect attendant credentials");
		}

		refillCashPanel = (JPanel) frame.getContentPane();
	}

	private class RefillCoinSelfCheckoutController extends SelfCheckoutController {

		public RefillCoinSelfCheckoutController() {
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
				public void refillCoinDispenser(
						BigDecimal coinValue, Currency currency, int numAdditionalCoins) {
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
		valueBox.setText("2.00");
		JTextField currencyBox = (JTextField) refillCashPanel.getComponents()[3];
		currencyBox.setText("CAD");
		JSpinner spinner = (JSpinner) refillCashPanel.getComponents()[5];
		spinner.setValue(1);
		JButton confirmBtn = (JButton) refillCashPanel.getComponents()[7];
		confirmBtn.doClick();
	}

}
