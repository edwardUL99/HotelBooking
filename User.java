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
	
	/**
	 * Notifies the BookingSystem to add the reservation to the system
	 * @param hotelName the name of the hotel the booking is for
	 * @param name the name of the customer making the reservation
	 * @param type the type of reservation (S/AP)
	 * @param checkinDate the check in date
	 * @param numberOfNights the number of nights staying in the hotel
	 * @param numberOfRooms the number of rooms to reserve
	 * @param rooms the list of rooms booked
	 */
	//Here maybe have it boolean to return if booking was successful or not
	public void createReservation(String hotelName, String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfRooms, ArrayList<Room> rooms) {
		this.system.addReservation(hotelName, new Reservation(name, type, checkinDate, numberOfNights, numberOfRooms, rooms));
	}
	
	/**
	 * Notifies the booking system that the reservation was cancelled and to remove it from the system
	 * @param hotelName the name of the hotel
	 * @param reservation the reservation to remove
	 */
	public void cancelReservation(String hotelName, Reservation reservation) {
		if (!reservation.getCheckinDate().equals(LocalDate.now().minusDays((long)1)) && reservation.getType().equals("S")) {
			reservation.setTotalCost(0.00);
			reservation.setDeposit(0.00); //assume this is what's meant by refund? i.e. total paid
		}
		this.system.removeReservation(hotelName, reservation, true);
	}
		
}