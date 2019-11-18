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
	
	public Reservation getReservation() {
		return this.reservation;
	}
	
	public boolean isCheckedIn() {
		return this.checkedIn;
	}
	
	public boolean isCheckedOut() {
		return !this.checkedIn;
	}
	
	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}
	
	public Bill getTotalIncome() {
		return this.totalIncome;
	}
	
	public void setTotalIncome(double income) {
		this.totalIncome.setAmountDue(income);
	}
	
	public LocalDate getStayStart() {
		return this.stayStart;
	}
	
	public LocalDate getStayEnd() {
		return this.stayEnd;
	}
	
	public void setStayStart(LocalDate stayStart) {
		this.stayStart = stayStart;
	}
	
	public void setStayEnd(LocalDate stayEnd) {
		this.stayEnd = stayEnd;
	}
	
	@Override
	public String toString() {
		return String.format("%s Checked In: %b, Total Income: €%.02f, Stay from %s to %s", this.reservation.toString(), this.checkedIn, this.totalIncome.getAmountDue(), this.stayStart.toString(), this.stayEnd.toString());
	}
	
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
			return this.reservation.equals(comp.reservation) && this.checkedIn == comp.checkedIn; //If reservations are the same no need to check stayStart or endStart as these would be set witht he same dates
		}
	}
}
