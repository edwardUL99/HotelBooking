import java.time.LocalDate;

/**
* A class representing the user of the system
*/
public class User  {
	protected BookingSystem system; //The system the User is on
	
	/**
	 * Constructs a user object
	 * @param system the system for which this user is working on
	 */
	public User(BookingSystem system) {
		this.system = system;
	}
	
	/** allows users to create reservations */
	public void createReservation(String hotelName, String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfRooms) {
		this.system.addReservation(hotelName, new Reservation(name, type, checkinDate, numberOfNights, numberOfRooms));
	}
	
	/** allows users to cancel reservations */
	public void cancelReservation(String hotelName, Reservation reservation) {
		this.system.removeReservation(hotelName, reservation);
	}
		
}
