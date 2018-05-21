

/**
 * Class used for testing effectiveness of Elevators.
 * You do not need to worry about this code. However, you do need to call
 * 'board()', 'exit()', and 'getDestination()' in Elevator. You must
 * also pass the Person objects from the Button 'press' method to the controller
 * through the request method, and finally to the elevator through the 'hail' 
 * method. This is the only way to pass the tests.
 */
public final class Person {
	private int startingFloor;
	private int destination;
	private AbstractElevator elevator;
	private boolean done = false;
	public boolean waiting() {
		return elevator == null;
	}
	public boolean isDone() {
		return done;
	}
	
	
	public Person(int startingFloor) {
		this.startingFloor = startingFloor;
		this.destination = Simulation.randInt(Simulation.current.numFloors);
	}
	/**
	 * @return the floor that this person wants to go to.
	 */
	public int getDestination() {
		return destination;
	}
	/**
	 * Call this method to make the person get on the elevator.
	 * 
	 * @param e the Elevator that this Person is boarding.
	 */
	public void board(AbstractElevator e) {
		if (elevator != null) 
			throw new TestFailure("Passenger " + startingFloor + " is Already "
					+ "on an Elevator");
		if (e.getFloor() != startingFloor)
			throw new TestFailure("Cannot Pick Up Passenger " + startingFloor 
					+" from Floor " + e.getFloor() +".");
		elevator = e;
	}
	/**
	 * Call this method to make the person get off the elevator
	 */
	public void exit() {
		//System.out.println(startingFloor +" dropped off at " + elevator.getFloor());
		if (elevator == null) 
			throw new TestFailure("Passenger " + startingFloor + " has not Boarded "
					+ "and Cannot Exit.");
		if (elevator.getFloor() != destination)
			throw new TestFailure("Passenger " + startingFloor + "'s Destination is "
					+destination+". He/She Cannot be Dropped Off at " 
					+ elevator.getFloor() +".");
		done = true;
	}
	@Override
	public String toString() {
		return "" + startingFloor;
	}
}