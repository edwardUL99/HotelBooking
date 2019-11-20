import java.time.LocalDate;

/**
* A class representing the Supervisor user of the system
*/
public class Supervisor extends DeskClerk {
	
	DataAnalysis A = new DataAnalysis();
	
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
	public void getFinancialDataAnalysis(LocalDate start, LocalDate end, String filePath) {
		
		A.getFinancialInfo(start, end, this.system.readDataFromFile(filePath));
	}
	
	public double getAverageRoomCost(LocalDate start, LocalDate end, Object[][] data ) {
		return A.getAverageCostPerRoom(start, end, data);
	}
	
	public double getTotalEarnedAmount(LocalDate start, LocalDate end, Object[][] data) {
		return A.getTotalEarned(start, end, data);
	}
	
	//gets data analysis of what rooms are occupied between two dates
	public void getOccupancyInfo(LocalDate start, LocalDate end) {
		
	}
}
