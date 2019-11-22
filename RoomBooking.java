/**
 * A class to represent a room that has been booked in a hotel
 */
public class RoomBooking {
	private Room room;
	private int[] stayed; //stayed[0] = adults staying stayed[1] = children staying
	private boolean breakfastIncluded;
	
	public RoomBooking(Room room, int numAdults, int numChildren) {
		this.room = room;
		this.stayed = new int[2];
		this.stayed[0] = numAdults;
		this.stayed[1] = numChildren;
	}
	
	public Room getRoom() {
		return this.room;
	}
	
	public int[] getOccupancy() {
		return this.stayed;
	}
	
	public boolean isBreakfastIncluded() {
		return breakfastIncluded;
	}

	public void setBreakfastIncluded(boolean breakfastIncluded) {
		this.breakfastIncluded = breakfastIncluded;
	}
}
