/**
 * A class to represent a room that has been booked in a hotel
 */
public class RoomBooking {
	private Room room;
	private int[] stayed; //stayed[0] = adults staying stayed[1] = children staying
	private boolean breakfastIncluded;
	
	/**
	 * Creates a RoomBooking object
	 * @param room the room this booking is based on
	 * @param numAdults the number of adults staying in the room
	 * @param numChildren the number of children staying in the room
	 */
	public RoomBooking(Room room, int numAdults, int numChildren) {
		this.room = room;
		this.stayed = new int[2];
		this.stayed[0] = numAdults;
		this.stayed[1] = numChildren;
	}
	
	/**
	 * Gets the room object from the booking
	 * @return the room booked
	 */
	public Room getRoom() {
		return this.room;
	}
	
	/**
	 * Gets the adult and child occupancy of the room
	 * @return the array representing occupancy, stayed[0] = adult occupancy, stayed[1] = child occupancy
	 */
	public int[] getOccupancy() {
		return this.stayed;
	}
	
	/**
	 * Checks if breakfast was included with this room booking
	 * @return true if breakfast is included
	 */
	public boolean isBreakfastIncluded() {
		return breakfastIncluded;
	}

	/**
	 * Sets breakfast included
	 * @param breakfastIncluded true if breakfast is to be included
	 */
	public void setBreakfastIncluded(boolean breakfastIncluded) {
		this.breakfastIncluded = breakfastIncluded;
	}
}
