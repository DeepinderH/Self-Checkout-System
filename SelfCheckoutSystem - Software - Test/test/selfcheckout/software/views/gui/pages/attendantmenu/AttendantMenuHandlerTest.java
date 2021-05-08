package selfcheckout.software.views.gui.pages.attendantmenu;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.external.CardIssuer;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.Product;
import selfcheckout.software.controllers.*;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class AttendantMenuHandlerTest {

	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel attendantMenuPanel;

	@Before
	public void setup() {
		this.viewStateManager = new ViewStateManager(ViewStateEnum.ATTENDANT_MENU);
		this.frame = new JFrame();
		this.scc = new AttendantMenuSelfCheckoutController();
		createAttendantMenuPanel();
	}

	private void createAttendantMenuPanel() {
		new AttendantMenuHandler(this.viewStateManager, this.scc, this.frame);
		this.attendantMenuPanel = (JPanel) (this.frame.getContentPane());
	}

	@Test
	public void testLogoutButtonPress() {
		final boolean[] changedControllerState = {false};
		this.scc = new AttendantMenuSelfCheckoutController() {
			@Override
			public void goToItemAdditionState() {
				changedControllerState[0] = true;
			}
		};
		createAttendantMenuPanel();
		JButton logoutButton = (JButton) (attendantMenuPanel.getComponents()[0]);
		logoutButton.doClick();
		assertTrue(changedControllerState[0]);
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.MAIN_MENU);
	}

	@Test
	public void checkNoRemoveItemButton() {
		// when no items in order, start order payment button should not appear
		Component possibleRemoveItemButton = this.attendantMenuPanel.getComponents()[2];
		assertFalse(possibleRemoveItemButton instanceof JButton);
	}

	@Test
	public void handleRemoveItemPaymentButtonPress() {
		this.scc = new AttendantMenuSelfCheckoutController() {
			@Override
			public ArrayList<Product> getCurrentProducts() {
				ArrayList<Product> products = new ArrayList<>();
				products.add(
					new BarcodedProduct(
						ControllerTestConstants.VALID_BARCODE,
						"product", new BigDecimal("10.00")));
				return products;
			}
		};
		createAttendantMenuPanel();
		JButton removeItemPaymentButton = (JButton) (attendantMenuPanel.getComponents()[2]);
		removeItemPaymentButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ITEM_REMOVAL);
	}

	@Test
	public void testLookupProductButtonPress() {
		JButton lookupProductButton = (JButton) (attendantMenuPanel.getComponents()[3]);
		lookupProductButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_PRODUCT_LOOKUP);
	}

	@Test
	public void testRefillReceiptPaperButtonPress() {
		JButton refillReceiptPaperButton = (JButton) (attendantMenuPanel.getComponents()[4]);
		refillReceiptPaperButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_RECEIPT_PAPER_ADDITION);
	}

	@Test
	public void testRefillReceiptInkButtonPress() {
		JButton refillReceiptInkButton = (JButton) (attendantMenuPanel.getComponents()[5]);
		refillReceiptInkButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_RECEIPT_INK_ADDITION);
	}

	@Test
	public void testEmptyCoinsButtonPress() {
		JButton emptyCoinsButton = (JButton) (attendantMenuPanel.getComponents()[6]);
		try {
			emptyCoinsButton.doClick();
		} catch (ExpectedThrownException e) {
			assertEquals(e.getMessage(), COIN_STRING);
			assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_MENU);
			return;
		}
		fail("should have thrown an ExpectedThrownException");
	}

	@Test
	public void testEmptyBanknotesButtonPress() {
		JButton emptyBanknotesButton = (JButton) (attendantMenuPanel.getComponents()[7]);
		try {
			emptyBanknotesButton.doClick();
		} catch (ExpectedThrownException e) {
			assertEquals(e.getMessage(), BANKNOTE_STRING);
			assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_MENU);
			return;
		}
		fail("should have thrown an ExpectedThrownException");
	}

	@Test
	public void testRefillStationCoinsButtonPress() {
		JButton refillCoinsButton = (JButton) (attendantMenuPanel.getComponents()[8]);
		refillCoinsButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_REFILL_COINS);
	}

	@Test
	public void testRefillStationBanknotesButtonPress() {
		JButton refillBanknotesButton = (JButton) (attendantMenuPanel.getComponents()[9]);
		refillBanknotesButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.ATTENDANT_REFILL_BANKNOTES);
	}

	@Test
	public void handleBlockStationButtonPress() {
		final boolean[] blockStationHit = {false};
		this.scc = new AttendantMenuSelfCheckoutController() {

			@Override
			public AttendantConsoleController getAttendantConsoleController() {
				return new AttendantConsoleController(
						new BasicSelfCheckoutStation(), null, null,
						null, null
					) {
					@Override
					public void blockStation() {
						blockStationHit[0] = true;
					}
				};
			}
		};
		createAttendantMenuPanel();
		JButton removeItemPaymentButton = (JButton) (attendantMenuPanel.getComponents()[10]);
		removeItemPaymentButton.doClick();
		assertTrue(blockStationHit[0]);
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.DISABLED);
	}

	@Test
	public void handleShutdownStationButtonPress() {
		final boolean[] shutdownStationHit = {false};
		this.scc = new AttendantMenuSelfCheckoutController() {

			@Override
			public AttendantConsoleController getAttendantConsoleController() {
				return new AttendantConsoleController(
					new BasicSelfCheckoutStation(), null, null,
					null, null
				) {
					@Override
					public void shutdownStation() {
						shutdownStationHit[0] = true;
					}
				};
			}
		};
		createAttendantMenuPanel();
		JButton removeItemPaymentButton = (JButton) (attendantMenuPanel.getComponents()[11]);
		removeItemPaymentButton.doClick();
		assertTrue(shutdownStationHit[0]);
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.EXIT_APPLICATION);
	}

	private static final String BANKNOTE_STRING = "banknote";
	private static final String COIN_STRING = "coin";

	private static class AttendantMenuSelfCheckoutController extends SelfCheckoutController {

		public AttendantMenuSelfCheckoutController() {
			super(new BasicSelfCheckoutStation(),
				null,
				new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME),
				null, null, null);

		}

		@Override
		public AttendantConsoleController getAttendantConsoleController() {
			return new AttendantConsoleController(
				new BasicSelfCheckoutStation(), null, null,
				new ControllerStateManager(ControllerStateEnum.ATTENDANT_ACCESS),
				null) {

				@Override
				public BigDecimal emptyBanknoteStorageUnit() {
					throw new ExpectedThrownException(BANKNOTE_STRING);
				}

				@Override
				public BigDecimal emptyCoinStorageUnit() {
					throw new ExpectedThrownException(COIN_STRING);
				}
			};
		}
	}
}
