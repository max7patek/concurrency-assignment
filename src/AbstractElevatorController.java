import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;


/**
 * An abstract class that defines the backbone of an Elevator Controller.
 * You should extend this class. <br><br>
 * 
 * The run() method, which you will implement, should wait for button presses 
 * and available elevators, dispatching elevators (by calling AbstractElevator's
 *  'hail' method) when possible. <br><br>
 *  
 *  Lock and Conditions are provided.
 * 
 *
 */
public abstract class AbstractElevatorController implements Runnable {

	/**
	 * The elevators in the building.
	 */
	protected Set <AbstractElevator> elevators;
	/**
	 * Holds the floors whose buttons have been pressed.
	 */
	protected Queue <Integer> floorQueue;
	/**
	 * Holds the people who have pressed the buttons.
	 */
	protected Queue <Person> personQueue;
	
	protected Thread thread;
	/**
	 * Used to wait for button presses.
	 */
	protected Simulation.TestingCondition buttonPressed;
	/**
	 * Used to wait for available elevators
	 */
	protected Simulation.TestingCondition elevatorFinished;
	protected ReentrantLock lock;
	
	/**
	 * This is private because the ElevatorController implementation shouldn't
	 * need to store all the buttons, the buttons should notify the controller
	 * independently. This field is only included for the printState method, which
	 * you do not need to worry about.
	 */
	//AbstractButton[] buttons = new AbstractButton[Simulation.FLOORS];
	
	/**
	 * Because this constructor does not take in any parameters, it is considered
	 * the default constructor, so your implementation does not need to wrap it.
	 */
	public AbstractElevatorController() {
		floorQueue = new LinkedBlockingQueue <Integer> ();
		personQueue = new LinkedBlockingQueue <Person> ();
		lock = new ReentrantLock();
		buttonPressed = new Simulation.TestingCondition(lock.newCondition());
		elevatorFinished = new Simulation.TestingCondition(lock.newCondition());
	}
	
	public final void setElevators(Set <AbstractElevator> elevators) {
		this.elevators = elevators;
	}
	
	public final void start() {
		thread = new Thread(this);
		for (AbstractElevator e : elevators)
			e.start();
		thread.start();
	}
	
	/**
	 * Should be called by the Buttons. It is up for the controller's 
	 * run() method to dequeue the floor and person queues. <br> <br>
	 * 
	 * This method is non-final, so you may override it if you like.
	 * 
	 * @param floor the floor that requested the elevator
	 * @param p the person that requested the elevator
	 */
	public void request(int floor, Person p) {
		floorQueue.add(floor);
		personQueue.add(p);

	}
	
	/**
	 * Used to number the printState outputs.
	 */
	private int count = 0;
	/**
	 * Prints the state of the elevator system for debugging. You do not
	 * need to worry about this code
	 */
	public final void printState(AbstractButton[] buttons) {
		
		@SuppressWarnings("unchecked")
		LinkedList<AbstractElevator>[] array = new LinkedList [Simulation.current.numFloors];
		for (int i = 0; i < Simulation.current.numFloors; i++)
			array[i]  = new LinkedList<AbstractElevator>();
		
		int longest = 0;
		for (AbstractElevator e : elevators) {
			array[e.getFloor()].add(e);
			if (array[e.getFloor()].size() > array[longest].size())
				longest = e.getFloor();
		}
		int size = array[longest].toString().length();
		size = size < 25 ? 25 : size;
		
		int width = String.format("| floor %2s: %7s\t%"+size+"s |\n", 
				0+"", 
				buttons[0], 
				array[0]
			).length();
		String bottom = "+";
		for (int i = 0; i < width +1; i++)
			bottom += "-";
		bottom += "+";
		String top = "+----------------frame-"+count;
		for (int i = 0; i < width +1 - ("----------------frame-"+count).length(); i++)
			top += "-";
		top += "+";
		
		System.out.println(top);
		for (int i = Simulation.current.numFloors-1; i >= 0; i--) 
			System.out.printf("| floor %2s: %7s\t%"+size+"s |\n", 
					i+"", 
					buttons[i], 
					array[i]
				);
		System.out.println(bottom + "\n");
		count++;
	}
	
	
	/**
	 * Wait for pressed buttons and available elevators by calling 'await'
	 * on the two corresponding Condition objects. When possible, assign an 
	 * Elevator to a waiting person by calling Elevator's 'hail' method. <br><br>
	 * 
	 * You should not just periodically check for pushed buttons and available
	 * elevators; you must use the conditions' await methods.
	 */
	@Override
	public abstract void run();
	
}