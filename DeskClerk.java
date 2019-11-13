/**
* A class representing the Desk Clerk user of the system
*/
public class DeskClerk extends User {
	
	public DeskClerk(String hotelName, BookingSystem system) {
		super(hotelName, system);
	}
	
//check in check out methods
// check if customer has arrived	
	/** checks in users */
	public void checkIn(String name) {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getDeposit().setAmountDue(0);
		//this.system.getReservation(this.hotelName, name, checkIn).getTotalCost().setAmountDue(this.system.getReservation(name, name, checkIn).getTotalCost().getAmountDue() - this.system.getReservation(this.hotelName, name, checkIn).getDeposit().getAmountDue());
	}
	
// check if room is available again
	/** checks out users */
	public void checkOut() {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getTotalCost().setAmountDue(0);
	}
	
	/**
	 * Removing a reservation is different from a cancellation, removing is for after checkout and they need to save space for example
	 * @param hotelName the name of the hotel
	 * @param reservation the reservation
	 */
	public void removeReservation(String hotelName, Reservation reservation) {
		this.system.removeReservation(hotelName, reservation, false); 
	}
	
}
