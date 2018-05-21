
/**
 * This Exception should be thrown if a busy elevator is hailed. <br><br>
 * 
 * Note that it extends Exception, which means it is checked.
 */
public class OccupiedException extends Exception {
	private static final long serialVersionUID = -3388816891645794348L;

	public OccupiedException(String id) {
		super(id);
	}
}