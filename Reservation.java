import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
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
		this.rooms = new ArrayList<Room>(numberOfRooms);
		this.totalCost = new Bill("Total Cost", LocalDate.now());
		this.deposit = new Bill("Deposit", LocalDate.now());
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
		Scanner in = new Scanner(System.in);
		TreeMap<String, TreeMap<Room, Integer>> map = new BookingSystem().getRooms(); //for now, have it an anonymous variable but may change
		System.out.println("Please choose your hotel: ");
		char c = 'A';
		ArrayList<String> hotels = new ArrayList<String>();
		for (String s : map.keySet()) {
			hotels.add(s);
			System.out.println(c + ")" + s);
			c++;
		}
		String choice = in.nextLine();
		ArrayList<Room> allRooms = new ArrayList<Room>(map.get(hotels.get(choice.charAt(0) - 'A')).keySet());
		while (this.rooms.size() != this.numberOfRooms) {
			char ch = 'A';
			for (Room r : allRooms) {
				System.out.println(ch + ")" + r.getType());
				ch++;
			} //This is not how it will be implemented, just until the other methods are developed
			String input = in.nextLine();
			int n = input.toUpperCase().charAt(0) - 'A';
			if (n >= 0 && n < allRooms.size()) {
				this.rooms.add(allRooms.get(n));
			}
		}
		in.close();
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
}
