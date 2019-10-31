import java.time.LocalDate;
import java.util.ArrayList;

/**
* A class representing the user of the system
*/
public class User  {

	private ArrayList<Reservation> reservations; //tree map to link reservation number to reservations? 
	
	/** allows users to create reservations */
	public void createReservation(String name, String type, LocalDate checkinDate, int numberOfNights, int numberOfRooms) {
		reservations.add(new Reservation(name, type, checkinDate, numberOfNights, numberOfRooms));
	}
	
	/** allows users to cancel reservations */
	public void cancelReservation() {
		//reservations.remove();
	}
		
}
