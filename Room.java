/**
 * A class to represent a room of a hotel
 *
 */
public class Room implements Comparable<Room> {
	private String type;
	private int[] occupancy; //occupancy[0] = Adult min occupancy[1] = child min, occupancy[2] = adult max, occupancy[3] = child max
	private int[] rates; //indexed 0 - 6 0 = Mon, 1 = Tues, 2 = Wed etc
	
	/**
	 * No-arg constructor which creates a default room object
	 */
	public Room() {
		this(null, new int[4], new int[7]);
	}
	
	/**
	 * Constructor which creates a room object with given type occupancy and rates.
	 * @param type The room type e.g. Deluxe Double
	 * @param occupancy the occupancy in the form occupancy[0] = adult minimum, occupancy[1] = child minimum, occupancy[2] = adult maximum, occupancy[3] = child maximum
	 * @param rates the rates with index 0 representing monday rates, index 1 representing tuesday and so on
	 */
	public Room(String type, int[] occupancy, int[] rates) {
		this.type = type;
		this.occupancy = occupancy;
		this.rates = rates;
	}
	
	/**
	 * gets the type of the room
	 * @return the room type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Returns the single occupancy value determined by the parameters
	 * @param adult if adult is true it will return adult occupancy and if false, child occupancy
	 * @param min if min is true, returns minimum, false if maximum
	 * @return the single occupancy 
	 */
	public int occupancy(boolean adult, boolean min) {
		if (adult) {
			if (min) {
				return this.occupancy[0];
			} else {
				return this.occupancy[2];
			}
		} else {
			if (min) {
				return this.occupancy[1];
			} else {
				return this.occupancy[3];
			}
		}
	}
	
	/**
	 * rates[0] = Monday rates, rates[1] = Tuesday rates, rates[2] = Wednesday rates ... etc.
	 * @return the array of room rates
	 */
	public int[] getRates() {
		return this.rates;
	}

	/**
	* Returns the rate for the specified day
	* @param day 0 = Monday, 1 = Tuesday, 2 = Wednesday etc
	* @return the rate for the day specified
	*/
	public int getRate(int day) {
		if (day >= 0 && day < 7) {
			return this.rates[day];
		}
		return -1;
	}
	
	/**
	* Provides the capability for rooms to be compared
	* @param other the room to compare to
	* @return the int value representing the comparison**/
	@Override
	public int compareTo(Room other) {
		return this.type.compareTo(other.type);
	}
	
	/**
	 * Returns a string representation of this room object
	 * @return string representation
	 */
	@Override
	public String toString() {
		return this.type;
	}
	
	/**
	 * Overriding object equals method
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Room)) {
			return false;
		}
		
		if (this == obj) {
			return true;
		} else if (this.hashCode() == obj.hashCode()) {
			return true;
		} else {
			Room comp = (Room)obj;
			return comp.type.equals(this.type);
		}
	}
}
