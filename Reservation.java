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
	public Reservation(String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfRooms) {
		this.number = (int)(Math.random() * 1000 + 9999);
		this.name = name;
		this.type = type;
		this.checkinDate = checkinDate;
		this.numberOfNights = numberOfNights;
		this.numberOfRooms = numberOfRooms;
		this.chooseRooms();
	}

	/**
	 * 
	 * @return the reservation number
	 */
	public int getNumber() {
		return number;
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
	
	private void chooseRooms() {
		//Implement to read available rooms from a file and then use a text interface for the user to chose the rooms they want
	}
	
	/**
	 * 
	 * @return a Bill object representing the total cost
	 */
	public Bill getTotalCost() {
		return totalCost;
	}

	/**
	 * 
	 * @return a Bill object representing the deposit
	 */
	public Bill getDeposit() {
		return deposit;
	}
}
