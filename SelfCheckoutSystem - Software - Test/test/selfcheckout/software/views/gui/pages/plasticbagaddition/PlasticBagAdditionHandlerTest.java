package selfcheckout.software.views.gui.pages.plasticbagaddition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.junit.Before;
import org.junit.Test;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.ItemBaggingException;
import selfcheckout.software.controllers.exceptions.ItemScanningException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

public class PlasticBagAdditionHandlerTest {
	
	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel plasticBagPanel;
	private ProductDatabasesWrapper products;

	private static class PlasticBagSelfCheckoutController extends SelfCheckoutController {
		public PlasticBagSelfCheckoutController(
			SelfCheckoutStation station, ProductDatabasesWrapper products,
			AttendantDatabaseWrapper attendantDatabaseWrapper) {
			super(station,
				products, null, null, null,
				attendantDatabaseWrapper);
		}
	}

	@Before
	public void setup() throws ItemBaggingException, ItemScanningException {
		ProductDatabasesWrapper.initializeDatabases();
		products = new ProductDatabasesWrapper();
		this.viewStateManager = new ViewStateManager(ViewStateEnum.PAYMENT_START);
		this.frame = new JFrame();

		BasicSelfCheckoutStation station = new BasicSelfCheckoutStation();
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.scc = new PlasticBagSelfCheckoutController(
			station, products, attendantDatabaseWrapper);
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant should be able to log in");
		}
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.scc, products);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(this.scc);

		createPlasticBagPanel();
	}

	private void createPlasticBagPanel() {
		new PlasticBagAdditionHandler(viewStateManager, this.scc, this.frame);
		this.plasticBagPanel = (JPanel) frame.getContentPane();
	}

	// Tests normal usage of the panel
	@Test
	public void testNormal() {
		JSpinner spinner = (JSpinner) this.plasticBagPanel.getComponents()[1];
		spinner.setValue(2);
		JButton confirmBtn = (JButton) this.plasticBagPanel.getComponents()[2];
		confirmBtn.doClick();
		assertEquals(ViewStateEnum.ORDER_PAYMENT_MENU, viewStateManager.getState());
		assertEquals(this.scc.getControllerStateEnumStatus(), ControllerStateEnum.ORDER_PAYMENT);
	}

}
