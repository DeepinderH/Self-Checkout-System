package selfcheckout.software.views;

public enum ViewStateEnum {
	// attendant empty the banknote storage unit
	ATTENDANT_EMPTY_BANKNOTES,
	// attendant empty the coin storage unit
	ATTENDANT_EMPTY_COINS,
	// attendant login required, after which it will go to the appropriate
	// state based on the previous customer/attendant action
	ATTENDANT_LOGIN,
	//Logging out as an Attendant
	ATTENDANT_LOGOUT,
	// menu for attendant
	ATTENDANT_MENU,
	// attendant looks up product by description
	ATTENDANT_PRODUCT_LOOKUP,
	// attendant adds ink to the receipt printer
	ATTENDANT_RECEIPT_INK_ADDITION,
	// attendant adds paper to the receipt printer
	ATTENDANT_RECEIPT_PAPER_ADDITION,
	// attendant refills the banknote storage unit
	ATTENDANT_REFILL_BANKNOTES,
	// attendant refills the coin storage unit
	ATTENDANT_REFILL_COINS,
	// approve a weight discrepancy for an item (different weight and/or
	// skipping bagging)
	ATTENDANT_WEIGHT_DISCREPANCY_APPROVAL,
	// add customer's bags to bagging area
	BAG_ADDITION,
	// pay with banknote
	BANKNOTE_PAYMENT,
	// pay with coin
	COIN_PAYMENT,
	// customer looks up product by description
	CUSTOMER_PRODUCT_LOOKUP,
	// station has been blocked/disabled
	DISABLED,
	// exit application
	EXIT_APPLICATION,
	// print the receipt, ask customer to remove bags,
	// and reset for new purchase
	FINISH_PURCHASE,
	// swipe gift card to pay for order
	GIFT_CARD_SWIPE,
	// select whether or not to bag an item
	ITEM_BAGGING,
	// remove an item from the order
	ITEM_REMOVAL,
	// scan an item with a barcode
	ITEM_SCANNING,
	// menu where customer can add to order or adjust order
	MAIN_MENU,
	// swipe a membership card
	MEMBERSHIP_CARD,
	// show options to pay for order
	ORDER_PAYMENT_MENU,
	// select whether to insert, swipe, or tap a debit/credit card
	PAYMENT_CARD_MENU,
	// change from item addition state to order payment state
	PAYMENT_START,
	// enter an item with a PLU code
	PLU_ITEM_INPUT,
	// print a receipt
	PRINT_RECEIPT,
	// request attendant assistance, wait for them to log in
	REQUEST_ATTENDANT_ASSISTANCE,
	// skip bagging an item with attendant permission
	SKIP_BAGGING_ITEM,
	// start up the station
	// similar to the DISABLED state
	STATION_STARTUP
}
