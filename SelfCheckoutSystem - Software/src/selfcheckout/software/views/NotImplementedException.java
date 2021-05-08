package selfcheckout.software.views;

public class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotImplementedException() {
		super("This method is not implemented");
	}
}
