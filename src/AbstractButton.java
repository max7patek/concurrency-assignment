import java.util.ArrayList;

/**
 * This abstract class mostly defines the button that is on each floor. 
 * There is only a single abstract method, 'press()' which is called when
 * the person on the floor with this button presses the button.
 *
 */
public abstract class AbstractButton implements Runnable {
	
	public final int floor;
	private Thread thread;
	protected AbstractElevatorController control;
	private ArrayList<Person> p;
	//private boolean done = false;
	
	/**
	 * Note that when extending this class, you will have to wrap this constructor.<br>
	 * 
	 * Something like this:
	 * <pre>
	 * 	public Button(int floor, Provided.AbstractElevatorController control) {
	 *		super(floor, control);
	 *	}
	 *</pre>
	 *
	 * @param floor the floor the button is on
	 * @param control the controller for this button
	 * 
	 */
	public AbstractButton(int floor, AbstractElevatorController control) {
		this.floor = floor;
		this.control = control;
		//control.buttons[floor] = this;
		//p = new Person(floor);
		p = new ArrayList<Person>();
	}
	public final void start() {
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public final void run() {
		for (int i = 0; i < Simulation.current.pressCount; i++) {
			try {
				Thread.sleep(Simulation.randInt(Simulation.current.testLengthSeconds*1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new TestFailure("Button was Interrupted");
			}
			spawnAndPress();
		}
	}
	
	public void spawnAndPress() {
		Person p = new Person(floor);
		this.p.add(p);
		press(p);
	}
	
	@Override
	public final String toString() {
		return waiting() ? "waiting" : "";
	}
	
	public boolean waiting() {
		for (Person p : p) {
			if (p.waiting())
				return true;
		}
		return false;
	}
	
	public boolean done() {
		for (Person p : p) {
			if (!p.isDone())
				return false;
		}
		return true;
	}
	
	/**
	 * Called when the button is pressed. Make sure to notify the controller
	 * by calling request() <br><br>
	 * 
	 * The Person parameter is constructed and passed in by the provided code
	 * and is used to verify that your system works, so you must make sure not
	 * to loose the reference to it. Here, it should be passed to the controller
	 * through the request method, then it should then be passed by the controller's 
	 * run() method from the controller to the Elevator through the hail method. 
	 * This way, the Elevators can call 'board' and 'exit' on the provided Person 
	 * objects, thereby passing the tests.
	 * 
	 * @param p, the person that pressed the button.
	 */
	protected abstract void press(Person p);
	
	
}