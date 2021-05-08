package selfcheckout.software.views.gui.pages.mainmenu;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.Product;
import selfcheckout.software.controllers.*;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;

import javax.swing.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MainMenuHandlerTest {

	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel mainMenuPanel;

	@Before
	public void setup() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.MAIN_MENU);
		this.frame = new JFrame();
		this.scc = new MainMenuSelfCheckoutController();
		createMainMenuPanel();
	}

	private void createMainMenuPanel() {
		new MainMenuHandler(this.viewStateManager, this.scc, this.frame);
		JPanel selfCheckoutPanel = (JPanel) frame.getContentPane();
		this.mainMenuPanel = (JPanel) (selfCheckoutPanel.getComponents()[1]);
	}

	@Test
	public void checkNoOrderPaymentButtonPress() {
		// when no items in order, start order payment button should not appear
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[0]);
		assertEquals(menuButtons.getComponents().length, 1);
	}

	@Test
	public void handleStartOrderPaymentButtonPress() {
		this.scc = new MainMenuSelfCheckoutController() {
			@Override
			public ArrayList<Product> getCurrentProducts() {
				ArrayList<Product> products = new ArrayList<>();
				products.add(new BarcodedProduct(ControllerTestConstants.VALID_BARCODE, "product", new BigDecimal("10.00")));
				return products;
			}
		};
		createMainMenuPanel();
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[0]);
		JButton startOrderPaymentButton = (JButton) (menuButtons.getComponents()[1]);
		startOrderPaymentButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.PAYMENT_START);
	}

	@Test
	public void testScanItemButtonPress() {
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[1]);
		JButton scanItemButton = (JButton) (menuButtons.getComponents()[0]);
		scanItemButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ITEM_SCANNING);
	}

	@Test
	public void testInputPLUItemButtonPress() {
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[1]);
		JButton inputPLUItemButton = (JButton) (menuButtons.getComponents()[1]);
		inputPLUItemButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.PLU_ITEM_INPUT);
	}

	@Test
	public void testAddOwnBagsButtonPress() {
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[1]);
		JButton addOwnBagsButton = (JButton) (menuButtons.getComponents()[2]);
		addOwnBagsButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.BAG_ADDITION);
	}

	@Test
	public void testSwipeMembershipCardButtonPress() {
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[1]);
		JButton swipeMembershipCardButton = (JButton) (menuButtons.getComponents()[3]);
		swipeMembershipCardButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.MEMBERSHIP_CARD);
	}

	@Test
	public void testRequestAttendantAssistanceButtonPress() {
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[1]);
		JButton requestAttendantAssistanceButton = (JButton) (menuButtons.getComponents()[4]);
		requestAttendantAssistanceButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.REQUEST_ATTENDANT_ASSISTANCE);
	}

	@Test
	public void testAttendantMenuButtonPress() {
		JPanel menuButtons = (JPanel) (this.mainMenuPanel.getComponents()[1]);
		JButton attendantMenuButton = (JButton) (menuButtons.getComponents()[5]);
		attendantMenuButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_LOGIN);
	}

	private static class MainMenuSelfCheckoutController extends SelfCheckoutController {
		public MainMenuSelfCheckoutController() {
			super(new BasicSelfCheckoutStation(),
				null,
				new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME),
				null, null, null);
		}
	}
}
