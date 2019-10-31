/**
* A class representing the Supervisor user of the system
*/
public class Supervisor extends DeskClerk {
	private String hotelName;

	public Supervisor(String hotelName, BookingSystem system) {
		super(hotelName, system);
	}
	
	/** allows supervisor to apply any discount to any reservation */
	public void applyDiscount(double discount, int reservationNumber ) {
		
		for(int i = 0; i < this.system.getReservations().get(hotelName).size(); i++) {
			
			if(this.system.getReservations().get(hotelName).get(i).getNumber() == reservationNumber) {
				this.system.getReservations().get(hotelName).get(i).getTotalCost().setAmountDue(this.system.getReservations().get(hotelName).get(i).getTotalCost().getAmountDue()*(1 - discount));
			}
		}
	}
	
	
	//methods requesting data Analysis
}
