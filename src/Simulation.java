import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.locks.Condition;



/**
 * CS 2110 Concurrency Assignment <br> <br>
 * 
 * Defined in the default package are several abstract classes for the backbone of 
 * an elevator system. You are to extend these abstract classes, implementing 
 * the abstract methods such that all the Persons are delivered safely to their
 * destination floors.<br> <br>
 * 
 * Start by making classes that will extend the abstract classes. Your classes
 * should be called: <br>
 *  - Elevator <br>
 *  - Button <br>
 *  - ElevatorController <br>
 * Add method stubs in your concrete classes for each of the abstract methods, and
 * go from there. <br> <br>
 * 
 * Read the comments in the provided files for instructions, advice, and hints 
 * on specific classes and methods. <br> <br>
 * 
 * We strongly recommend that you implement the Elevator 'start()' method first! 
 * Try running the main method in this file after you do; you should see helpful
 * debug information in your output console. We recommend that you then proceed to 
 * complete the Button class, then the Elevator class, and finally the ElevatorController
 * class. However, this is just a recommendation; feel free to work in whatever order
 * you like, and note that you will have to revisit some methods that you thought were
 * finished. <br><br>
 * 
 * The abstract classes include several public and protected fields. You may 
 * introduce more fields in your concrete implementations if you like, but you should
 * not need to; the provided fields are sufficient. Neglecting the provided fields
 * may cause tests to fail and/or debug information to be incorrect. <br><br>
 * 
 * We also recommend that you test your
 * implementation with various experimental parameters. Make sure your implementation
 * is thread-safe by trying high-speed, stressful parameters, like 'delayMilliseconds' = 5 and
 * 'testDureationSeconds' = 1. Make sure your implementation works with any number of elevators
 * and floors. You must utilize every elevator. <br> <br>
 * 
 * You should not make any changes to this file except for testing purposes. We 
 * will test your submission by running your classes with our version of this
 * file, so any changes you make will not be preserved during grading. Your
 * implementation must work with the provided code. <br><br>
 * 
 * This assignment assumes a simplified elevator system in which each floor only 
 * has a single button (not 'going-up' and 'going-down' buttons). Elevators
 * can only carry one person at a time. We do not expect you to implement the most
 * efficient solution, but your system should respond immediately to requests, and
 * every request must be fulfilled. 
 * 
 * @author Maxwell Patek
 * @version 1.3
 *
 */
public final class Simulation {
	
	private static final Random random = new Random();
	public static final int randInt(int bound) {
		return random.nextInt(bound);
	}
	
	
	public Simulation(int numFloors, int numElevators, 
			int testLengthSeconds, int delayMilliseconds, int pressCount) {
		this.numFloors = numFloors;
		this.numElevators = numElevators;
		this.testLengthSeconds = testLengthSeconds;
		this.delayMilliseconds = delayMilliseconds;
		this.pressCount = pressCount;
	}

	private boolean passed = true;
	public void fail(){
		passed = false;
	}
	
	
	/**
	 * Signal that is set to 'true' if the simulation should halt. All threads'
	 * run() methods should respect this signal by returning if set to 'true'.
	 */
	protected boolean TERMINATE = false;
	
	
	
	
	
	
	// ----- Experimental Parameters ----- \\
	/*
	 * Change these fields to adjust the parameters of the experiment. You might
	 * want to stress test your implementation by setting TEST_LENGTH to 1.
	 */
	/**
	 * Number of floors in the building. Floors are 0-indexed.
	 */
	public final int numFloors;
	/**
	 * Number of Elevators. Elevator 'id's go 'A', 'B', etc.
	 */
	public final int numElevators;
	/**
	 * The number of seconds during which buttons are randomly pressed.
	 * After this time, no buttons will be pressed, and the elevators only
	 * have to clear the queue.
	 */
	public final int testLengthSeconds; // seconds
	/**
	 * Essentially the inverse of the simulation speed. A delay of 1000 ms
	 * means that elevator floors will update once a second (1000 ms). 
	 */
	public final int delayMilliseconds; // milliseconds
	/**
	 * The number of times each button getts pressed and therefore the number
	 * of people spawned per floor.
	 */
	public final int pressCount;

	


	/**
	 * Condition wrapper class that ensures their use.
	 */
	public static final class TestingCondition {
		private Condition cond;
		private boolean awaited =false;
		private boolean signalled =false;
		
		public TestingCondition(Condition cond) {
			this.cond = cond;
		}
		public void await() throws InterruptedException {
			awaited = true;
			cond.await();
		}
		public void signalAll() {
			signalled = true;
			cond.signalAll();
		}
		public void signal() {
			signalled = true;
			cond.signal();
		}
	}
	
	/**
	 * Thread that handles outputting the state of the elevators, buttons, people.
	 *
	 */
	private final class Monitor implements Runnable{
		private Thread thread;
		private AbstractElevatorController control;
		private AbstractButton[] buttons;
		
		public Monitor(AbstractElevatorController control, AbstractButton[] buttons) {
			this.control = control;
			this.buttons = buttons;
			thread = new Thread(this);
			
		}
		
		public void run() {
			while (!TERMINATE) {
				control.printState(buttons);
				try {
					Thread.sleep(delayMilliseconds);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
		
		public void start() {
			thread.start();
		}
		
	}
	
	
	/**
	 * Runs a simulation. Only one simulation should be running at a time (thus the static
	 * 'current' field).
	 * 
	 * @return whether the tests passed
	 */
	private boolean run() {
		current = this;
		// ---- Initialize Test ---- //
		ElevatorController control = new ElevatorController();
		HashSet <AbstractElevator> elevators = new HashSet<AbstractElevator>();
		for (int i = 0; i < numElevators; i++)
			elevators.add(new Elevator(control));
		//HashSet <AbstractButton> b = new HashSet<AbstractButton>();
		AbstractButton[] buttons = new AbstractButton[numFloors];
		for (int i = 0; i < numFloors; i++)
			buttons[i] = new Button(i, control);
		control.setElevators(elevators);


		// ---- Start Test ---- //
		control.start(); // controller starts the elevators
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (!control.thread.isAlive()) // Test that Controller started properly
			throw new TestFailure("Controller Terminated Early.");
		if (!control.thread.getState().equals(Thread.State.WAITING))
			throw new TestFailure("Controller Needs to Call Await on the Conditions.");
		for (AbstractButton b : buttons)
			b.start();
		Monitor monitor = new Monitor(control, buttons);
		monitor.start();

		// ---- Sleep While Buttons are Pressing ---- //
		try {
			Thread.sleep(testLengthSeconds*1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < numElevators+1; i++)
			buttons[0].spawnAndPress(); // ensure that elevatorFinished.await is called


		// ---- Check if Test is Complete ---- //
		while (!TERMINATE) {
			try {
				Thread.sleep(delayMilliseconds); 
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			boolean stillGoing = false;
			for (AbstractElevator e : elevators) 
				if (e.isMoving()) // if no elevator is moving...
					stillGoing = true;
			try {
				Thread.sleep(delayMilliseconds); 
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			for (AbstractElevator e : elevators) 
				if (e.isMoving()) // if no elevator is moving again... stop
					stillGoing = true;
			
			if (!stillGoing) {
				for (AbstractButton b: buttons)
					if (!b.done()) // if stopping, everyone should be delivered
						throw new TestFailure( "Did Not Drop Everyone Off!");
				if (!control.thread.getState().equals(Thread.State.WAITING))
					throw new TestFailure( "Test Ended with Controller in "
							+ "Non-Awaiting State");
				if (!control.buttonPressed.awaited)
					throw new TestFailure( "Did not call await on buttonPressed");
				if (!control.buttonPressed.signalled)
					throw new TestFailure( "Did not call signalAll on buttonPressed");
				if (!control.elevatorFinished.awaited)
					throw new TestFailure( "Did not call await on elevatorFinished");
				if (!control.elevatorFinished.signalled)
					throw new TestFailure( "Did not call signalAll on elevatorFinished");
				TERMINATE = true; // Tell all threads to stop
				control.thread.interrupt(); 
				monitor.thread.interrupt();
			}
		}
		if (passed)
			System.out.println("All tests passed!");
		return passed;
	}

	
	
	
	// ----- Main Method Testing ----- \\
		
	public static Simulation current;
	
	/**
	 * Run this to test your code.<br>
	 * However, we encourage you to write your own tests as well.
	 */
	public static void main(String[] args) {
		Simulation sim = new Simulation(25, 5, 30, 500, 2);
		sim.run();
	}
		
}
