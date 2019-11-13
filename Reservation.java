import java.time.LocalDate;
import java.util.ArrayList;
/**
 * A class to represent the reservation for a hotel room(s)
 *
 */
public class Reservation {
	private int number;
	private String name;
	private String type;
	private LocalDate checkinDate;
	private int numberOfNights;
	private int numberOfRooms;
	private ArrayList<Room> rooms;
	private Bill totalCost;
	private Bill deposit;
	
	/**
	 * 
	 * @param name the reservation name
	 * @param type the type of booking S/AP
	 * @param checkinDate the date of check-in
	 * @param numberOfNights the number of nights the reservation is for
	 * @param numberOfRooms the number of rooms to book
	 */
	public Reservation(String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfRooms, ArrayList<Room> rooms) {
		this.number = (int)(Math.random() * 8999 + 1000);
		this.name = name;
		this.type = type;
		this.checkinDate = checkinDate;
		this.numberOfNights = numberOfNights;
		this.numberOfRooms = numberOfRooms;
		this.rooms = rooms;
		this.totalCost = new Bill("Total Cost", LocalDate.now()); //For both bills choose a more suitable date
		this.deposit = new Bill("Deposit", LocalDate.now(), 75.00); //May change the price later
	}

	/**
	 * 
	 * @return the reservation number
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Sets the new reservation number, particularly useful for when reading in a new reservation from data in a file
	 * @param number the reservation number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	
	/**
	 * @return the reservation name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return the reservation type (S/AP)
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 
	 * @return the check-in date for this reservation
	 */
	public LocalDate getCheckinDate() {
		return checkinDate;
	}
	
	/**
	 * 
	 * @return the number of nights for the reservation
	 */
	public int getNumberOfNights() {
		return numberOfNights;
	}

	/**
	 * 
	 * @return the number of rooms booked 
	 */
	public int getNumberOfRooms() {
		return numberOfRooms;
	}
	
	/**
	 * 
	 * @return a list of all rooms booked for this reservation
	 */
	public ArrayList<Room> getRooms() {
		return rooms;
	}
	
	public LocalDate getCheckoutDate() {
		return this.checkinDate.plusDays((long)this.numberOfNights);
	}
	
	public void setTotalCost(double amount) {
		this.totalCost.setAmountDue(amount);
	}
	
	public void setDeposit(double amount) {
		this.deposit.setAmountDue(amount);
	}
	
	public Bill getTotalCost() {
		return this.totalCost;
	}
	
	/**
	 * Returns a Bill object with the amount due set to the total payable calculated using rates including deposit
	 * @return a Bill object representing the total cost
	 */
	public Bill getTotalCostCalculated() {
		int dayOfWeek;
		double total = 0.00;
		for (int i = 0; i < this.numberOfNights; i++) {
			dayOfWeek  = this.checkinDate.plusDays((long)i).getDayOfWeek().getValue() - 1; //Our rates array from room is indexed from 0 to 6 and getDayOfWeek().getValue() returns a number 1-7
			for (Room r : this.rooms) {
				total += r.getRate(dayOfWeek);
			}
		}
		if (this.type.equals("AP")) {
			total -= total * 0.05;
		}
		total += this.getDeposit().getAmountDue(); //Maybe after checkout, return the deposit???
		this.totalCost.setAmountDue(total);
		return this.totalCost;
	}

	/**
	 * Returns a Bill object with the amount due set to €75
	 * @return a Bill object representing the deposit
	 */
	public Bill getDeposit() {
		return this.deposit;
	}
	
	/**
	 * Overwriting the equals method of Object 
	 * @param the object to check against
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Reservation)) {
			//if obj is not an instance of Reservation, it is definitely not equal
			return false;
		} else if (this == obj) {
			//if the reference of obj is equal to this reference, they're the same object
			return true;
		} else if (this.hashCode() == obj.hashCode()) {
			//if the hashCodes match, they are equal objects
			return true;
		} else {
			Reservation comp = (Reservation)obj;
			return comp.name.equals(this.name) && this.number == comp.number && this.type.equals(comp.type) 
				 && this.checkinDate.equals(comp.checkinDate) && this.numberOfNights == comp.numberOfNights 
				 && this.numberOfRooms == comp.numberOfRooms; //This is enough to check equality for now
		}
	}
	
	private String roomsBookedAsString() {
		String returned = "";
		for (Room r : this.rooms) {
			returned += r + "\n";
		}
		return returned;
	}
	
	/**
	 * Formats the reservation into a readable line by line 
	 * @return formatted reservation 
	 */
	public String format() {
		return String.format("Reservation name: %s\nReservation type: %s\nResrvation number: %d\nCheck-in Date: %s\nNumber of nights: %d\nNumber of rooms: %d\nRooms Booked:\n%sTotal Cost (incl. deposit): €%.02f\nDeposit: €%.02f", this.name, this.type, this.number, this.checkinDate.toString(), this.numberOfNights, this.numberOfRooms, roomsBookedAsString(), this.getTotalCost().getAmountDue(), this.deposit.getAmountDue());
	}
	
	public String toString() {
		return String.format("Reservation name: %s, Type: %s, Number: %d, Checkin Date: %s, Number of nights: %d, Number of rooms: %d", this.name, this.type, this.number, this.checkinDate.toString(), this.numberOfNights, this.numberOfRooms);
	}
}
