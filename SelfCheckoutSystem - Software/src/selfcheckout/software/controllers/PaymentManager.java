package selfcheckout.software.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * The PaymentManager class holds the current state of the customer's payment
 */
public class PaymentManager {

	private BigDecimal currentPaymentTotal = new BigDecimal("0.00");

	private ArrayList<BigDecimal> previousPayments = new ArrayList<>();

	public PaymentManager() {}

	/**
	 * Add an amount to the total payment so far
	 * @param amountAdded the amount added to the total
	 */
	public void addPayment(BigDecimal amountAdded) {
		currentPaymentTotal = currentPaymentTotal.add(amountAdded);
	}

	/**
	 * Reset payment for a new order
	 */
	public void resetPayment() {
		this.previousPayments.add(this.currentPaymentTotal);
		this.currentPaymentTotal = new BigDecimal("0.00");
	}

	public BigDecimal getCurrentPaymentTotal() {
		return currentPaymentTotal;
	}

	public BigDecimal getPreviousPaymentTotal() {
		return this.previousPayments.get(this.previousPayments.size() - 1);
	}
}
