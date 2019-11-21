import java.time.LocalDate;

/**
* A class representing the Desk Clerk user of the system
*/
public class DeskClerk extends User {
	
	public DeskClerk(String hotelName, BookingSystem system) {
		super(hotelName, system);
	}
	
//check in check out methods
// check if customer has arrived	
	/**
	 * Checks users into the hotel
	 * @param customerName the name of the customer who made the reservation
	 * @param checkIn the check in date
	 * @return whether the check in was succesful, false if the reservation doesn't exist
	 */
	public boolean checkIn(String customerName, LocalDate checkIn) {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getDeposit().setAmountDue(0);
		//this.system.getReservation(this.hotelName, name, checkIn).getTotalCost().setAmountDue(this.system.getReservation(name, name, checkIn).getTotalCost().getAmountDue() - this.system.getReservation(this.hotelName, name, checkIn).getDeposit().getAmountDue());
		Reservation r = this.system.getReservation(this.hotelName, customerName, checkIn);
		if (r == null) {
			return false;
		} else {
			r.getTotalCostCalculated(); //Updates their bill with total cost
			HotelStay stay = new HotelStay(r); //will be added to an arraylist in BookingSystem 
			this.system.addHotelStay(this.hotelName, stay);
			return true;
			//possibly write to file here
		}
	}
	
// check if room is available again
	/**
	 * Checks users out of the hotel
	 * @param customerName the name of the customer who made the reservation
	 * @param checkIn the check in date
	 * @param checkOut the checkout date
	 * @return whether the check out was successful, false if the reservation doesn't exist or it's not even checked in
	 */
	public boolean checkOut(String customerName, LocalDate checkIn, LocalDate checkOut) {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getTotalCost().setAmountDue(0);
		//may have to check if there's any stays gone past checkout and should be removed
		Reservation r = this.system.getReservation(this.hotelName, customerName, checkIn);
		HotelStay stay = this.system.getHotelStay(this.hotelName, r);
		if (stay == null) {
			return false;
		} else {
			stay.setCheckedIn(false);
			stay.setStayStart(checkIn); //possibly check here if these are valid dates
			stay.setStayEnd(checkOut);
			this.system.writeReservationsToFile(true, true);
			return true;
		}
		//possibly trigger write to file in system here to update the checkIn status
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
