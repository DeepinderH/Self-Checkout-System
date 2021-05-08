package selfcheckout.software.controllers;

/**
 * A class which simply holds a ControllerStateEnum representing
 * the state of the controller.
 */
public class ControllerStateManager {

	private ControllerStateEnum state;

	public ControllerStateManager(ControllerStateEnum initialState) {
		this.state = initialState;
	}

	public void setState(ControllerStateEnum newState) {
		this.state = newState;
	}

	public ControllerStateEnum getState() {
		return this.state;
	}
}
