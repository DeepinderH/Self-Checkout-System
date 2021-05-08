package selfcheckout.software.controllers;

/**
 * An enum representing all possible states of the controller.
 */
public enum ControllerStateEnum {
	ATTENDANT_ACCESS,
	DISABLED,
	ITEM_ADDITION,
	ORDER_PAYMENT,
	FINISHED_PAYMENT // For printing receipts.
}
