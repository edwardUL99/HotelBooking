import java.time.LocalDate;

/**
* A class representing the Supervisor user of the system
*/
public class Supervisor extends DeskClerk {

	public Supervisor(String hotelName, BookingSystem system) {
		super(hotelName, system);
	}
	
	/** allows supervisor to apply any discount to any reservation */
	public void applyDiscount(double discount, int reservationNumber ) {
		
		for(int i = 0; i < this.system.getReservations().get(hotelName).size(); i++) {
			Reservation r = this.system.getReservations().get(hotelName).get(i);
			if(r.getNumber() == reservationNumber) {
				r.getTotalCost().setAmountDue(r.getTotalCost().getAmountDue()*(1 - discount));
				break;
			}
		}
	}
	
	
	//methods requesting data Analysis
	//gets data analysis of financial information between two dates
	public void getFinancialInfo(LocalDate start, LocalDate end) {

	}
	
	//gets data analysis of what rooms are occupied between two dates
	public void getOccupancyInfo(LocalDateTime start, LocalDateTime end) {
		
	}
}
