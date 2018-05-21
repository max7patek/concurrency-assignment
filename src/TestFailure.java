
/**
 * A TestFailure will be thrown if an elevator or the elevator control
 * attempts an illegal operation or fails to meet requirements. <br><br>
 * 
 * Note that TestFailure extends RuntimeException, so it is unchecked.
 */
public final class TestFailure extends RuntimeException {
	private static final long serialVersionUID = 7177872724706310332L;

	public TestFailure(String id) {
		super(id);
		Simulation.current.TERMINATE = true;
		Simulation.current.fail();
	}
}