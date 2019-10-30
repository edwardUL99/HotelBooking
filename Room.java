/**
 * A class to represent a room of a hotel
 *
 */
public class Room {
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
	 * 
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
	 * 
	 * @return the room type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 
	 * @param type the type of the room
	 */
	public void setType(String type) {
		this.type = type;
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
	 * Sets the occupancy for the room
	 * @param adultMin the minimum adult occupancy for the room
	 * @param childMin the minimum child occupancy
	 * @param adultMax the maximum adult occupancy
	 * @param childMax the minimum child occupancy
	 */
	public void setOccupancy(int adultMin, int childMin, int adultMax, int childMax) {
		this.occupancy[0] = adultMin;
		this.occupancy[1] = childMin;
		this.occupancy[2] = adultMax;
		this.occupancy[3] = childMax;
	}
	
	/**
	 * rates[0] = Monday rates, rates[1] = Tuesday rates, rates[2] = Wednesday rates ... etc.
	 * @return the array of room rates
	 */
	public int[] getRates() {
		return this.rates;
	}
	
	/**
	 * Sets the rates array with a new array of rates if and only if the length of the new rates array is 7
	 * @param rates the new rates for the room
	 */
	public void setRates(int[] rates) {
		if (rates.length == 7) {
			this.rates = rates;
		}
	}
	
	/**
	 * Sets a specific rate for a particular day
	 * @param day 0 = Monday, 1 = Tuesday, 2 = Wednesday, 3 = Thursday, 4 = Friday, 5 = Saturday, 6 = Sunday
	 * @param rate the new rate for the day chosen
	 */
	public void setSingleRate(int day, int rate) {
		if (day >= 0 && day < 7) {
			this.rates[day] = rate;
		}
	}
	




	/** 



	* Returns the rate for the specified day 



	* @param day 0 = Monday, 1 = Tuesday, 2 = Wednesday etc 



	* @return the rate for the day specified 



	**/ 



	public int getRate(int day) { 
		if (day >= 0 && day < 7) { 
			return this.rates[day]; 
		} 
		return -1; 
	} 
}
