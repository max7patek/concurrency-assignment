import java.util.concurrent.locks.ReentrantLock;


/**
 * An abstract class defining the backbone of any Elevator.
 * You should extend this class. Make sure to use the provided fields. <br><br>
 * 
 * run() is implemented for you, and it will call the methods that you
 * will implement. <br>
 * Lock provided
 * 
 */
public abstract class AbstractElevator implements Runnable {
	
	private static char ID = 'A';
	
	protected ReentrantLock lock = new ReentrantLock();
	protected Thread thread;
	protected AbstractElevatorController control;
	
	private int floor;
	/**
	 * The elevator will move towards this floor, so make sure to set it appropriately
	 * in your concrete methods.
	 */
	protected int targetFloor;
	public final char id;
	/**
	 * The passenger field should be set upon being hailed. Make the passenger
	 * board the elevator when appropriate by calling 'board' on the field.
	 * Make the passenger get off the elevator by calling 'exit', then forget about
	 * the passenger by setting the field to null.
	 */
	protected Person passenger;
	/**
	 * Field to allow the elevator to know whether it has picked up its passenger yet.
	 * The field is used in the provided toString method, which is in turn used in
	 * the printState method, so we recommend that you set this field appropriately.
	 */
	protected boolean carrying;

	/**
	 * Note that when extending this class, you will have to wrap this constructor.<br>
	 * Something like this:	
	 * <pre>
	 * 	public Elevator(Provided.AbstractElevatorController c) {
	 *		super(c);
	 *	}
	 * </pre>
	 * Same goes for the other classes you are extending.
	 * 
	 * @param control the controller for this elevator
	 * 
	 */
	public AbstractElevator(AbstractElevatorController control) {
		this.control = control;
		this.targetFloor = this.floor = 0;
		carrying = false;
		passenger = null;
		id = ID;
		ID++;
	}
	
	public final int getFloor() {
		return floor;
	}
	public final boolean isMoving() {
		return floor != targetFloor;
	}
	
	/**
	 * This run() method will:
	 * <ol>
	 *  <li> move the elevator up or down towards the targetFloor. 
	 *  <li> check if shouldPickUp()?
	 *  	if yes, it will call pickUp()
	 *  <li> check if shouldOffload()?
	 *  	if yes, it will call offload()
	 *  <li> repeat
	 *  </ol>
	 */
	@Override
	public final void run() {
		if (ran)
			throw new TestFailure("Elevator run() was called twice.");
		ran = true;
		int oldFloor = floor;
		while (!Simulation.current.TERMINATE) {
			try {
				lock.lock();
				validateFloor(oldFloor);
				//if (id == 'A')
				//	control.printState();

				if (floor != targetFloor) { // move the elevator 1 floor
					floor += (floor < targetFloor) ? 1 : -1;
				}

				if (shouldPickUp())
					pickUp();
				else if (shouldOffload())
					offload();

				oldFloor = floor;
			} finally {
				lock.unlock();
			}
			delay();
		}
	} private boolean ran = false;
	
	private final void validateFloor(int oldFloor) {
		if (floor != oldFloor) {
			throw new TestFailure("Elevators should "
					+ "only be moved by the provided code. "
					+ "Do not change 'floor'.");
		}
	}
	
	private final void delay() {
		try {
			Thread.sleep(Simulation.current.delayMilliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public final String toString() {
		return id + "(" + (carrying ? passenger : "") + ")->"+targetFloor;
	}
	
	/**
	 * Should start the elevator moving in the direction of 'floor'.
	 * The Person that pressed the button and hailed the elevator is passed
	 * in as Person 'p' and should be saved in the passenger field.
	 * 
	 * @param floor where the elevator should go
	 * @param p the person that hailed the elevator
	 * @throws OccupiedException if hailed but not available
	 * 
	 */
	public abstract void hail(int floor, Person p) throws OccupiedException;
	
	/**
	 * Construct and start the thread running this elevator. <br>
	 * You must the provided 'thread' field. <br> <br>
	 * 
	 * Note: <br>
	 * We recommend you write this method first, since it will make 
	 * the other methods easier to visualize.
	 */
	public abstract void start();
	
	/**
	 * @return true if the elevator is available to be hailed
	 */
	public abstract boolean isAvailable();
	
	/**
	 * Make sure to call 'p's board method.
	 * Elevator should start moving towards passenger's destination.
	 */
	protected abstract void pickUp();
	
	/**
	 * @return true if the elevator should open its doors and pick up 'passenger'
	 */
	protected abstract boolean shouldPickUp();
	
	/**
	 * Make sure to call the passenger's exit method
	 */
	protected abstract void offload();
	
	/**
	 * @return true if the elevator can offload its passenger
	 */
	protected abstract boolean shouldOffload();
	
}