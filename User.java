import java.time.LocalDate;
import java.util.ArrayList;

/**
* A class representing the user of the system
*/
public class User  {
	protected String name;
	protected BookingSystem system;//The system the User is on
	protected String hotelName;
	
	/**
	 * Constructs a user object
	 * @param system the system for which this user is working on
	 */
	public User(String hotelName ,BookingSystem system) {
		this.system = system;
		this.hotelName = hotelName;
	}
	
	/** allows users to create reservations */
	//Here maybe have it boolean to return if booking was successful or not
	public void createReservation(String hotelName, String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfRooms, ArrayList<Room> rooms) {
		this.system.addReservation(hotelName, new Reservation(name, type, checkinDate, numberOfNights, numberOfRooms, rooms));
	}
	
	/** allows users to cancel reservations */
	public void cancelReservation(String hotelName, Reservation reservation) {
		this.system.removeReservation(hotelName, reservation);
	}
		
}
