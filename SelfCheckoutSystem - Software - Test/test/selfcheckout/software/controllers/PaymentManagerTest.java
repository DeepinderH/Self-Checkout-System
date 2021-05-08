package selfcheckout.software.controllers;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PaymentManagerTest {

	PaymentManager paymentManager;

	@Before
	public void setUp() {
		this.paymentManager = new PaymentManager();
	}

	@Test
	public void initializeZero() {
		assertEquals(BigDecimal.ZERO.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}

	@Test
	public void testAddPaymentSingle() {
		BigDecimal amount = new BigDecimal("12.34");
		this.paymentManager.addPayment(amount);
		assertEquals(amount.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}

	@Test
	public void testAddPaymentMultiple() {
		this.paymentManager.addPayment(new BigDecimal("12.34"));
		this.paymentManager.addPayment(new BigDecimal("34.56"));
		this.paymentManager.addPayment(new BigDecimal("56.78"));
		BigDecimal expectedAmount = new BigDecimal("103.68");
		assertEquals(expectedAmount.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}

	@Test
	public void testResetPayment() {
		BigDecimal amount = new BigDecimal("12.34");
		this.paymentManager.addPayment(amount);
		this.paymentManager.resetPayment();
		assertEquals(BigDecimal.ZERO.compareTo(this.paymentManager.getCurrentPaymentTotal()), 0);
	}
}
