package selfcheckout.software.views.gui.pages.bagaddition;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.external.CardIssuer;
import selfcheckout.software.controllers.BasicSelfCheckoutStation;
import selfcheckout.software.controllers.ControllerTestConstants;
import selfcheckout.software.controllers.SelfCheckoutController;
import selfcheckout.software.views.ViewStateEnum;
import selfcheckout.software.views.ViewStateManager;
import selfcheckout.software.views.gui.pages.ExpectedThrownException;

import javax.swing.*;

import static org.junit.Assert.*;

public class CustomerAddsBagsHandlerTest {
    private SelfCheckoutController scc;
    private ViewStateManager viewStateManager;
    private JFrame frame;
    private CustomerAddsOwnBagsPanel addingBagsPanel;


    @Before
    public void setUp() {
        this.viewStateManager = new ViewStateManager(ViewStateEnum.BAG_ADDITION);
        this.frame = new JFrame();
        this.scc = new BagAdditionController();
        createAddingBagsPanel();
    }

    private void createAddingBagsPanel() {
        new CustomerAddsOwnBagsHandler(this.viewStateManager, this.scc, this.frame);
        this.addingBagsPanel = (CustomerAddsOwnBagsPanel) frame.getContentPane();
    }

    @Test
    public void pressEnterButton() {
        JButton enterButton = (JButton) addingBagsPanel.getComponents()[2];
        JTextField weightTextField = (JTextField) (addingBagsPanel.getComponents()[1]);
        weightTextField.setText("2");
        try {
            enterButton.doClick();
        } catch (ExpectedThrownException e) {
            assertEquals(e.getMessage(), ADD_BAGS_STRING);
            assertEquals(this.viewStateManager.getState(), ViewStateEnum.BAG_ADDITION);
            return;
        }
        fail("Should have thrown an exception");
    }

    @Test
    public void testGetWeight() {
        JTextField weightTextField = (JTextField) (addingBagsPanel.getComponents()[1]);
        weightTextField.setText("2");
        assertEquals(this.addingBagsPanel.getWeight(), "2");
        assertEquals(this.viewStateManager.getState(), ViewStateEnum.BAG_ADDITION);
    }

    private static final String ADD_BAGS_STRING = "Add bags";

    private static class BagAdditionController extends SelfCheckoutController {
        public BagAdditionController() {
            super(new BasicSelfCheckoutStation(), null,
                new CardIssuer(ControllerTestConstants.CARD_ISSUER_NAME),
                null, null, null);
        }

        @Override
        public void addBags(Double weight) {
            throw new ExpectedThrownException(ADD_BAGS_STRING);
        }
    }
}
