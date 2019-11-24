import java.time.LocalDate;

/**
* A class representing the Desk Clerk user of the system
*/
public class DeskClerk extends User {
	
	/**
	 * Creates a DeskClerk object 
	 * @param hotelName the name of the hotel the DeskClerk is "working at"
	 * @param system the name of the system the DeskClerk is working on
	 */
	public DeskClerk(String hotelName, BookingSystem system) {
		super(hotelName, system);
	}
		
	/**
	 * Checks users into the hotel
	 * @param customerName the name of the customer who made the reservation
	 * @param checkIn the check in date
	 * @param number  the booking number
	 * @return whether the check in was succesfull, false if the reservation doesn't exist
	 */
	public boolean checkIn(String customerName, LocalDate checkIn, int number) {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getDeposit().setAmountDue(0);
		//this.system.getReservation(this.hotelName, name, checkIn).getTotalCost().setAmountDue(this.system.getReservation(name, name, checkIn).getTotalCost().getAmountDue() - this.system.getReservation(this.hotelName, name, checkIn).getDeposit().getAmountDue());
		Reservation r = this.system.getReservation(this.hotelName, customerName, checkIn, number);
		if (r == null) {
			return false;
		} else {
			if (!this.system.isStayed(this.hotelName, r)) { //if reservation is already a stay no point checking in
				r.getTotalCostCalculated(); //Updates their bill with total cost
				HotelStay stay = new HotelStay(r); //will be added to an arraylist in BookingSystem 
				this.system.addHotelStay(this.hotelName, stay);
				return true;
			}
			//possibly write to file here
			return false;
		}
	}
	
// check if room is available again
	/**
	 * Checks users out of the hotel
	 * @param customerName the name of the customer who made the reservation
	 * @param checkIn the check in date
	 * @param checkOut the checkout date
	 * @param number the booking number
	 * @return whether the check out was successful, false if the reservation doesn't exist or it's not even checked in
	 */
	public boolean checkOut(String customerName, LocalDate checkIn, LocalDate checkOut, int number) {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getTotalCost().setAmountDue(0);
		//may have to check if there's any stays gone past checkout and should be removed
		Reservation r = this.system.getReservation(this.hotelName, customerName, checkIn, number);
		HotelStay stay = this.system.getHotelStay(this.hotelName, r);
		if (stay == null) {
			return false;
		} else {
			stay.setCheckedIn(false);
			stay.setStayStart(checkIn); //possibly check here if these are valid dates
			//May just check the reservation checkin date against the LocalDate.now() and if they're not equal they're not allowed checkin? 
			stay.setStayEnd(checkOut);
			this.system.writeReservationsToFile(true, true); //write to the stays file
			return true;
		}
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
