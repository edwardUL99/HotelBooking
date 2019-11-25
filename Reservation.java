import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A class to represent the reservation for a hotel room(s)
 *
 */
public class Reservation {
	private static int lastBookingNumber = 999; //Keeps track of what the last booking number was and sets the next reservation to the one above it. Initialised as 999 so the very first reservation will have 1000 as its number
	
	private int number;
	private String name;
	private String type;
	private LocalDate checkinDate;
	private int numberOfNights;
	private int numberOfPeople;
	private int numberOfRooms;
	private ArrayList<RoomBooking> rooms;
	private Bill totalCost;
	private Bill deposit;
	
	/**
	 * Creates a Reservation object
	 * @param name the reservation name
	 * @param type the type of booking S/AP
	 * @param checkinDate the date of check-in
	 * @param numberOfNights the number of nights the reservation is for
	 * @param numberOfPeople the number of people staying in the reservation
	 * @param numberOfRooms the number of rooms to book
	 * @param rooms the rooms to add to the reservation
	 */
	public Reservation(String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfPeople, int numberOfRooms, ArrayList<RoomBooking> rooms) {
		this.number = ++lastBookingNumber;
		this.name = name;
		this.type = type;
		this.checkinDate = checkinDate;
		this.numberOfNights = numberOfNights;
		this.numberOfPeople = numberOfPeople;
		this.numberOfRooms = numberOfRooms;
		this.rooms = rooms;
		this.totalCost = new Bill("Total Cost", checkinDate); //For both bills choose a more suitable date
		this.deposit = new Bill("Deposit", checkinDate, 75.00); //May change the price later
	}
	
	/**
	 * Constructor used when you want to override the default booking number generation
	 * @param number the booking number
	 * @param name the name to have on the reservation
	 * @param type the type of the reservation
	 * @param checkinDate the checkin date
	 * @param numberOfNights the number of nights
	 * @param numberOfPeople the number of peple for this reservation
	 * @param numberOfRooms the number of rooms
	 * @param rooms the list of rooms booked
	 */
	public Reservation(int number, String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfPeople, int numberOfRooms, ArrayList<RoomBooking> rooms) {
		this.setNumber(number);
		this.name = name;
		this.type = type;
		this.checkinDate = checkinDate;
		this.numberOfNights = numberOfNights;
		this.numberOfPeople = numberOfPeople;
		this.numberOfRooms = numberOfRooms;
		this.rooms = rooms;
		this.totalCost = new Bill("Total Cost", checkinDate);
		this.deposit = new Bill("Deposit", checkinDate, 75.00);
	}

	/**
	 * 
	 * @return the reservation number
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Sets the last booking number used to update the new booking number
	 * @param lastBookingNumber the last booking number you want to subsequent reservation to take
	 */
	public static void setLastBookingNumber(int lastBookingNumber) {
		Reservation.lastBookingNumber = lastBookingNumber;
	}
	
	/**
	 * Gets the last booking number 
	 * @return last booking number
	 */
	public static int getLastBookingNumber() {
		return lastBookingNumber;
	}
	
	/**
	 * Sets the new reservation number, particularly useful for when reading in a new reservation from data in a file
	 * @param number the reservation number
	 */
	public void setNumber(int number) {
		this.number = number;
		lastBookingNumber++;
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
	 * Sets the number of people for this reservation
	 * @param numberOfPeople the number of people
	 */
	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}
	
	/**
	 * Gets the number of people booked on this reservation
	 * @return the number of people
	 */
	public int getNumberOfPeople() {
		return this.numberOfPeople;
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
	 * Returns the list of all the rooms booked
	 * @return a list of all rooms booked for this reservation
	 */
	public ArrayList<RoomBooking> getRooms() {
		return rooms;
	}
	
	/**
	 * Calculates the checkout date by adding on the number of nights staying to the check in date
	 * @return the check out date for this reservation
	 */
	public LocalDate getCheckoutDate() {
		return this.checkinDate.plusDays(this.numberOfNights);
	}
	
	/**
	 * Sets the total cost bill amount due to the amount specified
	 * @param amount the amount to be paid
	 */
	public void setTotalCost(double amount) {
		this.totalCost.setAmountDue(amount);
	}
	
	/**
	 * Sets the deposit in the deposit bill associated with this object to the amount specified
	 * @param amount the amount to set the deposit to
	 */
	public void setDeposit(double amount) {
		this.deposit.setAmountDue(amount);
	}
	
	/**
	 * Returns the current Bill TotalCost without calculating the amount to pay for each room for each night
	 * @return the total cost bill
	 */
	public Bill getTotalCost() {
		return this.totalCost;
	}
	
	/**
	 * Calculates the cost of breakfast for this reservation
	 * @return the cost of breakfast calculated per night per person
	 */
	public double calculateBreakfastCost() {
		double breakfastCost = 14.00;
		double calculated = 0.00;
		
		for (RoomBooking rb : this.rooms) {
			for (int i = 0; i < this.numberOfNights; i++) {
				if (rb.isBreakfastIncluded()) {
					calculated += breakfastCost * this.numberOfPeople;
				}
			}
		}
		
		return calculated;
	}
	
	/**
	 * Returns a Bill object with the amount due set to the total payable calculated using rates including deposit
	 * Also sets the amount due of the bill total cost associated with this object to this calculated cost, so if you want to get the current total cost, either not calculated yet or discounted yet,
	 * see getTotalCost()
	 * @return a Bill object representing the total cost
	 */
	public Bill getTotalCostCalculated() {
		int dayOfWeek;
		double total = 0.00;
		for (int i = 0; i < this.numberOfNights; i++) {
			dayOfWeek = this.checkinDate.plusDays(i).getDayOfWeek().getValue() - 1; //Our rates array from room is indexed from 0 to 6 and getDayOfWeek().getValue() returns a number 1-7
			for (RoomBooking rb : this.rooms) {
				Room r = rb.getRoom();
				total += r.getRate(dayOfWeek);
			}
		}
		if (this.type.equals("AP")) {
			total -= total * 0.05;
		}
		total += this.getDeposit().getAmountDue(); //Maybe after checkout, return the deposit???
		total += this.calculateBreakfastCost();
		this.totalCost.setAmountDue(total);
		return this.totalCost;
	}

	/**
	 * Returns a Bill object with the amount due set to \u20ac75
	 * @return a Bill object representing the deposit
	 */
	public Bill getDeposit() {
		return this.deposit;
	}
	
	/**
	 * Overriding the equals method of Object 
	 * @param obj the object to check against
	 */
	@Override
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
		for (RoomBooking rb : this.rooms) {
			Room r = rb.getRoom();
			returned += r + " Breakfast Included: " + rb.isBreakfastIncluded() + "\n";
		}
		return returned;
	}
	
	/**
	 * Formats the reservation into a readable line by line 
	 * @return formatted reservation 
	 */
	public String format() {
		return String.format("Reservation name: %s\nReservation type: %s\nResrvation number: %d\nCheck-in Date: %s\nNumber of nights: %d\nNumber of rooms: %d\nRooms Booked:\n%sTotal Cost (incl. deposit): \u20ac%.02f\nDeposit: \u20ac%.02f", this.name, this.type, this.number, this.checkinDate.toString(), this.numberOfNights, this.numberOfRooms, roomsBookedAsString(), this.getTotalCost().getAmountDue(), this.deposit.getAmountDue());
	}
	
	/**
	 * Returns a string representation of this reservation object
	 * @return string representation
	 */
	@Override
	public String toString() {
		return String.format("Reservation name: %s, Type: %s, Number: %d, Checkin Date: %s, Number of nights: %d, Number of rooms: %d", this.name, this.type, this.number, this.checkinDate.toString(), this.numberOfNights, this.numberOfRooms);
	}
	
}
