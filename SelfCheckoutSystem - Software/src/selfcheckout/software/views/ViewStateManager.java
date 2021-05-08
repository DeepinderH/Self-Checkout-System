package selfcheckout.software.views;

/**
 * A class which just holds a ViewStateEnum value for implementations of the
 * SelfCheckoutView
 */
public class ViewStateManager {

	ViewStateEnum state;

	public ViewStateManager(ViewStateEnum initialState) {
		this.state = initialState;
	}

	public void setState(ViewStateEnum newState) {
		this.state = newState;
	}

	public ViewStateEnum getState() {
		return state;
	}
}
