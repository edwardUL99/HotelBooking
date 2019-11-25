import java.time.LocalDate;

/**
 * A class to represent a reservation that turned into a hotel stay 
 */
public class HotelStay {
	private Reservation reservation;
	private boolean checkedIn; //default is true as this object is always created on check in
	private Bill totalIncome; //The total income this stay brought in
	private LocalDate stayStart, stayEnd;
	
	/**
	 * Creates a new HotelStay object setting the reservation that has been processed to a stay and if it is checked in or not
	 * @param reservation the reservation to check in
	 */
	public HotelStay(Reservation reservation) {
		this.reservation = reservation;
		this.checkedIn = true;
		this.totalIncome = this.reservation.getTotalCost();
		this.stayStart = this.reservation.getCheckinDate();
		this.stayEnd = this.reservation.getCheckoutDate();
	}
	
	/**
	 * Gets the reservation of this hotel stay
	 * @return the reservation of this hotel stay.
	 */
	public Reservation getReservation() {
		return this.reservation;
	}
	
	/**
	 * checks if the guests have checked in
	 * @return True if the guests have checked in
	 */
	public boolean isCheckedIn() {
		return this.checkedIn;
	}
	
	/**
	 * checks if the guests have checked out/not checked in
	 * @return True if gets are checked out
	 */
	public boolean isCheckedOut() {
		return !this.checkedIn;
	}
	
	/**
	 * Sets guests to checked In or not
	 * @param checkedIn boolean decides if guest is checked in or not
	 */
	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}
	
	/**
	 * Gets the Bill for total income
	 * @return Bill for total income from hotel stay
	 */
	public Bill getTotalIncome() {
		return this.totalIncome;
	}
	
	/**
	 * Sets income from hotel stay
	 * @param income double amount of income from hotel stay
	 */
	public void setTotalIncome(double income) {
		this.totalIncome.setAmountDue(income);
	}
	
	/**
	 * gets start date of hotel stay
	 * @return LocalDate Start date of hotel stay
	 */
	public LocalDate getStayStart() {
		return this.stayStart;
	}
	
	/**
	 * gets end date of hotel stay
	 * @return LocalDate End date of hotel stay
	 */
	public LocalDate getStayEnd() {
		return this.stayEnd;
	}
	
	/**
	 * sets start date of hotel stay
	 * @param stayStart LocalDate hotel stay start date
	 */
	public void setStayStart(LocalDate stayStart) {
		this.stayStart = stayStart;
	}
	
	/**
	 * sets end date of hotel stay
	 * @param stayEnd LoccalDate hotel stay end date
	 */
	public void setStayEnd(LocalDate stayEnd) {
		this.stayEnd = stayEnd;
	}

	/**
	 * checks if two hotel stays are the same
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HotelStay)) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj.hashCode() == this.hashCode()) {
			return true;
		} else {
			HotelStay comp = (HotelStay)obj;
			return this.reservation.equals(comp.reservation);//If reservations are the same no need to check stayStart or endStart as these would be set with the same dates. Don't check if checkIn are equals because this can change from true to false if they checkin or checkout and still be the same hotel stay
		}
	}
}
