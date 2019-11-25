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
	 * @param reservation the reservation to check in
	 * @return whether the check in was successful, false if the reservation doesn't exist
	 */
	public boolean checkIn(Reservation reservation) {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getDeposit().setAmountDue(0);
		//this.system.getReservation(this.hotelName, name, checkIn).getTotalCost().setAmountDue(this.system.getReservation(name, name, checkIn).getTotalCost().getAmountDue() - this.system.getReservation(this.hotelName, name, checkIn).getDeposit().getAmountDue());
		if (reservation == null) {
			return false;
		} else {
			if (!this.system.isStayed(this.hotelName, reservation)) { //if reservation is already a stay no point checking in
				HotelStay stay = new HotelStay(reservation); //will be added to an arraylist in BookingSystem 
				reservation.getTotalCostCalculated(); //Updates their bill with total cost
				this.system.addHotelStay(this.hotelName, stay);
				System.out.println("Your deposit payable is:\n" + reservation.getDeposit());
				return true;
			}
			//possibly write to file here
			return false;
		}
	}
	
// check if room is available again
	/**
	 * Checks users out of the hotel
	 * @param reservation the reservation to checkout
	 * @return whether the check out was successful, false if the reservation doesn't exist or it's not even checked in
	 */
	public boolean checkOut(Reservation reservation) {
		//LocalDate checkIn = null;
		//this.system.getReservation(name, name, checkIn).getTotalCost().setAmountDue(0);
		//may have to check if there's any stays gone past checkout and should be removed
		HotelStay stay = this.system.getHotelStay(this.hotelName, reservation);
		if (stay == null) {
			return false;
		} else {
			System.out.println("Your bill(incl. deposit) is:\n" + reservation.getTotalCost());
			reservation.getTotalCost().setBilledDate(reservation.getCheckoutDate()); //set the billable date as the checkout
			Bill totalExclDeposit = reservation.getTotalCost();
			totalExclDeposit.setAmountDue(reservation.getTotalCost().getAmountDue() - 75);
			System.out.println("Your bill(excl. deposit) is:\n" + totalExclDeposit);
			stay.setCheckedIn(false);
			stay.setStayStart(reservation.getCheckinDate()); //possibly check here if these are valid dates
			stay.setStayEnd(reservation.getCheckoutDate());
			this.system.updateFiles("Reservation"); 
			this.system.updateFiles("Stays");
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
