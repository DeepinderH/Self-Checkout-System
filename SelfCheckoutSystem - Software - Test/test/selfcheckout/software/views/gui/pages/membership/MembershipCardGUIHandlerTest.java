package selfcheckout.software.views.gui.pages.membership;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.external.CardIssuer;

import selfcheckout.software.controllers.*;
import selfcheckout.software.controllers.exceptions.IncorrectAttendantLoginInformationException;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

public class MembershipCardGUIHandlerTest {
	
	private SelfCheckoutController scc;
	private ViewStateManager viewStateManager;
	private JFrame frame;
	private JPanel membershipPanel;
	private AttendantDatabaseWrapper attendantDatabaseWrapper;
	
	private class MembershipSelfCheckoutController extends SelfCheckoutController {
		public MembershipSelfCheckoutController() {
			super(new BasicSelfCheckoutStation(),
				null, 
				new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME),
				null, null,
				attendantDatabaseWrapper);
		}

		@Override
		public void processMembershipCard(String cardNumber) {
			throw new ExpectedThrownException("PROCESS_CARD");
		}

		@Override
		public void processMembershipNumber(String membershipNumber) {
			throw new ExpectedThrownException("PROCESS_NUMBER");
		}
	}

	@Before
	public void setup() {
		AttendantDatabase attendantDatabase = new AttendantDatabase();
		this.attendantDatabaseWrapper = new AttendantDatabaseWrapper(attendantDatabase);
		this.attendantDatabaseWrapper.initializeAccountLoginInformationDatabase();
		this.viewStateManager = new ViewStateManager(ViewStateEnum.MEMBERSHIP_CARD);
		this.frame = new JFrame();
		this.scc = new MembershipSelfCheckoutController();
		try {
			this.scc.getAttendantConsoleController().unblockStation(
				AttendantConsoleConstant.VALID_ATTENDANT_ID,
				AttendantConsoleConstant.VALID_ATTENDANT_PASSWORD);
		} catch (IncorrectAttendantLoginInformationException e) {
			fail("should be able to login");
		}
		createMembershipPanel();
	}

	private void createMembershipPanel() {
		new MembershipCardGUIHandler(this.viewStateManager, this.scc, this.frame);
		this.membershipPanel = (JPanel) frame.getContentPane();
	}

	@Test
	public void testReturnToMainMenuButtonPress() {
		JButton mainMenuButton = (JButton) (membershipPanel.getComponents()[4]);
		mainMenuButton.doClick();
		assertEquals(this.viewStateManager.getState(), ViewStateEnum.MAIN_MENU);
	}
	
	@Test 
	public void testSwipeMembershipCardButton() {
		JButton membershipCardButton = (JButton) (membershipPanel.getComponents()[2]);
		try {
			membershipCardButton.doClick();
		} catch (ExpectedThrownException e) {
			assertEquals(e.getMessage(), "PROCESS_CARD");
			return;
		}
		fail("Expected exception was not thrown");
	}

	@Test
	public void testEnterMembershipNumberButton() {
		JButton membershipCardButton = (JButton) (membershipPanel.getComponents()[3]);
		try {
			membershipCardButton.doClick();
		} catch (ExpectedThrownException e) {
			assertEquals(e.getMessage(), "PROCESS_NUMBER");
			return;
		}
		fail("Expected exception was not thrown");
	}
}
