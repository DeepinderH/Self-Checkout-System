package selfcheckout.software.views.gui.pages.paymentcard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;

import selfcheckout.software.controllers.AttendantConsoleConstant;
import selfcheckout.software.controllers.AttendantDatabase;
import selfcheckout.software.controllers.AttendantDatabaseWrapper;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.ProductDatabasesWrapper;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.controllers.exceptions.ItemBaggingException;
import selfcheckout.software.controllers.exceptions.ItemScanningException;
import selfcheckout.software.controllers.subcontrollers.PSTC;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

public class PaymentCardHandlerTest {
	private SelfCheckoutController selfCheckoutController;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel panel;
	private CardIssuer cardIssuer;
	private ProductDatabasesWrapper products;

	private static class PaymentCardSelfCheckoutController extends SelfCheckoutController {
		public PaymentCardSelfCheckoutController(
			SelfCheckoutStation station, CardIssuer cardIssuer,
			AttendantDatabaseWrapper attendantDatabaseWrapper,
			ProductDatabasesWrapper products) {
			super(station,
				products , cardIssuer, null, null,
				attendantDatabaseWrapper);
		}
		
        @Override
       public void swipePaymentCard(
            String type, String number, String cardholder, String cvv,
            String pin, boolean isTapEnabled, boolean hasChip, BufferedImage signature) {
            throw new ExpectedThrownException("SWIPE");
        }
        
        @Override
       public void insertPaymentCard(
            String type, String number, String cardholder, String cvv,
            String pin, boolean isTapEnabled, boolean hasChip) {
            throw new ExpectedThrownException("INSERT");
        }
        
        @Override
       public void tapPaymentCard(
            String type, String number, String cardholder, String cvv,
            String pin, boolean isTapEnabled, boolean hasChip) {
            throw new ExpectedThrownException("TAP");
        }
	}

	@Before
	public void setup() throws ItemBaggingException, ItemScanningException {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.PAYMENT_START);
		this.frame = new JFrame();
		ProductDatabasesWrapper.initializeDatabases();
		products = new ProductDatabasesWrapper();

		BasicSelfCheckoutStation station = new BasicSelfCheckoutStation();
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		AttendantDatabaseWrapper attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.selfCheckoutController = new PaymentCardSelfCheckoutController(
			station, this.cardIssuer, attendantDatabaseWrapper, this.products);
		try {
			this.selfCheckoutController.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("Attendant should be able to log in");
		}
		BasicSelfCheckoutStation.scanOneItemSuccessfully(this.selfCheckoutController, this.products);
		BasicSelfCheckoutStation.bagOneItemSuccessfully(this.selfCheckoutController);
		this.selfCheckoutController.goToOrderPaymentState();

		createPaymentCardPanel();
	}
	
	private void createPaymentCardPanel() {
		new PaymentCardHandler(viewStateManager, this.selfCheckoutController, this.frame);
		this.panel = (JPanel) frame.getContentPane();
	}
	
	@Test
	public void testSwipeButtonPress() {
		JButton swipeButton = (JButton) (panel.getComponents()[12]);
		try {
			  swipeButton.doClick();
		  } catch (ExpectedThrownException e) {
			  assertEquals(e.getMessage(), "SWIPE");
			  return;
		  }
		  fail("Did not call expected controller function");
	}
	
	@Test
	public void testInsertButtonPress() {
		JButton insertButton = (JButton) (panel.getComponents()[13]);
		try {
			insertButton.doClick();
		} catch (ExpectedThrownException e) {
			assertEquals(e.getMessage(), "INSERT");
			return;
		}
		fail("Did not call expected controller function");
	}
	
	@Test
	public void testTapButtonPress() {
		JButton tapButton = (JButton) (panel.getComponents()[14]);
		try {
			tapButton.doClick();
		} catch (ExpectedThrownException e) {
			assertEquals(e.getMessage(), "TAP");
			return;
		}
		fail("Did not call expected controller function");
	}
	
	@Test
	public void testReturnOrderPaymentMenuListener() {
		JButton returnToMenuButton = (JButton) (panel.getComponents()[15]);
		returnToMenuButton.doClick();
		assertEquals(viewStateManager.getState(), ViewStateEnum.ORDER_PAYMENT_MENU);	       
	}
}
